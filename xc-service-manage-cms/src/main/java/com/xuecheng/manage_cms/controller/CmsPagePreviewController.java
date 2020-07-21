package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author WangPan
 * @date 2020/7/20 15:54
 */
@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    CmsPageService pageService;

    // 页面预览
    @RequestMapping(value = "/cms/preview/{pageId}",method = RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId) throws IOException {
        // 执行静态化
        String pageHtml = pageService.getPageHtml(pageId);
        // 通过response对象 将内容输出
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(pageHtml.getBytes(StandardCharsets.UTF_8));
    }
}
