package com.greenstone.mes.material.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.base.api.RemoteBomService;
import com.greenstone.mes.bom.response.BomQueryResp;
import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.material.application.service.MaterialReceivingManager;
import com.greenstone.mes.material.application.service.MaterialTaskManager;
import com.greenstone.mes.material.domain.MaterialTask;
import com.greenstone.mes.material.domain.MaterialTaskBomRelation;
import com.greenstone.mes.material.domain.MaterialTaskMember;
import com.greenstone.mes.material.domain.MaterialTaskStatusChange;
import com.greenstone.mes.material.domain.service.*;
import com.greenstone.mes.material.request.MaterialReceivingAddReq;
import com.greenstone.mes.material.request.MaterialTaskAddReq;
import com.greenstone.mes.material.request.MaterialTaskEditReq;
import com.greenstone.mes.material.response.MaterialTaskDetailResp;
import com.greenstone.mes.system.api.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-08-08-14:21
 */
@Slf4j
@Service
public class MaterialTaskManagerImpl implements MaterialTaskManager {

    @Autowired
    private RemoteUserService userService;

    @Autowired
    private RemoteBomService remoteBomService;

    @Autowired
    private IMaterialTaskService materialTaskService;

    @Autowired
    private IMaterialTaskMemberService materialTaskMemberService;

    @Autowired
    private IMaterialTaskBomRelationService materialTaskBomRelationService;

    @Autowired
    private IMaterialTaskStatusChangeService statusChangeService;

    @Autowired
    private MaterialReceivingManager receivingManager;

    @Autowired
    private IMaterialReceivingService receivingService;

    @Override
    @Transactional
    public void insertMaterialTask(MaterialTaskAddReq materialTaskAddReq) {
        // 转换负责人id为姓名
        String leaderName = getUserNameById(materialTaskAddReq.getLeader());
        // 插入任务
        MaterialTask materialTask = MaterialTask.builder().projectCode(materialTaskAddReq.getProjectCode()).taskName(materialTaskAddReq.getTaskName())
                .leader(materialTaskAddReq.getLeader()).leaderName(leaderName).type(materialTaskAddReq.getType())
                .status(0).progress(0).deadline(materialTaskAddReq.getDeadline()).takeTime((double) 0).build();
        materialTaskService.save(materialTask);

        // 插入任务关联的表
        insertMaterialTaskRelation(materialTask, materialTaskAddReq);

    }

    /**
     * 根据用户id获得用户姓名
     *
     * @param userId 用户id
     * @return 姓名
     */
    private String getUserNameById(Long userId) {
        R<String> userResult = userService.getUserNameById(userId, SecurityConstants.INNER);
        if (R.FAIL == userResult.getCode()) {
            log.error("人员未找到,userId：" + userId);
            throw new ServiceException(userResult.getMsg());
        }
        return userResult.getData();
    }

    @Override
    public MaterialTaskDetailResp selectMaterialTaskById(Long id) {
        MaterialTask materialTask = materialTaskService.getById(id);
        if (ObjectUtil.isEmpty(materialTask)) {
            log.error("未找到任务,id:" + id);
            throw new ServiceException("未找到任务,id:" + id);
        }
        // bom关联信息
        List<MaterialTaskAddReq.BomInfo> bomInfos = new ArrayList<>();
        QueryWrapper<MaterialTaskBomRelation> bomRelationQueryWrapper = Wrappers.query(MaterialTaskBomRelation.builder().taskId(materialTask.getId()).build());
        List<MaterialTaskBomRelation> bomRelations = materialTaskBomRelationService.list(bomRelationQueryWrapper);
        if (CollectionUtil.isNotEmpty(bomRelations)) {
            for (MaterialTaskBomRelation bomRelation : bomRelations) {
                R<BomQueryResp> bomById = remoteBomService.getBomById(bomRelation.getBomId());
                if (R.FAIL == bomById.getCode()) {
                    log.error("该任务关联的bom不存在,bomId：" + bomRelation.getBomId());
                    throw new ServiceException("该任务关联的bom不存在");
                }
                MaterialTaskAddReq.BomInfo bomInfo = MaterialTaskAddReq.BomInfo.builder().bomId(bomRelation.getBomId()).name(bomById.getData().getBomName()).build();
                bomInfos.add(bomInfo);
            }

        }
        // 成员关联信息
        List<MaterialTaskAddReq.MemberInfo> memberInfos = selectMaterialTaskMemberListById(materialTask.getId());


        return MaterialTaskDetailResp.builder().id(materialTask.getId()).projectCode(materialTask.getProjectCode())
                .taskName(materialTask.getTaskName()).leaderName(materialTask.getLeaderName()).leader(materialTask.getLeader())
                .type(materialTask.getType()).deadline(materialTask.getDeadline()).progress(materialTask.getProgress())
                .status(materialTask.getStatus()).takeTime(materialTask.getTakeTime())
                .bomList(bomInfos).memberList(memberInfos)
                .build();
    }

