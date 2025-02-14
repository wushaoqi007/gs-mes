package com.greenstone.mes.external.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.external.application.dto.cmd.CopyAddCmd;
import com.greenstone.mes.external.application.dto.cmd.CopyStatusChangeCmd;
import com.greenstone.mes.external.application.dto.cmd.FlowNoticeCmd;
import com.greenstone.mes.external.application.dto.query.TaskQ;
import com.greenstone.mes.external.application.dto.result.ProcessCopyResult;
import com.greenstone.mes.external.application.dto.result.TaskResult;
import com.greenstone.mes.external.application.service.ProcessNoticeService;
import com.greenstone.mes.external.application.service.ProcessTaskService;
import com.greenstone.mes.external.domain.converter.ProcessConverter;
import com.greenstone.mes.external.domain.entity.*;
import com.greenstone.mes.external.domain.entity.node.Assignee;
import com.greenstone.mes.external.domain.entity.node.NodeParams;
import com.greenstone.mes.external.domain.repository.*;
import com.greenstone.mes.external.domain.service.FlowableExecutor;
import com.greenstone.mes.external.dto.cmd.ProcessCmd;
import com.greenstone.mes.external.dto.cmd.TaskRunCmd;
import com.greenstone.mes.external.dto.result.TaskRunResult;
import com.greenstone.mes.external.enums.TaskStatus;
import com.greenstone.mes.external.infrastructure.enums.CopyHandleStatus;
import com.greenstone.mes.external.infrastructure.enums.FlowNodeType;
import com.greenstone.mes.external.infrastructure.enums.NoticeWay;
import com.greenstone.mes.external.infrastructure.enums.NotificationTarget;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author gu_renkai
 * @date 2023/3/2 13:07
 */
@AllArgsConstructor
@Slf4j
@Service
public class ProcessTaskServiceImpl implements ProcessTaskService {

    private final NodeDefinitionRepository nodeDefinitionRepository;
    private final TaskRepository taskRepository;
    private final ProcessInstanceRepository processInstanceRepository;
    private final TaskIdentityLinkRepository taskIdentityLinkRepository;
    private final ProcessCopyRepository processCopyRepository;
    private final FlowableExecutor flowableExecutor;
    private final ProcessConverter converter;
    private final RemoteSystemService systemService;
    private final RemoteUserService userService;
    private final ProcessNoticeService processNoticeService;


    @Override
    public List<TaskResult> taskList(TaskQ taskQ) {
        taskQ.setCurrUser(SecurityUtils.getLoginUser().getUser().getUserId());
        List<ProcessTask> tasks = taskRepository.list(taskQ);
        return converter.toTaskRs(tasks);
    }

    /**
     * 执行审批操作
     */
    @Override
    @Transactional
    public TaskRunResult runTask(ProcessCmd runCmd) {
        for (ProcessCmd.Runner runner : runCmd.getRunners()) {
            // 获取任务实例信息
            ProcessInstance processInstance = processInstanceRepository.get(runner.getProcessInstanceId());
            if (processInstance == null) {
                return TaskRunResult.builder().success(false).errMsg("Task run failed: process instance is not exist.").build();
            }
            // 组装参数
            Map<String, Object> variables = new HashMap<>();
            variables.put("approved", runCmd.isApproved());
            TaskRunCmd taskRunCmd = TaskRunCmd.builder().comment(runCmd.getComment())
                    .processInstanceId(runner.getProcessInstanceId())
                    .taskId(runner.getTaskId()).variables(variables).build();
            // 执行任务
            runTask(taskRunCmd);
        }
        return null;
    }

