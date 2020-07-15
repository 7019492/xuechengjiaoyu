package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author WangPan
 * @date 2020/7/14 17:28
 */
@Service
public class CmsConfigServiceImpl implements CmsConfigService {
    @Autowired
    CmsConfigRepository cmsConfigRepository;

    @Override
    public CmsConfig getmodel(String id) {
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        return optional.orElse(null);
    }
}
