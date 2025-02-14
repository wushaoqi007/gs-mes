package com.greenstone.mes.workflow.domain.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.external.enums.TaskStatus;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.producer.MsgProducer;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.workflow.cmd.FlowCommitCmd;
import com.greenstone.mes.workflow.domain.helper.WxApprovalBuilder;
import com.greenstone.mes.workflow.domain.helper.WxApprovalHelper;
import com.greenstone.mes.workflow.domain.service.FlowService;
import com.greenstone.mes.workflow.infrastructure.consts.FlowConst;
import com.greenstone.mes.workflow.infrastructure.consts.WxApprovalConst;
import com.greenstone.mes.workflow.infrastructure.mapper.*;
import com.greenstone.mes.workflow.infrastructure.persistence.*;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;
import com.greenstone.mes.workflow.resp.FlowCommitResp;
import com.greenstone.mes.wxcp.api.RemoteWxFlowService;
import com.greenstone.mes.wxcp.cmd.WxFlowCommitCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpXmlApprovalInfo;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class FlowServiceImpl implements FlowService {

    private final FlwProcessMapper flwProcessMapper;
    private final RemoteWxFlowService wxFlowService;
    private final RemoteUserService userService;
    private final FlwInstanceMapper flwInstanceMapper;
    private final FlwHisInstanceMapper flwHisInstanceMapper;
    private final FlwTaskMapper flwTaskMapper;
    private final FlwHisTaskMapper flwHisTaskMapper;
    private final MsgProducer<ApprovalChangeMsg> msgProducer;
    private final WxApprovalBuilder wxApprovalBuilder;

    @Override
    public FlowCommitResp commit(FlowCommitCmd commitCmd) {
        log.info("接收到审批提交请求：{}", commitCmd);
        FlwProcess flwProcess = flwProcessMapper.getOneOnly(FlwProcess.builder().businessKey(commitCmd.getBusinessKey()).build());
        if (flwProcess == null) {
            throw new RuntimeException("缺少业务key对应的流程信息: " + commitCmd.getBusinessKey());
        }
        FlowCommitResp commitResp;
        if (FlowConst.Source.WXCP == flwProcess.getProcessSource()) {
            WxFlowCommitCmd wxFlowCommitCmd = WxFlowCommitCmd.builder().applyUserId(commitCmd.getApplyUserId())
                    .templateId(flwProcess.getProcessKey())
                    .attrs(commitCmd.getAttrs()).build();
            commitResp = wxFlowService.commit(wxFlowCommitCmd);

            SysUser originator = userService.getUser(SysUser.builder().userId(commitCmd.getApplyUserId()).build());
            if (originator == null) {
                throw new RuntimeException(StrUtil.format("用户id不存在: {}, ", commitCmd.getApplyUserId()));
            }

            FlwInstance flwInstance = wxApprovalBuilder.buildInstance(flwProcess, originator, commitCmd, commitResp);
            flwInstanceMapper.insert(flwInstance);

            FlwHisInstance flwHisInstance = wxApprovalBuilder.buildHisInstance(flwInstance, flwProcess, originator, commitResp);
            flwHisInstance.setSerialNo(flwInstance.getSerialNo());

            flwHisInstanceMapper.insert(flwHisInstance);
        } else {
            throw new RuntimeException("不支持的流程来源：" + flwProcess.getProcessSource());
        }
        log.info("审批提交晚餐");
        return commitResp;
    }

    /**
     * 企业微信审批变更回调
     *
     * @param cpXmlMessage 回调消息
     */
    @Override
    public void wxApprovalChange(WxCpXmlMessage cpXmlMessage) {
        log.info("审批：接收到企业微信审批消息");
        SysUser originator = userService.getUser(SysUser.builder().mainWxcpId(cpXmlMessage.getToUserName()).wxUserId(cpXmlMessage.getApprovalInfo().getApplier().getUserId()).build());
        if (originator == null) {
            throw new RuntimeException(StrUtil.format("系统中不存在此用户, 企业微信id: {}, 用户id: {}", cpXmlMessage.getToUserName(),
                    cpXmlMessage.getApprovalInfo().getApplier().getUserId()));
        }
        FlwProcess flwProcess = flwProcessMapper.getOneOnly(FlwProcess.builder().processKey(cpXmlMessage.getApprovalInfo().getTemplateId()).build());
        if (flwProcess == null) {
            throw new RuntimeException("缺少流程key对应的业务key: " + cpXmlMessage.getApprovalInfo().getTemplateId());
        }

        switch (cpXmlMessage.getApprovalInfo().getStatusChangeEvent()) {
            // 提交
            case WxApprovalConst.StatusChangeEvent.COMMIT -> wxApprovalCommit(cpXmlMessage, originator);
            // 同意
            case WxApprovalConst.StatusChangeEvent.AGREE -> wxApprovalAgree(cpXmlMessage, originator);
            // 驳回
            case WxApprovalConst.StatusChangeEvent.REJECT -> wxApprovalReject(cpXmlMessage, originator);
            // 改签
            case WxApprovalConst.StatusChangeEvent.CHANGE_ASSIGNOR -> changeAssignor(cpXmlMessage, originator);
            // 撤回
            case WxApprovalConst.StatusChangeEvent.REVOKE -> wxApprovalRevoke(cpXmlMessage, originator);
            default -> throw new RuntimeException("不支持的企业微信审批操作: " + cpXmlMessage.getApprovalInfo().getStatusChangeEvent());
        }

        FlwInstance flwInstance = flwInstanceMapper.getOneOnly(FlwInstance.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        FlwHisInstance flwHisInstance = null;
        if (flwInstance == null) {
            flwHisInstance = flwHisInstanceMapper.getOneOnly(FlwHisInstance.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        }


        ApprovalChangeMsg approvalChangeMsg = ApprovalChangeMsg.builder()
                .itemId(flwInstance != null ? flwInstance.getItemId() : flwHisInstance.getItemId())
                .serialNo(flwInstance != null ? flwInstance.getSerialNo() : flwHisInstance.getSerialNo())
                .instanceNo(cpXmlMessage.getApprovalInfo().getSpNo())
                .status(WxApprovalHelper.convertProcessStatus(cpXmlMessage))
                .businessKey(flwProcess.getBusinessKey())
                .processName(flwProcess.getProcessName()).build();

        // 添加审批意见和操作人
        WxCpXmlApprovalInfo.Detail spDetail = getLastSpDetail(cpXmlMessage);
        if (spDetail != null) {
            User operator = userService.getByWx(cpXmlMessage.getToUserName(), spDetail.getApprover().getUserId());
            approvalChangeMsg.setOperator(operator);
            approvalChangeMsg.setRemark(spDetail.getSpeech());
            approvalChangeMsg.setNodeName("审批");
        }

        try {
            msgProducer.send(MqConst.Topic.FLOW_APPROVAL_CHANGE, approvalChangeMsg);
        } catch (ExecutionException | InterruptedException e) {
            log.error("发送审批变更消息失败: ", e);
        }
    }

    @Override
    public List<FlwTask> todoTasks() {
        return flwTaskMapper.list(FlwTask.builder().approverId(SecurityUtils.getUserId()).build());
    }

    @Override
    public List<FlwHisTaskPo> hisTask(String instanceNo) {
        return flwHisTaskMapper.list(FlwHisTaskPo.builder().instanceNo(instanceNo).build());
    }

    /**
     * 处理企业微信审批提交通知
     * 审批提交的时候审批只会在审批中状态
     * statusChangeEvent=1 且 spStatus=1
     */
    private void wxApprovalCommit(WxCpXmlMessage cpXmlMessage, SysUser originator) {
        log.info("审批：处理提交信息");
        FlwInstance flwInstance = flwInstanceMapper.getOneOnly(FlwInstance.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        if (flwInstance == null) {
            // 如果没有获取到流程实例，说明在保存到数据库之前已经收到了企业微信的回调，等待3秒再查询一次
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            flwInstance = flwInstanceMapper.getOneOnly(FlwInstance.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
            // TODO 等待三秒后还是查不到，则进行补录
            if (flwInstance == null) {
                throw new RuntimeException("无法获取审批实例: " + cpXmlMessage.getApprovalInfo().getSpNo());
            }
        }
        // 记录当前任务
        WxCpXmlApprovalInfo.SpRecord nextSpRecord = getNextSpRecord(cpXmlMessage);
        if (nextSpRecord != null) {
            List<FlwTask> flwTasks = buildTasks(flwInstance, cpXmlMessage, nextSpRecord, originator);
            flwTaskMapper.insertBatchSomeColumn(flwTasks);
        }

        // 记录历史任务表
        List<FlwHisTaskPo> flwHisTasks = buildHisTasks(flwInstance, cpXmlMessage, originator);
        flwHisTaskMapper.insertBatchSomeColumn(flwHisTasks);
    }

    /**
     * 处理企业微信审批通过通知
     * 审批通过的时候审批会在审批中或已通过状态
     * statusChangeEvent=2 且 spStatus=1或2
     */
    private void wxApprovalAgree(WxCpXmlMessage cpXmlMessage, SysUser originator) {
        log.info("审批：处理同意信息");
        FlwInstance flwInstance = flwInstanceMapper.getOneOnly(FlwInstance.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        if (flwInstance == null) {
            // TODO 需要主动同步
            throw new RuntimeException("无法获取审批实例: " + cpXmlMessage.getApprovalInfo().getSpNo());
        }
        // 同意且后继续审批的情况
        if (cpXmlMessage.getApprovalInfo().getSpStatus() == WxApprovalConst.SpStatus.WAIT_APPROVE) {

            // 删除完成的审批任务
            flwTaskMapper.delete(FlwTask.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());

            // 记录当前审批任务
            flwTaskMapper.delete(FlwTask.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
            WxCpXmlApprovalInfo.SpRecord nextSpRecord = getNextSpRecord(cpXmlMessage);
            if (nextSpRecord != null) {
                List<FlwTask> flwTasks = buildTasks(flwInstance, cpXmlMessage, nextSpRecord, originator);
                flwTaskMapper.insertBatchSomeColumn(flwTasks);
            }

            // 记录历史审批任务
            flwHisTaskMapper.delete(FlwHisTaskPo.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
            List<FlwHisTaskPo> flwHisTasks = buildHisTasks(flwInstance, cpXmlMessage, originator);
            if (flwHisTasks != null) {
                flwHisTaskMapper.insertBatchSomeColumn(flwHisTasks);
            }

        }
        // 同意且已通过的情况
        else if (cpXmlMessage.getApprovalInfo().getSpStatus() == WxApprovalConst.SpStatus.PASSED) {
            // 删除当前审批实例
            LambdaUpdateWrapper<FlwInstance> deleteWrapper = new LambdaUpdateWrapper<>();
            deleteWrapper.eq(FlwInstance::getInstanceNo, cpXmlMessage.getApprovalInfo().getSpNo());
            flwInstanceMapper.delete(deleteWrapper);

            // 更新审批实例状态和结束时间
            LambdaUpdateWrapper<FlwHisInstance> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(FlwHisInstance::getEndTime, LocalDateTimeUtil.of(cpXmlMessage.getCreateTime() * 1000))
                    .set(FlwHisInstance::getInstanceStatus, cpXmlMessage.getApprovalInfo().getSpStatus())
                    .eq(FlwHisInstance::getInstanceNo, cpXmlMessage.getApprovalInfo().getSpNo());
            flwHisInstanceMapper.update(updateWrapper);

            // 删除当前审批任务
            flwTaskMapper.delete(FlwTask.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());

            // 记录历史审批任务
            flwHisTaskMapper.delete(FlwHisTaskPo.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
            List<FlwHisTaskPo> flwHisTasks = buildHisTasks(flwInstance, cpXmlMessage, originator);
            if (flwHisTasks != null) {
                flwHisTaskMapper.insertBatchSomeColumn(flwHisTasks);
            }
        }
    }

    /**
     * 处理企业微信审批驳回通知
     * statusChangeEvent=3 且 spStatus=3
     */
    private void wxApprovalReject(WxCpXmlMessage cpXmlMessage, SysUser originator) {
        log.info("审批：处理驳回信息");
        FlwInstance flwInstance = flwInstanceMapper.getOneOnly(FlwInstance.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        if (flwInstance == null) {
            // TODO 需要主动同步
            throw new RuntimeException("无法获取审批实例: " + cpXmlMessage.getApprovalInfo().getSpNo());
        }
        // 删除当前的审批实例
        LambdaUpdateWrapper<FlwInstance> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(FlwInstance::getInstanceNo, cpXmlMessage.getApprovalInfo().getSpNo());
        flwInstanceMapper.delete(deleteWrapper);

        // 更新审批实例状态和结束时间
        LambdaUpdateWrapper<FlwHisInstance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FlwHisInstance::getEndTime, LocalDateTimeUtil.of(cpXmlMessage.getCreateTime() * 1000))
                .set(FlwHisInstance::getInstanceStatus, cpXmlMessage.getApprovalInfo().getSpStatus())
                .eq(FlwHisInstance::getInstanceNo, cpXmlMessage.getApprovalInfo().getSpNo());
        flwHisInstanceMapper.update(updateWrapper);

        // 删除审批任务
        flwTaskMapper.delete(FlwTask.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());

        // 记录历史审批任务
        flwHisTaskMapper.delete(FlwHisTaskPo.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        List<FlwHisTaskPo> flwHisTasks = buildHisTasks(flwInstance, cpXmlMessage, originator);
        if (flwHisTasks != null) {
            flwHisTaskMapper.insertBatchSomeColumn(flwHisTasks);
        }
    }

    /**
     * 处理企业微信审批转审
     * statusChangeEvent=4 且 spStatus=4
     */
    private void changeAssignor(WxCpXmlMessage cpXmlMessage, SysUser originator) {
        log.info("审批：处理转审信息");
        FlwInstance flwInstance = flwInstanceMapper.getOneOnly(FlwInstance.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        if (flwInstance == null) {
            // TODO 需要主动同步
            throw new RuntimeException("无法获取审批实例: " + cpXmlMessage.getApprovalInfo().getSpNo());
        }
        // 删除完成的审批任务
        flwTaskMapper.delete(FlwTask.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());

        // 记录当前审批任务
        flwTaskMapper.delete(FlwTask.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        WxCpXmlApprovalInfo.SpRecord nextSpRecord = getNextSpRecord(cpXmlMessage);
        if (nextSpRecord != null) {
            List<FlwTask> flwTasks = buildTasks(flwInstance, cpXmlMessage, nextSpRecord, originator);
            flwTaskMapper.insertBatchSomeColumn(flwTasks);
        }

        // 记录历史审批任务
        flwHisTaskMapper.delete(FlwHisTaskPo.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        List<FlwHisTaskPo> flwHisTasks = buildHisTasks(flwInstance, cpXmlMessage, originator);
        if (flwHisTasks != null) {
            flwHisTaskMapper.insertBatchSomeColumn(flwHisTasks);
        }
    }

    /**
     * 处理企业微信审批撤销通知
     * statusChangeEvent=6 且 spStatus=4
     */
    private void wxApprovalRevoke(WxCpXmlMessage cpXmlMessage, SysUser originator) {
        log.info("审批：处理撤销信息");
        FlwInstance flwInstance = flwInstanceMapper.getOneOnly(FlwInstance.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        if (flwInstance == null) {
            // TODO 需要主动同步
            throw new RuntimeException("无法获取审批实例: " + cpXmlMessage.getApprovalInfo().getSpNo());
        }
        // 删除当前的审批实例
        LambdaUpdateWrapper<FlwInstance> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(FlwInstance::getInstanceNo, cpXmlMessage.getApprovalInfo().getSpNo());
        flwInstanceMapper.delete(deleteWrapper);

        // 更新审批实例状态和结束时间
        LambdaUpdateWrapper<FlwHisInstance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FlwHisInstance::getEndTime, LocalDateTimeUtil.of(cpXmlMessage.getCreateTime() * 1000))
                .set(FlwHisInstance::getInstanceStatus, cpXmlMessage.getApprovalInfo().getSpStatus())
                .eq(FlwHisInstance::getInstanceNo, cpXmlMessage.getApprovalInfo().getSpNo());
        flwHisInstanceMapper.update(updateWrapper);

        // 删除审批任务
        flwTaskMapper.delete(FlwTask.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());

        // 记录历史审批任务
        flwHisTaskMapper.delete(FlwHisTaskPo.builder().instanceNo(cpXmlMessage.getApprovalInfo().getSpNo()).build());
        List<FlwHisTaskPo> flwHisTasks = buildHisTasks(flwInstance, cpXmlMessage, originator);
        if (flwHisTasks != null) {
            flwHisTaskMapper.insertBatchSomeColumn(flwHisTasks);
        }
    }

    @Nullable
    private WxCpXmlApprovalInfo.SpRecord getNextSpRecord(WxCpXmlMessage cpXmlMessage) {
        return cpXmlMessage.getApprovalInfo().getSpRecords().stream().filter(
                spRecord -> spRecord.getDetails().stream().allMatch(detail -> detail.getSpStatus().equals(String.valueOf(WxApprovalConst.SpStatus.WAIT_APPROVE)))
        ).findFirst().orElse(null);
    }

    @Nullable
    private WxCpXmlApprovalInfo.Detail getLastSpDetail(WxCpXmlMessage cpXmlMessage) {
        return cpXmlMessage.getApprovalInfo().getSpRecords().stream()
                // 取出spDetail
                .flatMap(spRecord -> spRecord.getDetails().stream())
                // 拿到最近的一次审批（sp时间最近的）
                .max(Comparator.comparingLong(WxCpXmlApprovalInfo.Detail::getSpTime)).orElse(null);
    }

    private List<FlwTask> buildTasks(FlwInstance flwInstance, WxCpXmlMessage cpXmlMessage, WxCpXmlApprovalInfo.SpRecord spRecord, SysUser user) {
        return spRecord.getDetails().stream().map(detail -> {
            SysUser approver = userService.getUser(SysUser.builder().mainWxcpId(cpXmlMessage.getToUserName()).wxUserId(cpXmlMessage.getApprovalInfo().getApplier().getUserId()).build());
            if (approver == null) {
                throw new RuntimeException(StrUtil.format("系统中不存在此用户, 企业微信id: {}, 用户id: {}", cpXmlMessage.getToUserName(),
                        cpXmlMessage.getApprovalInfo().getApplier().getUserId()));
            }
            return wxApprovalBuilder.buildTask(flwInstance, user, approver);
        }).toList();
    }

    /**
     * @return 需要保存的历史任务
     */
    @Nullable
    private List<FlwHisTaskPo> buildHisTasks(FlwInstance flwInstance, WxCpXmlMessage cpXmlMessage, SysUser originator) {
        List<FlwHisTaskPo> hisTasks = new ArrayList<>();
        // 添加发起节点
        FlwHisTaskPo originatorTask = wxApprovalBuilder.buildHisTask(flwInstance, originator, originator, TaskStatus.COMMIT,
                cpXmlMessage.getApprovalInfo().getApplyTime());
        hisTasks.add(originatorTask);

        // 添加审批节点
        List<WxCpXmlApprovalInfo.SpRecord> hisSpRecord = getHisSpRecord(cpXmlMessage);
        if (hisSpRecord != null) {
            hisSpRecord.forEach(spRecord -> {
                // 将SpRecord已经审批的 但是 Detail还未审批的去掉，在多人或签情况下会出现
                List<WxCpXmlApprovalInfo.Detail> details =
                        spRecord.getDetails().stream().filter(detail ->
                                !(!spRecord.getSpStatus().equals(String.valueOf(WxApprovalConst.SpRecord.SpStatus.WAIT_APPROVE)) && detail.getSpStatus().equals(String.valueOf(WxApprovalConst.SpRecord.SpStatus.WAIT_APPROVE)))).toList();
                spRecord.setDetails(details);
            });
            List<FlwHisTaskPo> spTasks = hisSpRecord.stream().flatMap(spRecord -> spRecord.getDetails().stream())
                    .map(detail -> {
                        SysUser approver =
                                userService.getUser(SysUser.builder().mainWxcpId(cpXmlMessage.getToUserName()).wxUserId(detail.getApprover().getUserId()).build());
                        if (approver == null) {
                            throw new RuntimeException(StrUtil.format("系统中不存在此用户, 企业微信id: {}, 用户id: {}", cpXmlMessage.getToUserName(),
                                    cpXmlMessage.getApprovalInfo().getApplier().getUserId()));
                        }
                        return wxApprovalBuilder.buildHisTask(flwInstance, originator, approver,
                                WxApprovalHelper.convertTaskStatus(Integer.valueOf(detail.getSpStatus())), detail.getSpTime());
                    }).toList();
            hisTasks.addAll(spTasks);
        }

        // 添加撤回节点
        if (cpXmlMessage.getApprovalInfo().getSpStatus().equals(WxApprovalConst.SpStatus.REVOKED)) {
            FlwHisTaskPo revokeTask = wxApprovalBuilder.buildHisTask(flwInstance, originator, originator,
                    TaskStatus.REVOKED, cpXmlMessage.getCreateTime());
            hisTasks.add(revokeTask);
        }

        return hisTasks;
    }

    /**
     * @param cpXmlMessage 审批回调消息
     * @return 需要记录到历史任务表中的 spRecord
     */
    @Nullable
    private List<WxCpXmlApprovalInfo.SpRecord> getHisSpRecord(WxCpXmlMessage cpXmlMessage) {
        List<WxCpXmlApprovalInfo.SpRecord> spRecords = cpXmlMessage.getApprovalInfo().getSpRecords();
        if (spRecords.size() == 0) {
            return null;
        }
        Integer spStatus = cpXmlMessage.getApprovalInfo().getSpStatus();
        int index = -1;
        // 审批中，需要保存截止到当前的审批节点
        switch (spStatus) {
            case WxApprovalConst.SpStatus.WAIT_APPROVE -> {
                for (int i = 0; i < cpXmlMessage.getApprovalInfo().getSpRecords().size(); i++) {
                    if (spRecords.get(i).getSpStatus().equals(String.valueOf(WxApprovalConst.SpRecord.SpStatus.WAIT_APPROVE))) {
                        index = i;
                        break;
                    }
                }
            }
            case WxApprovalConst.SpStatus.PASSED -> {
                index = spRecords.size() - 1;
            }
            case WxApprovalConst.SpStatus.REJECTED -> {
                for (int i = 0; i < cpXmlMessage.getApprovalInfo().getSpRecords().size(); i++) {
                    if (spRecords.get(i).getSpStatus().equals(String.valueOf(WxApprovalConst.SpRecord.SpStatus.REJECTED))) {
                        index = i;
                        break;
                    }
                }
            }
            case WxApprovalConst.SpStatus.REVOKED -> {
                for (int i = 0; i < cpXmlMessage.getApprovalInfo().getSpRecords().size(); i++) {
                    if (spRecords.get(i).getSpStatus().equals(String.valueOf(WxApprovalConst.SpRecord.SpStatus.WAIT_APPROVE))) {
                        index = i - 1;
                        break;
                    }
                }
            }
            default -> {
                throw new RuntimeException("不支持的审批回调状态: 通过后撤销");
            }
        }
        if (index == -1) {
            log.debug("没有获取到需要保存的历史审批任务");
            return null;
        }
        return spRecords.subList(0, index + 1);
    }

}
