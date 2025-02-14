package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.utils.bean.BeanUtils;
import com.greenstone.mes.oa.application.service.OaDesignProjectService;
import com.greenstone.mes.oa.constant.DesignProjectConst;
import com.greenstone.mes.oa.domain.OaDesignProject;
import com.greenstone.mes.oa.infrastructure.mapper.OaDesignProjectMapper;
import com.greenstone.mes.oa.request.OaDesignProjectAddReq;
import com.greenstone.mes.oa.request.OaDesignProjectEditReq;
import com.greenstone.mes.oa.response.OaDesignProjectListResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OaDesignProjectServiceImpl extends ServiceImpl<OaDesignProjectMapper, OaDesignProject> implements OaDesignProjectService {

    @Override
    public List<OaDesignProject> getProject(String projectCode) {
        List<OaDesignProject> list = list(new QueryWrapper<>(OaDesignProject.builder().projectCode(projectCode).build()));
        if (CollectionUtil.isNotEmpty(list)) {
            list.sort(Comparator.comparingInt(OaDesignProject::getPhase));
        }
        return list;
    }

    @Override
    public void saveProject(List<OaDesignProjectAddReq> addReqs) {
        duplicatedCheck(OaDesignProject.builder().projectCode(addReqs.get(0).getProjectCode()).build(), "已存在相同的业务代码");

        List<OaDesignProject> projects = new ArrayList<>();
        for (OaDesignProjectAddReq addReq : addReqs) {
            OaDesignProject project = new OaDesignProject();
            BeanUtils.copyBeanProp(project, addReq);
            projects.add(project);
        }
        saveBatch(projects);
    }

    @Override
    @Transactional
    public void updateProject(List<OaDesignProjectEditReq> editReqs) {
        for (OaDesignProjectEditReq editReq : editReqs) {
            OaDesignProject project = new OaDesignProject();
            BeanUtils.copyBeanProp(project, editReq);
            updateById(project);
        }
    }

    @Override
    public List<OaDesignProjectListResp> getProjects(List<OaDesignProject> projects) {
        List<OaDesignProjectListResp> respList = new LinkedList<>();
        // 根据业务代码分组
        Map<String, List<OaDesignProject>> projectMap = projects.stream().collect(Collectors.groupingBy(OaDesignProject::getProjectCode));
        // 循环每一个项目，判断是否延期了
        projectMap.forEach((code, projectList) -> {
            // 拿到计划数据和实际数据
            Optional<OaDesignProject> planOptional = projectList.stream().filter(p -> p.getPhase() == DesignProjectConst.Phase.PLAN).findFirst();
            Optional<OaDesignProject> actualOptional = projectList.stream().filter(p -> p.getPhase() == DesignProjectConst.Phase.ACTUAL).findFirst();
            if (!planOptional.isPresent() || !actualOptional.isPresent()) {
                log.warn("bad project data: lack of phase {}", code);
                return;
            }
            OaDesignProject plan = planOptional.get();
            OaDesignProject actual = actualOptional.get();
            // 按照步骤从后往前判断，如果某一步骤的实际完成时间晚于计划时间，则为延期；或实际完成时间没填且当前时间已经晚于计划时间，也是延期
            int result = DesignProjectConst.Status.ON_TIME;

            Date[] planDates = new Date[]{plan.getDeadline(), plan.getOutputReviewTime(), plan.getWorks2dFinishTime(),
                    plan.getDetailReviewTime(), plan.getPlanReviewTime(), plan.getWorks3dFinishTime()};
            Date[] actualDates = new Date[]{actual.getDeadline(), actual.getOutputReviewTime(), actual.getWorks2dFinishTime(),
                    actual.getDetailReviewTime(), actual.getPlanReviewTime(), actual.getWorks3dFinishTime()};
            // 找到需要比较的日期，需要比较的日期条件为：1. 节点的计划日期不能为空 2. 最早的没有填写实际完成日期的环节点对应的计划时间
            Date planDate = null;
            for (int i = 0; i < actualDates.length; i++) {
                if (actualDates[i] == null && planDates[i] != null) {
                    planDate = planDates[i];
                } else {
                    break;
                }
            }
            // 若没有需比对的计划日期，则表示已经填了实际纳期，则用计划纳期和实际纳期做比对
            if (planDate == null) {
                if (plan.getDeadline().before(actual.getDeadline())) {
                    result = DesignProjectConst.Status.DELAY;
                }
            } else {
                // 若没有实际的纳期，则用计划日期对比实际日期
                Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
                if (planDate.before(today)) {
                    result = DesignProjectConst.Status.DELAY;
                } else if ((planDate.getTime() - today.getTime()) <= 3 * 24 * 60 * 60 * 1000) {
                    result = DesignProjectConst.Status.WARN;
                }
            }

            // 按照阶段排序
            projectList.sort(Comparator.comparingInt(OaDesignProject::getPhase));

            // 封装返回结果
            for (OaDesignProject designProject : projectList) {
                OaDesignProjectListResp resp = new OaDesignProjectListResp();
                BeanUtils.copyBeanProp(resp, designProject);
                resp.setStatus(result);
                respList.add(resp);
            }
        });
        return respList;
    }

}
