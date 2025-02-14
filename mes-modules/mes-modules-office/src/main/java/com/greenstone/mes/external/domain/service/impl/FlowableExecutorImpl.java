package com.greenstone.mes.external.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.external.domain.dto.cmd.FlwDeployCmd;
import com.greenstone.mes.external.domain.dto.cmd.FlwTaskRunCmd;
import com.greenstone.mes.external.domain.dto.result.FlwDeployResult;
import com.greenstone.mes.external.domain.service.FlowableExecutor;
import com.greenstone.mes.external.dto.cmd.TaskRunCmd;
import com.greenstone.mes.external.dto.result.ProcInstStartResult;
import com.greenstone.mes.external.dto.result.TaskRunResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2023/3/1 8:40
 */
@AllArgsConstructor
@Slf4j
@Service
public class FlowableExecutorImpl implements FlowableExecutor {

//    private final RepositoryService repositoryService;
//    private final RuntimeService runtimeService;
//    private final TaskService taskService;
//
//    @Override
//    public Task getTask(String taskId) {
//        return taskService.createTaskQuery().taskId(taskId).singleResult();
//    }
//
//    @Override
//    public List<IdentityLink> getIdentityLinksForTask(String taskId) {
//        return taskService.getIdentityLinksForTask(taskId);
//    }
//
//
//    @Override
//    public FlwDeployResult save(FlwDeployCmd defDeployCmd) {
//        Deployment deployment = repositoryService.createDeployment()
//                .addString(defDeployCmd.getResourceName(), defDeployCmd.getProcessText())
//                .name(defDeployCmd.getProcessName()).deploy();
//
//        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
//                .deploymentId(deployment.getId())
//                .processDefinitionKey(defDeployCmd.getProcessKey())
//                .processDefinitionName(defDeployCmd.getProcessName()).latestVersion()
//                .singleResult();
//
//        return FlwDeployResult.builder().processId(definition.getId()).deploymentId(deployment.getId())
//                .processKey(definition.getKey()).processName(definition.getName())
//                .version(definition.getVersion()).build();
//    }
//
//    @Override
//    public ProcInstStartResult start(String procDefId, Map<String, Object> variables) {
//        ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, variables);
//        return ProcInstStartResult.builder().processInstanceId(processInstance.getId()).build();
//    }
//
//
//    public Task run(FlwTaskRunCmd runCmd) {
//        Task task = taskService.createTaskQuery().processInstanceId(runCmd.getProcessInstanceId())
//                .taskId(runCmd.getTaskId()).singleResult();
//        if (StrUtil.isNotEmpty(runCmd.getComment())) {
//            taskService.addComment(task.getId(), task.getProcessInstanceId(), runCmd.getComment());
//        }
//        taskService.complete(task.getId(), runCmd.getVariables());
//        return task;
//    }
//
//    @Override
//    public TaskRunResult runTask(TaskRunCmd runCmd) {
//        Task task = taskService.createTaskQuery().processInstanceId(runCmd.getProcessInstanceId())
//                .taskId(runCmd.getTaskId()).singleResult();
//        if (task == null) {
//            return TaskRunResult.builder().success(false).errMsg("Task run failed: task is not exist.").build();
//        } else {
//            taskService.addComment(task.getId(), task.getProcessInstanceId(), runCmd.getComment());
//            taskService.complete(task.getId(), runCmd.getVariables());
//            return TaskRunResult.builder().success(true).processDefinitionId(task.getProcessDefinitionId())
//                    .processInstanceId(runCmd.getProcessInstanceId()).taskId(runCmd.getTaskId())
//                    .taskDefinitionKey(task.getTaskDefinitionKey()).build();
//        }
//    }
//
//    @Override
//    public List<IdentityLink> getIdentityLinks(String taskId) {
//        return taskService.getIdentityLinksForTask(taskId);
//    }
//
//    @Override
//    public TaskInfo getActiveTask(String processInstanceId) {
//        return taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
//    }
//
//    @Override
//    public void deleteProcessInstance(String processInstanceId, String deleteReason) {
//        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
//    }
}