    @Override
    @Transactional
    public void updateMaterialTask(MaterialTaskEditReq materialTaskEditReq) {
        MaterialTask materialTask = materialTaskService.getById(materialTaskEditReq.getId());
        if (ObjectUtil.isEmpty(materialTask)) {
            log.error("未找到任务:" + materialTaskEditReq);
            throw new ServiceException("未找到任务:" + materialTaskEditReq);
        }
        if (!SecurityUtils.getLoginUser().getUser().getNickName().equals(materialTask.getCreateBy())) {
            log.error("仅创建人可修改任务！");
            throw new ServiceException("仅创建人可修改任务！");
        }
        // 转换负责人id为姓名
        String leaderName = getUserNameById(materialTaskEditReq.getLeader());
        // 修改任务信息
        materialTask.setTaskName(materialTaskEditReq.getTaskName());
        materialTask.setProjectCode(materialTaskEditReq.getProjectCode());
        materialTask.setLeader(materialTaskEditReq.getLeader());
        materialTask.setLeaderName(leaderName);
        materialTask.setType(materialTaskEditReq.getType());
        materialTask.setDeadline(materialTaskEditReq.getDeadline());
        materialTaskService.updateById(materialTask);

        // 删除任务关联的表
        QueryWrapper<MaterialTaskMember> userQueryWrapper = Wrappers.query(MaterialTaskMember.builder().taskId(materialTask.getId()).build());
        materialTaskMemberService.remove(userQueryWrapper);
        QueryWrapper<MaterialTaskBomRelation> bomRelationQueryWrapper = Wrappers.query(MaterialTaskBomRelation.builder().taskId(materialTask.getId()).build());
        materialTaskBomRelationService.remove(bomRelationQueryWrapper);

        // 插入任务关联的表
        MaterialTaskAddReq materialTaskAddReq = MaterialTaskAddReq.builder().taskName(materialTaskEditReq.getTaskName())
                .projectCode(materialTaskEditReq.getProjectCode()).bomList(materialTaskEditReq.getBomList()).memberList(materialTaskEditReq.getMemberList())
                .leader(materialTaskEditReq.getLeader()).type(materialTaskEditReq.getType()).deadline(materialTaskEditReq.getDeadline()).build();
        insertMaterialTaskRelation(materialTask, materialTaskAddReq);
    }

