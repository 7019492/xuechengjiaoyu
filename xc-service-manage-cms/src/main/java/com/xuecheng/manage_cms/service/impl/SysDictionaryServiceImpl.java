package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import com.xuecheng.manage_cms.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WangPan
 * @date 2020/7/30 17:10
 */
@Service
public class SysDictionaryServiceImpl implements SysDictionaryService {

    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    @Override
    public SysDictionary findByDType(String type) {
        return sysDictionaryRepository.findByDType(type);
    }
}
