package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author WangPan
 * @date 2020/6/28 15:06
 */
@Api(value = "cms页面管理接口", description = "cms页面管理接口，提供页面的憎、删、改、查。")
public interface CmsPageControllerApi {
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, dataType = "int", paramType = "path")
    })
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    // 新增页面
    @ApiOperation("新增页面")
    CmsPageResult add(CmsPage cmsPage);

    // 根据id查询页面
    @ApiOperation("根据id查询页面信息")
    CmsPage findById(String id);

    // 编辑页面
    @ApiOperation("编辑页面")
    CmsPageResult edit(String id, CmsPage cmsPage);

    // 删除页面
    @ApiOperation("删除页面")
    ResponseResult delete(String id);
}
