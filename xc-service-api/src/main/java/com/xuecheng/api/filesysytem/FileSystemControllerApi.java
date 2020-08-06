package com.xuecheng.api.filesysytem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author WangPan
 * @date 2020/8/6 13:05
 */
public interface FileSystemControllerApi {
    /**
     * 上传文件
     * @param multipartFile 文件
     * @param filetag   文件标签
     * @param businesskey   业务key
     * @param metadata  元信息，json格式
     * @return
     */
    UploadFileResult upload(MultipartFile multipartFile,
                            String filetag,
                            String businesskey,
                            String metadata);
}
