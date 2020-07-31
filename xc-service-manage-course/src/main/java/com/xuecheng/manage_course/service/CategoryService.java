package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;

/**
 * @author WangPan
 * @date 2020/7/30 9:09
 */

public interface CategoryService {
    // 查询分类
    CategoryNode findList();
}
