package com.greenstone.mes.material.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.material.application.service.MaterialTaskManager;
import com.greenstone.mes.material.application.service.MaterialTaskReportManager;
import com.greenstone.mes.material.domain.MaterialTask;
import com.greenstone.mes.material.domain.MaterialTaskProblemReport;
import com.greenstone.mes.material.domain.MaterialTaskProgressReport;
import com.greenstone.mes.material.domain.MaterialTaskWorkReport;
import com.greenstone.mes.material.application.helper.MaterialFileHelper;
import com.greenstone.mes.material.request.*;
import com.greenstone.mes.material.domain.service.IMaterialTaskProblemReportService;
import com.greenstone.mes.material.domain.service.IMaterialTaskProgressReportService;
import com.greenstone.mes.material.domain.service.IMaterialTaskService;
import com.greenstone.mes.material.domain.service.IMaterialTaskWorkReportService;
import com.greenstone.mes.file.api.request.FileUploadReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-08-09-9:18
 */
@Slf4j
@Service
public class MaterialTaskReportManagerImpl implements MaterialTaskReportManager {

    @Autowired
    private IMaterialTaskProgressReportService progressReportService;

    @Autowired
    private IMaterialTaskProblemReportService problemReportService;

    @Autowired
    private IMaterialTaskWorkReportService workReportService;

    @Autowired
    private MaterialFileHelper fileHelper;

    @Autowired
    private IMaterialTaskService materialTaskService;

    @Autowired
    private MaterialTaskManager materialTaskManager;


    @Override
    @Transactional
    public void progressReport(List<MultipartFile> files, MaterialTaskProgressReportAddReq progressReportAddReq) {
        MaterialTask materialTask = materialTaskService.getById(progressReportAddReq.getTaskId());
        if (ObjectUtil.isEmpty(materialTask)) {
            log.error("未找到任务,id:" + progressReportAddReq.getTaskId());
            throw new ServiceException("未找到任务,id:" + progressReportAddReq.getTaskId());
        }
        if (!SecurityUtils.getLoginUser().getUser().getNickName().equals(materialTask.getLeaderName())) {
            log.error("仅负责人可上报进度！");
            throw new ServiceException("仅负责人可上报进度！");
        }
        MaterialTaskProgressReport materialTaskProgressReport = MaterialTaskProgressReport.builder().taskId(progressReportAddReq.getTaskId()).progress(progressReportAddReq.getProgress())
                .remark(progressReportAddReq.getRemark()).build();
        progressReportService.save(materialTaskProgressReport);

        // 上传文件
        if (CollectionUtil.isNotEmpty(files)) {
            FileUploadReq fileUploadReq = FileUploadReq.builder().fileList(files).relationId(materialTaskProgressReport.getId()).relationType(1).build();
            fileHelper.uploadFile(fileUploadReq);
        }
        if (CollectionUtil.isEmpty(files) && progressReportAddReq.getFileBase64() != null) {
            FileUploadReq fileUploadReq = FileUploadReq.builder().baseStrList(progressReportAddReq.getFileBase64()).relationId(materialTaskProgressReport.getId()).relationType(1).build();
            fileHelper.uploadFile(fileUploadReq);
        }
        // 修改任务进度
        materialTask.setProgress(progressReportAddReq.getProgress());
        materialTaskService.updateById(materialTask);
    }

    @Override
    @Transactional
    public void problemReport(List<MultipartFile> files, MaterialTaskProblemReportAddReq problemReportAddReq) {
        MaterialTask materialTask = materialTaskService.getById(problemReportAddReq.getTaskId());
        if (ObjectUtil.isEmpty(materialTask)) {
            log.error("未找到任务,id:" + problemReportAddReq.getTaskId());
            throw new ServiceException("未找到任务,id:" + problemReportAddReq.getTaskId());
        }

        if (!SecurityUtils.getLoginUser().getUser().getNickName().equals(materialTask.getLeaderName())) {
            log.error("仅负责人可上报问题！");
            throw new ServiceException("仅负责人可上报问题！");
        }
        MaterialTaskProblemReport problemReport = MaterialTaskProblemReport.builder().taskId(problemReportAddReq.getTaskId()).type(problemReportAddReq.getType())
                .questioner(problemReportAddReq.getQuestioner()).questionerName(problemReportAddReq.getQuestionerName()).description(problemReportAddReq.getDescription()).build();
        problemReportService.save(problemReport);

        // 上传文件
        if (CollectionUtil.isNotEmpty(files)) {
            FileUploadReq fileUploadReq = FileUploadReq.builder().fileList(files).relationId(problemReport.getId()).relationType(2).build();
            fileHelper.uploadFile(fileUploadReq);
        }
        if (CollectionUtil.isEmpty(files) && problemReportAddReq.getFileBase64() != null) {
            FileUploadReq fileUploadReq = FileUploadReq.builder().baseStrList(problemReportAddReq.getFileBase64()).relationId(problemReport.getId()).relationType(2).build();
            fileHelper.uploadFile(fileUploadReq);
        }

    }

