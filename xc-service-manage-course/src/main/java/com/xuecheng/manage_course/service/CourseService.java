package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

import java.util.List;


/**
 * @author WangPan
 * @date 2020/7/30 9:09
 */

public interface CourseService {
    // 课程计划查询
    TeachplanNode findTeachplanList(String courseId);

    // 添加課程計劃
    ResponseResult addTeachplan(Teachplan teachplan);

    // 我的课程列表
    QueryResponseResult findCourseListPage(Integer page, Integer size, CourseListRequest courseListRequest);

    // 添加课程基本信息
    AddCourseResult addCourseBase(CourseBase courseBase);

    // 获取课程基础信息
    CourseBase getCourseBaseById(String courseId);

    // 更新课程基础信息
    ResponseResult updateCourseBase(String id, CourseBase courseBase);

    // 获取课程营销信息
    CourseMarket getCourseMarketById(String courseId);

    // 更新课程营销信息
    CourseMarket saveCourseMarket(String id, CourseMarket courseMarket);
}