    @Override
    @Transactional
    public void updateMaterialTaskStatus(MaterialTaskEditReq materialTaskEditReq) {
        MaterialTask materialTask = materialTaskService.getById(materialTaskEditReq.getId());
        if (ObjectUtil.isEmpty(materialTask)) {
            log.error("未找到任务:" + materialTaskEditReq);
            throw new ServiceException("未找到任务:" + materialTaskEditReq);
        }
        if (!SecurityUtils.getLoginUser().getUser().getNickName().equals(materialTask.getCreateBy())) {
            log.error("仅创建人可操作任务！");
            throw new ServiceException("仅创建人可操作任务！");
        }
        if (materialTaskEditReq.getStatus() != null) {
            materialTask.setStatus(materialTaskEditReq.getStatus());
            materialTaskService.updateById(materialTask);
            // 记录任务状态变更
            MaterialTaskStatusChange statusChange = MaterialTaskStatusChange.builder().taskId(materialTask.getId()).status(materialTaskEditReq.getStatus()).build();
            statusChangeService.save(statusChange);
        } else {
            throw new ServiceException("任务状态不为空:" + materialTaskEditReq.getStatus());
        }

    }

    @Override
    public List<MaterialTaskAddReq.MemberInfo> selectMaterialTaskMemberListById(Long id) {
        // 成员关联信息
        List<MaterialTaskAddReq.MemberInfo> memberInfos = new ArrayList<>();
        QueryWrapper<MaterialTaskMember> userQueryWrapper = Wrappers.query(MaterialTaskMember.builder().taskId(id).build());
        List<MaterialTaskMember> userList = materialTaskMemberService.list(userQueryWrapper);
        if (CollectionUtil.isNotEmpty(userList)) {
            for (MaterialTaskMember materialTaskMember : userList) {
                String memberName = getUserNameById(materialTaskMember.getMemberId());
                MaterialTaskAddReq.MemberInfo memberInfo = MaterialTaskAddReq.MemberInfo.builder().memberType(materialTaskMember.getMemberType()).memberId(materialTaskMember.getMemberId()).memberName(memberName).build();
                memberInfos.add(memberInfo);
            }
        }
        return memberInfos;
    }

    /**
     * 插入任务关联信息表
     *
     * @param materialTask       任务信息
     * @param materialTaskAddReq 待新增信息
     */
    public void insertMaterialTaskRelation(MaterialTask materialTask, MaterialTaskAddReq materialTaskAddReq) {
        // 负责人
        materialTaskMemberService.save(MaterialTaskMember.builder().taskId(materialTask.getId()).memberType(0).memberId(materialTaskAddReq.getLeader()).build());
        // 成员
        if (CollectionUtil.isNotEmpty(materialTaskAddReq.getMemberList())) {
            for (MaterialTaskAddReq.MemberInfo memberInfo : materialTaskAddReq.getMemberList()) {
                materialTaskMemberService.save(MaterialTaskMember.builder().taskId(materialTask.getId()).memberType(1).memberId(memberInfo.getMemberId()).build());
            }
        }
        // 组件关联
        if (CollectionUtil.isNotEmpty(materialTaskAddReq.getBomList())) {
            for (MaterialTaskAddReq.BomInfo bomInfo : materialTaskAddReq.getBomList()) {
                materialTaskBomRelationService.save(MaterialTaskBomRelation.builder().taskId(materialTask.getId()).bomId(bomInfo.getBomId()).build());

            }
        }
        // 新增领料单
        if (materialTask.getType() == 1 && materialTask.getReceivingId() == null) {
            MaterialReceivingAddReq materialReceivingAddReq = MaterialReceivingAddReq.builder().projectCode(materialTask.getProjectCode())
                    .deadline(materialTask.getDeadline()).build();
            List<MaterialReceivingAddReq.BomInfo> bomInfoList = new ArrayList<>();
            for (MaterialTaskAddReq.BomInfo bomInfo : materialTaskAddReq.getBomList()) {
                MaterialReceivingAddReq.BomInfo info = MaterialReceivingAddReq.BomInfo.builder().bomId(bomInfo.getBomId()).name(bomInfo.getName()).build();
                bomInfoList.add(info);
            }
            materialReceivingAddReq.setBomList(bomInfoList);
            Long id = receivingManager.insertMaterialReceiving(materialReceivingAddReq);
            // 更新任务的领料单关联ID
            materialTask.setReceivingId(id);
            materialTaskService.updateById(materialTask);
        }
    }
}
