package com.xuecheng.filesystem.service;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author WangPan
 * @date 2020/8/6 13:09
 */
public interface FileSystemService {
    // 上传文件
    UploadFileResult upload(MultipartFile file, String filetag, String businesskey, String metadata);
}
