package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * j监听mq，接收页面发布的消息
 *
 * @author WangPan
 * @date 2020/7/28 16:55
 */
@Component
public class ConsumerPostPage {

    private static Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);

    @Autowired
    PageService pageService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String message) {
        // 解析消息
        Map map = JSON.parseObject(message, Map.class);
        // 得到消息中的页面id
        String pageId = (String) map.get("pageId");
        // 校验页面是否合法
        CmsPage cmsPage = pageService.findCmsPageById(pageId);
        if (cmsPage == null) {
            LOGGER.error("receive postpage message,cmsPage is null,pageId:{}", pageId);
            return;
        }
        // 调用service方法将页面从GridFS中下载到服务器
        pageService.savePageToServicePath(pageId);
    }
}
