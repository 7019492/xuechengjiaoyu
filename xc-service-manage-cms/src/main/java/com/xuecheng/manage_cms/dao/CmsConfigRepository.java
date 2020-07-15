package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author WangPan
 * @date 2020/7/14 17:26
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
