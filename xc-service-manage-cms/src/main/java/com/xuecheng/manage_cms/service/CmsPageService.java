package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * @author WangPan
 * @date 2020/6/28 15:23
 */
public interface CmsPageService {
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    CmsPageResult add(CmsPage cmsPage);

    CmsPage getById(String id);

    CmsPageResult edit(String id, CmsPage cmsPage);

    ResponseResult delete(String id);

    // 页面静态化方法
    String getPageHtml(String pageId);

    // 页面发布
    ResponseResult post(String pageId);
}
