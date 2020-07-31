package com.xuecheng.manage_course.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CourseMapper;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import com.xuecheng.manage_course.service.CourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author WangPan
 * @date 2020/7/30 9:11
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMapper courseMapper;

    @Override
    public TeachplanNode findTeachplanList(String courseId) {
        return teachplanMapper.selectList(courseId);
    }

    @Override
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            // 取出该课程的根节点
            parentid = getTeachplanRoot(courseid);
        }
        assert parentid != null;
        Optional<Teachplan> opt = teachplanRepository.findById(parentid);
        Teachplan parentNode = null;
        if (opt.isPresent()) {
            parentNode = opt.get();
        }
        assert parentNode != null;
        String grade = parentNode.getGrade();
        // 新节点
        Teachplan teachplan1New = new Teachplan();
        BeanUtils.copyProperties(teachplan, teachplan1New);
        teachplan1New.setParentid(parentid);
        teachplan1New.setCourseid(courseid);
        if ("1".equals(grade))
            teachplan1New.setGrade("2");//根据父节点的级别设置
        else teachplan1New.setGrade("3");

        teachplanRepository.save(teachplan1New);
        return ResponseResult.SUCCESS();
    }

    @Override
    public QueryResponseResult findCourseListPage(Integer page, Integer size, CourseListRequest courseListRequest) {
        page = page == null || page <= 0 ? 1 : page;
        size = size == null || size <= 0 ? 10 : size;
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> list = courseListPage.getResult();
        if (list == null) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(list);
        queryResult.setTotal(courseListPage.getTotal());
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS, queryResult);
    }

    @Override
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        courseBase.setStatus("202001");
        CourseBase save = courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, save.getId());
    }

    // 查询课程的根据点，如果查询不到，要自动添加根节点
    private String getTeachplanRoot(String courseid) {

        Optional<CourseBase> opt = courseBaseRepository.findById(courseid);
        if (!opt.isPresent()) {
            return null;
        }
        CourseBase courseBase = opt.get();

        List<Teachplan> list = teachplanRepository.findByCourseidAndParentid(courseid, "0");
        if (list == null || list.size() <= 0) {
            // 查询不到，要自动添加根节点
            Teachplan teachplan = Teachplan.builder()
                    .parentid("0")
                    .grade("1")
                    .pname(courseBase.getName())
                    .courseid(courseid)
                    .status("0").build();
            Teachplan save = teachplanRepository.save(teachplan);
            return save.getId();
        }
        return list.get(0).getId();
    }
}
