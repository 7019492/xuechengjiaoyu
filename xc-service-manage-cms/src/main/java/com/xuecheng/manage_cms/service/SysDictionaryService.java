package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;

/**
 * @author WangPan
 * @date 2020/7/30 17:09
 */
public interface SysDictionaryService {
    // 根据字典分类查询字典信息
    SysDictionary findByDType(String type);
}
