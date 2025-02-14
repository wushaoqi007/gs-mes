package com.greenstone.mes.external.application.service.impl;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.external.application.dto.query.ProcDefQuery;
import com.greenstone.mes.external.application.dto.result.ProcessDefinitionResult;
import com.greenstone.mes.external.application.service.ProcessDefinitionService;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.application.service.ProcessTaskService;
import com.greenstone.mes.external.domain.entity.ProcessInstance;
import com.greenstone.mes.external.domain.repository.ProcessInstanceRepository;
import com.greenstone.mes.external.domain.service.FlowableExecutor;
import com.greenstone.mes.external.dto.cmd.*;
import com.greenstone.mes.external.dto.result.ProcInstStartResult;
import com.greenstone.mes.external.dto.result.ProcessRevokeResult;
import com.greenstone.mes.external.dto.result.ProcessRunResult;
import com.greenstone.mes.external.dto.result.TaskRunResult;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2023/3/2 14:45
 */
@AllArgsConstructor
@Slf4j
@Service
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    private final ProcessDefinitionService processDefinitionService;
    private final ProcessInstanceRepository processInstanceRepository;
    private final FlowableExecutor flowableExecutor;
    private final ProcessTaskService processTaskService;

    /**
     * 创建并运行一次流程
     */
    @Override
    @Transactional
    public ProcessRunResult createAndRun(@Validated ProcessStartCmd startCmd) {
        // 装配参数
        Map<String, Object> variables = null;
        if (startCmd.getAssigneeList() != null) {
            variables = new HashMap<>();
            if (startCmd.getAssigneeList().size() > 1) {
                variables.put("assigneeList", startCmd.getAssigneeList());
            } else {
                variables.put("assignee", startCmd.getAssigneeList().get(0));
            }
        }
        startCmd.setVariables(variables);
        // 启动流程实例
        ProcInstStartResult procInstStartResult = createProcess(startCmd);
        // 执行一次流程
        ProcessRunCmd processRunCmd = ProcessRunCmd.builder().processInstanceId(procInstStartResult.getProcessInstanceId())
                .serialNo(startCmd.getSerialNo())
                .comment(startCmd.getComment())
                .variables(variables).build();
        ProcessRunResult processRunResult = runProcess(processRunCmd);
        return processRunResult;
    }

    /**
     * 创建流程
     */
    @Override
    @Transactional
    public ProcInstStartResult createProcess(ProcessStartCmd startCmd) {
        log.info("start process instance: {}", startCmd);
        ProcessDefinitionResult processDefinitionResult = processDefinitionService.get(ProcDefQuery.builder().formId(startCmd.getFormId()).build());
        if (processDefinitionResult == null) {
            throw new ServiceException("选择的流程定义不存在，请重新选择");
        }
//        ProcInstStartResult startResult = flowableExecutor.start(processDefinitionResult.getProcessDefinitionId(), startCmd.getVariables());
//
//        ProcessInstance processInstance = ProcessInstance.builder().processInstanceId(startResult.getProcessInstanceId())
//                .formId(startCmd.getFormId())
//                .formName(startCmd.getFormName())
//                .serviceName(startCmd.getServiceName())
//                .appliedBy(SecurityUtils.getLoginUser().getUser().getUserId())
//                .appliedByName(SecurityUtils.getLoginUser().getUser().getNickName())
//                .appliedTime(LocalDateTime.now())
//                .processStatus(ProcessStatus.APPROVING)
//                .serialNo(startCmd.getSerialNo()).build();
//        processInstanceRepository.add(processInstance);
//
//        return ProcInstStartResult.builder().processInstanceId(startResult.getProcessInstanceId()).build();
        return null;
    }

    @Transactional
    @Override
    public List<ProcessRunResult> runProcess(ProcessCmd processCmd) {
        List<ProcessRunResult> results = new ArrayList<>();
        for (ProcessCmd.Runner runner : processCmd.getRunners()) {
            // 获取任务实例信息
            ProcessInstance processInstance = processInstanceRepository.get(runner.getProcessInstanceId());
            if (processInstance == null) {
                results.add(ProcessRunResult.builder().success(false).errMsg("Task run failed: process instance is not exist.").build());
            }
            // 组装参数
            Map<String, Object> variables = new HashMap<>();
            variables.put("approved", processCmd.isApproved());
            ProcessRunCmd taskRunCmd = ProcessRunCmd.builder().comment(processCmd.getComment())
                    .processInstanceId(processInstance.getProcessInstanceId())
                    .serialNo(processInstance.getSerialNo())
                    .variables(variables).build();
            // 执行任务
            ProcessRunResult processRunResult = runProcess(taskRunCmd);
            processRunResult.setFormId(processInstance.getFormId());
            processRunResult.setFormName(processInstance.getFormName());
            processRunResult.setServiceName(processInstance.getServiceName());
            results.add(processRunResult);
        }
        return results;
    }

    /**
     * 运行一次流程
     */
    @Transactional
    public ProcessRunResult runProcess(ProcessRunCmd runCmd) {
//        TaskInfo currTask = flowableExecutor.getActiveTask(runCmd.getProcessInstanceId());
//        // 若流程已经没有活动的任务，则此流程已经结束
//        if (currTask == null) {
//            return ProcessRunResult.builder().success(false).complete(true).errMsg("该流程已结束。").build();
//        } else {
//            TaskRunCmd taskRunCmd = TaskRunCmd.builder().processInstanceId(runCmd.getProcessInstanceId())
//                    .taskId(currTask.getId()).comment(runCmd.getComment())
//                    .serialNo(runCmd.getSerialNo())
//                    .variables(runCmd.getVariables()).build();
//            TaskRunResult taskRunResult = processTaskService.runTask(taskRunCmd);
//
//            return ProcessRunResult.builder().processInstanceId(runCmd.getProcessInstanceId())
//                    .currentTaskId(currTask.getId())
//                    .success(taskRunResult.isSuccess())
//                    .errMsg(taskRunResult.getErrMsg())
//                    .nextTaskId(taskRunResult.getNextTaskId())
//                    .serialNo(runCmd.getSerialNo())
//                    .comment(runCmd.getComment())
//                    .approved(taskRunResult.isApproved())
//                    .complete(taskRunResult.getNextTaskId() == null).build();
//        }
        return null;
    }

    @Transactional
    @Override
    public ProcessRevokeResult revokeProcess(ProcessRevokeCmd revokeCmd) {
//        flowableExecutor.deleteProcessInstance(revokeCmd.getProcessInstanceId(), "用户撤回");
//        ProcessInstance processInstance = processInstanceRepository.get(revokeCmd.getProcessInstanceId());
//        if (ProcessStatus.APPROVING != processInstance.getProcessStatus()) {
//            throw new ServiceException("只能撤回审批中状态的单据。");
//        }
//        processInstanceRepository.revoke(revokeCmd.getProcessInstanceId());
//        processTaskService.revokeTask(revokeCmd.getProcessInstanceId());
//        return ProcessRevokeResult.builder().success(true).serialNo(processInstance.getSerialNo()).build();
        return null;
    }

}