    @Override
    @Transactional
    public void workReport(MaterialTaskWorkReportAddReq workReportAddReq) {
        MaterialTask materialTask = materialTaskService.getById(workReportAddReq.getTaskId());
        if (ObjectUtil.isEmpty(materialTask)) {
            log.error("未找到任务,id:" + workReportAddReq.getTaskId());
            throw new ServiceException("未找到任务,id:" + workReportAddReq.getTaskId());
        }
        // 判断是否任务成员
        boolean isTaskMember = false;
        List<MaterialTaskAddReq.MemberInfo> memberInfos = materialTaskManager.selectMaterialTaskMemberListById(workReportAddReq.getTaskId());
        if (CollUtil.isNotEmpty(memberInfos)) {
            for (MaterialTaskAddReq.MemberInfo memberInfo : memberInfos) {
                if (SecurityUtils.getLoginUser().getUserid().equals(memberInfo.getMemberId())) {
                    isTaskMember = true;
                }
            }
        }
        // 不是任务成员不可以上报工时
        if (!isTaskMember) {
            log.error("仅任务成员可上报任务工时！");
            throw new ServiceException("仅任务成员可上报任务工时！");
        }
        MaterialTaskWorkReport workReport = MaterialTaskWorkReport.builder().taskId(workReportAddReq.getTaskId()).takeTime(workReportAddReq.getTakeTime()).description(workReportAddReq.getDescription()).build();
        workReportService.save(workReport);
        // 计算总耗时
        if (materialTask.getTakeTime() != null && workReport.getTakeTime() != null) {
            materialTask.setTakeTime(materialTask.getTakeTime() + workReport.getTakeTime());
            materialTaskService.updateById(materialTask);
        }
    }

    @Override
    public List<MaterialTaskWorkReport> selectWorkList(MaterialTaskReportListReq reportListReq) {
        QueryWrapper<MaterialTaskWorkReport> queryWrapper = Wrappers.query(MaterialTaskWorkReport.builder().taskId(reportListReq.getTaskId()).build());
        return workReportService.list(queryWrapper);
    }

    @Override
    public List<MaterialTaskProblemReport> selectProblemList(MaterialTaskReportListReq reportListReq) {
        QueryWrapper<MaterialTaskProblemReport> queryWrapper = Wrappers.query(MaterialTaskProblemReport.builder().taskId(reportListReq.getTaskId()).build());
        List<MaterialTaskProblemReport> list = problemReportService.list(queryWrapper);
        // 设置附件信息
        if (CollectionUtil.isNotEmpty(list)) {
            for (MaterialTaskProblemReport materialTaskProgressReport : list) {
                materialTaskProgressReport.setFileInfoList(fileHelper.getFileInfo(materialTaskProgressReport.getId(), 2));
            }
        }
        return list;
    }

    @Override
    public List<MaterialTaskProgressReport> selectProgressList(MaterialTaskReportListReq reportListReq) {
        QueryWrapper<MaterialTaskProgressReport> queryWrapper = Wrappers.query(MaterialTaskProgressReport.builder().taskId(reportListReq.getTaskId()).build());
        List<MaterialTaskProgressReport> list = progressReportService.list(queryWrapper);
        // 设置附件信息
        if (CollectionUtil.isNotEmpty(list)) {
            for (MaterialTaskProgressReport materialTaskProgressReport : list) {
                materialTaskProgressReport.setFileInfoList(fileHelper.getFileInfo(materialTaskProgressReport.getId(), 1));
            }
        }
        return list;
    }


}
