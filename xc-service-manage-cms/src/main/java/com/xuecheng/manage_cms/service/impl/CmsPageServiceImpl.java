package com.xuecheng.manage_cms.service.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import com.xuecheng.manage_cms.service.CmsPageService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author WangPan
 * @date 2020/6/28 15:25
 */
@Service
public class CmsPageServiceImpl implements CmsPageService {
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 页面查询
     *
     * @param page             页码
     * @param size             每页大小
     * @param queryPageRequest 查询条件
     * @return
     */
    @Override
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        // 请求参数处理
        queryPageRequest = queryPageRequest == null ? new QueryPageRequest() : queryPageRequest;
        page = page <= 0 ? 1 : page;
        page = page - 1;
        size = size <= 0 ? 10 : size;
        // 分页处理
        Pageable pageable = PageRequest.of(page, size);
        // 定义条件查询器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        // 设置查询条件
        CmsPage cmsPage = new CmsPage();
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId()))
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase()))
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId()))
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        // 定义条件对象 Example
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        // 查询数据
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        // 封装结果并返回
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    // 新增页面
    @Override
    public CmsPageResult add(CmsPage cmsPage) {
        if (cmsPage == null) {
            // 抛出非法参数异常
        }
        // 校验页面唯一性
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());

        if (cmsPage1 != null) {
            // 抛出页面已经存在异常
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        cmsPage.setPageId(null);
        CmsPage save = cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, save);

    }

    // 根据id查询页面
    @Override
    public CmsPage getById(String id) {
        Optional<CmsPage> opt = cmsPageRepository.findById(id);
        return opt.orElse(null);
    }

    // 编辑页面
    @Override
    public CmsPageResult edit(String id, CmsPage cmsPage) {
        //根据id从数据库查询页面信息
        CmsPage one = this.getById(id);
        if (one != null) {
            one.setTemplateId(cmsPage.getTemplateId());
            one.setSiteId(cmsPage.getSiteId());
            one.setPageAliase(cmsPage.getPageAliase());
            one.setPageName(cmsPage.getPageName());
            one.setPageWebPath(cmsPage.getPageWebPath());
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            one.setDataUrl(cmsPage.getDataUrl());
            CmsPage save = cmsPageRepository.save(one);
            return new CmsPageResult(CommonCode.SUCCESS, save);
        }
        //修改失败
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    @Override
    public ResponseResult delete(String id) {
        Optional<CmsPage> opt = cmsPageRepository.findById(id);
        if (opt.isPresent()) {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    // 页面发布
    @Override
    public ResponseResult post(String pageId) {
        // 执行页面静态化
        String pageHtml = getPageHtml(pageId);
        // 将页面静态化文件保存到GridFS中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        // 向mq发消息
        sendPostPage(pageId);
        return ResponseResult.SUCCESS();
    }

    /**
     * 向mq发送消息
     *
     * @param pageId
     */
    private void sendPostPage(String pageId) {
        // 得到页面的信息
        CmsPage cmsPage = getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        // 创建消息对象
        Map<String, String> msg = new HashMap<>();
        msg.put("pageId", pageId);
        // 转成json串
        String jsonString = JSON.toJSONString(msg);
        // 发送给mq
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,cmsPage.getSiteId(),jsonString);
    }

    /**
     * 保存静态化文件到GridFS
     *
     * @param pageId
     * @param htmlContent
     * @return
     */
    private CmsPage saveHtml(String pageId, String htmlContent) {
        // 得到页面的信息
        CmsPage cmsPage = getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        ObjectId fileId = null;
        try {
            // 将htmlContent转成输入流
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            // 将html文件保存到GridFS
            fileId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将html文件id更新到cmsPage中
        assert fileId != null;
        cmsPage.setHtmlFileId(fileId.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    // 页面静态化方法
    @Override
    public String getPageHtml(String pageId) {
        // 获取数据模型
        Map model = getModelByPageId(pageId);
        if (model == null) {
            // 数据模型获取不到
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        // 获取页面的模板
        String template = getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(template)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        // 执行静态化
        String html = generateHtml(template, model);
        return html;
    }

    // 执行静态化
    private String generateHtml(String templateContent, Map model) {
        // 创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", templateContent);
        // 向configuration中配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        // 获取模板
        try {
            Template template = configuration.getTemplate("template");
            // 调用api进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取模板
    private String getTemplateByPageId(String pageId) {
        //取出页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        // 取出页面的模板id
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        // 查询模板信息
        Optional<CmsTemplate> opt = cmsTemplateRepository.findById(templateId);
        if (opt.isPresent()) {
            CmsTemplate cmsTemplate = opt.get();
            // 获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            // 从GridFS中获取模板文件内容
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            // 打开一个下载流对象
            assert gridFSFile != null;
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            // 创建GridFSResource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            // 从流中获取数据
            try {
                return IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    // 获取模型数据
    private Map getModelByPageId(String pageId) {
        // 取出页面的信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        // 取出页面的dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            //页面dataurl为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        // 通过restTemplate请求dataUrl获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        return forEntity.getBody();
    }
}
