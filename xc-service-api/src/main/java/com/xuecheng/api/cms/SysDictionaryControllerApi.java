package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author WangPan
 * @date 2020/7/30 17:04
 */
@Api(value = "数据字典接口", description = "提供数据字典接口的管理、查询功能")
public interface SysDictionaryControllerApi {
    // 数据字段
    @ApiOperation("数据字段查询")
    SysDictionary getByType(String type);
}