    @Override
    public TaskRunResult runTask(TaskRunCmd taskRunCmd) {
//        TaskRunResult taskRunResult = flowableExecutor.runTask(taskRunCmd);
//        if (taskRunCmd.getVariables() != null) {
//            boolean approved = Boolean.parseBoolean(taskRunCmd.getVariables().getOrDefault("approved", "false").toString());
//            taskRunResult.setApproved(approved);
//        }
//        // 获取活动的任务信息，本次任务已经提交，所以获取的是下一个任务的信息
//        TaskInfo nextTask = flowableExecutor.getActiveTask(taskRunCmd.getProcessInstanceId());
//        if (nextTask != null) {
//            taskRunResult.setNextTaskId(nextTask.getId());
//            taskRunResult.setNextTaskDefinitionKey(nextTask.getTaskDefinitionKey());
//        }
//        // 发布任务执行事件，记录审批结果和下一个任务的信息
//        afterTaskRun(taskRunCmd, taskRunResult, nextTask);
//        taskRunResult.setSuccess(true);
//        return taskRunResult;
        return null;
    }


//    private void afterTaskRun(TaskRunCmd taskRunCmd, TaskRunResult taskRunResult, TaskInfo nextTask) {
//        NodeDefinition nodeDefinition = nodeDefinitionRepository.get(taskRunResult.getTaskDefinitionKey(), taskRunResult.getProcessDefinitionId());
//        ProcessInstance processInstance = processInstanceRepository.get(taskRunResult.getProcessInstanceId());
//        // 若是审批节点，则更新审批结果
//        if (nodeDefinition.getNodeType() == FlowNodeType.APPROVE) {
//            // 保存表单任务
//            ProcessTask task = new ProcessTask();
//            task.setTaskId(taskRunResult.getTaskId());
//            task.setSerialNo(taskRunCmd.getSerialNo());
//            task.setApprovedBy(SecurityUtils.getLoginUser().getUser().getUserId());
//            task.setApprovedByName(SecurityUtils.getLoginUser().getUser().getNickName());
//            task.setApprovedTime(LocalDateTime.now());
//            task.setComment(taskRunCmd.getComment());
//
//            task.setTaskStatus(taskRunResult.isApproved() ? TaskStatus.APPROVED : TaskStatus.REJECTED);
//            taskRepository.saveApproved(task);
//        }
//        // 删除完成的任务的身份关系
//        taskIdentityLinkRepository.removeLinks(taskRunResult.getTaskId());
//
//        NodeParams nodeParams = JSON.parseObject(nodeDefinition.getParamsJson(), NodeParams.class);
//
//
//        // 申请驳回通知
//        if (!taskRunResult.isApproved() && nodeParams != null && CollUtil.isNotEmpty(nodeParams.getNotifications())) {
//            for (NodeParams.Notification notification : nodeParams.getNotifications()) {
//                if (notification.getTarget() == NotificationTarget.APPROVAL_BY) {
//                    SysUser userInfo = systemService.getUserPublicInfo(processInstance.getAppliedBy());
//                    if (userInfo == null) {
//                        log.warn("需要进行通知的发起人不存在 {} {}", processInstance.getAppliedBy(), processInstance.getAppliedByName());
//                        break;
//                    }
//                    String title = "申请驳回通知";
//                    String subTitle = "您的申请已被驳回";
//                    String content = StrUtil.format("您的申请已被驳回，{} {}", processInstance.getFormName(), processInstance.getSerialNo());
//
//                    FlowNoticeCmd noticeCmd = FlowNoticeCmd.builder().way(notification.getWay())
//                            .title(title)
//                            .subTitle(subTitle)
//                            .content(content)
//                            .serialNo(processInstance.getSerialNo())
//                            .billTypeName(processInstance.getFormName()).build();
//                    processNoticeService.sendNotice(noticeCmd, Collections.singletonList(userInfo));
//                }
//            }
//
//        }
//
//        // 若有下一个审批任务，则增加一个待处理的审批任务
//        if (nextTask != null) {
//            NodeDefinition nextNodeDefinition = nodeDefinitionRepository.get(nextTask.getTaskDefinitionKey(), nextTask.getProcessDefinitionId());
//            NodeParams nextNodeParams = JSON.parseObject(nextNodeDefinition.getParamsJson(), NodeParams.class);
//            if (nextNodeParams != null && nextNodeDefinition.getNodeType() == FlowNodeType.APPROVE) {
//                // 保存表单任务
//                ProcessTask task = converter.toTask(processInstance);
//                task.setSerialNo(taskRunCmd.getSerialNo());
//                task.setTaskId(nextTask.getId());
//                task.setTaskStatus(TaskStatus.PENDING);
//                taskRepository.add(task);
//
//                List<IdentityLink> nextIdentityLinks = flowableExecutor.getIdentityLinksForTask(nextTask.getId());
//
//                if (CollUtil.isNotEmpty(nextIdentityLinks)) {
//                    List<TaskIdentityLink> identityLinks = new ArrayList<>();
//                    for (IdentityLink identityLink : nextIdentityLinks) {
//                        Long userId = Long.valueOf(identityLink.getUserId());
//
//                        TaskIdentityLink theIdentityLink = TaskIdentityLink.builder().taskId(task.getTaskId())
//                                .type(nextNodeParams.getApproveType())
//                                .userId(userId)
//                                .userName(userService.getNickName(userId, SecurityConstants.INNER)).build();
//                        identityLinks.add(theIdentityLink);
//                    }
//                    taskIdentityLinkRepository.saveLinks(identityLinks);
//
//
//                    String title = "收到了新的审批单";
//                    String subTitle = "您有新的待审批单据";
//                    String content = StrUtil.format("收到了新的审批单，{} {}", processInstance.getFormName(), processInstance.getSerialNo());
//                    List<SysUser> users = nextIdentityLinks.stream().map(id -> SysUser.builder().userId(Long.valueOf(id.getUserId())).build()).toList();
//
//                    FlowNoticeCmd noticeCmd = FlowNoticeCmd.builder().way(NoticeWay.WX_WORK_MSG)
//                            .title(title)
//                            .subTitle(subTitle)
//                            .content(content)
//                            .serialNo(processInstance.getSerialNo())
//                            .billTypeName(processInstance.getFormName()).build();
//                    try {
//                        processNoticeService.sendNotice(noticeCmd, users);
//                    } catch (Exception e) {
//                        log.warn("市购件提交通知失败: {}", e.getMessage());
//                    }
//                }
//
//            }
//        }
//    }

