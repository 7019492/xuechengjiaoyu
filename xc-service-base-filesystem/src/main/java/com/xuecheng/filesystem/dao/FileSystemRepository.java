package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author WangPan
 * @date 2020/8/6 13:08
 */
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
