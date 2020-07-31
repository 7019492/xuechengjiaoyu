package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WangPan
 * @date 2020/7/30 17:07
 */
@Repository
public interface SysDictionaryRepository extends MongoRepository<SysDictionary, String> {
    // 根据字段分类查询字段信息
    SysDictionary findByDType(String type);
}
