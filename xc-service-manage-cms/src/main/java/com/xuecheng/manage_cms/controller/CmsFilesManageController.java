package com.xuecheng.manage_cms.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author WangPan
 * @date 2020/7/21 9:18
 * <p>
 * 文件管理
 */
@RequestMapping("/files")
@Controller
public class CmsFilesManageController {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    /**
     * 存文件
     *
     * @param url 文件的物理地址
     * @throws FileNotFoundException
     */
    @RequestMapping("/upload/{url}")
    public void upload(@PathVariable("url")String url) throws FileNotFoundException {
        if (url != null){
            //定义file
//            File file = new File("E:/code/java/ideaworkspace/xuechengjiaoyu/xc-service-manage-cms/src/main/resources/templates/index_banner.ftl");
            File file = new File(url);
            //定义fileInputStream
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectId objectId = gridFsTemplate.store(fileInputStream, "index_banner.ftl");
            System.out.println(objectId);
        }
    }

    // 取文件
    @RequestMapping("/getFileById")
    public void getFileById(String fileId) throws IOException {
        fileId = fileId == null ? "" : fileId;
        //根据文件id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));

        //打开一个下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建GridFsResource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //从流中取数据
        String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(content);

    }
    // 删除文件
    // 更新文件
}