    @Override
    public List<TaskResult> pendingTasks() {
        List<ProcessTask> pendingTasks = taskRepository.pendingTasks(SecurityUtils.getLoginUser().getUser().getUserId());
        return converter.toTaskRs(pendingTasks);
    }

    @Override
    public List<TaskResult> processedTasks() {
        List<ProcessTask> processedTasks = taskRepository.processedTasks(SecurityUtils.getLoginUser().getUser().getUserId());
        return converter.toTaskRs(processedTasks);
    }

    /**
     * 执行抄送操作
     */
    @Override
    public void addCopy(CopyAddCmd copyAddCmd) {
        ProcessInstance processInstance = processInstanceRepository.get(copyAddCmd.getProcessInstanceId());
        NodeDefinition nodeDefinition = nodeDefinitionRepository.get(copyAddCmd.getNodeId(), copyAddCmd.getProcessDefinitionId());
        NodeParams nodeParams = JSON.parseObject(nodeDefinition.getParamsJson(), NodeParams.class);
        for (Assignee assignee : nodeParams.getAssignees()) {
            ProcessCopy copy = ProcessCopy.builder().handleStatus(CopyHandleStatus.NEW)
                    .serialNo(processInstance.getSerialNo())
                    .billType(processInstance.getFormId())
                    .userId(assignee.getUserId())
                    .appliedBy(processInstance.getAppliedBy())
                    .appliedByName(processInstance.getAppliedByName())
                    .appliedTime(processInstance.getAppliedTime()).build();
            processCopyRepository.save(copy);
        }

        String title = "审批抄送通知";
        String subTitle = "审批抄送通知";
        String content = StrUtil.format("您收到了新的抄送单据，{} {}", processInstance.getFormName(), processInstance.getSerialNo());
        List<SysUser> noticeUsers = new ArrayList<>();
        for (Assignee assignee : nodeParams.getAssignees()) {
            SysUser userInfo = systemService.getUserPublicInfo(assignee.getUserId());
            if (userInfo == null) {
                log.warn("需要进行通知的抄送人不存在 {} {}", assignee.getUserId(), assignee.getUserName());
                continue;
            }
            noticeUsers.add(userInfo);
        }
        FlowNoticeCmd noticeCmd = FlowNoticeCmd.builder().way(NoticeWay.WX_WORK_MSG)
                .title(title)
                .subTitle(subTitle)
                .content(content)
                .serialNo(processInstance.getSerialNo())
                .billTypeName(processInstance.getFormName()).build();
        processNoticeService.sendNotice(noticeCmd, noticeUsers);
    }

    @Override
    public List<ProcessCopyResult> currUserCopies() {
        return processCopyRepository.copies();
    }

    @Override
    public void copyHandleStatusChange(CopyStatusChangeCmd changeCmd) {
        processCopyRepository.changeStatus(changeCmd);
    }

    @Override
    public void revokeTask(String processInstanceId) {
        List<String> revokeTaskIds = taskRepository.revoke(processInstanceId);
        taskIdentityLinkRepository.revoke(revokeTaskIds);
    }

}
