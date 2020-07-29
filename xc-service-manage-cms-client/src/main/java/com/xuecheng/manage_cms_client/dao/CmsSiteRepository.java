package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WangPan
 * @date 2020/6/28 15:21
 */
@Repository
public interface CmsSiteRepository extends MongoRepository<CmsSite, String> {
}
