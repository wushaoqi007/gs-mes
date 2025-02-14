package com.greenstone.mes.external.domain.repository;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.external.application.dto.query.TaskQ;
import com.greenstone.mes.external.domain.converter.ProcessConverter;
import com.greenstone.mes.external.domain.entity.ProcessTask;
import com.greenstone.mes.external.enums.TaskStatus;
import com.greenstone.mes.external.infrastructure.mapper.TaskMapper;
import com.greenstone.mes.external.infrastructure.persistence.TaskDO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/2 13:13
 */
@AllArgsConstructor
@Service
public class TaskRepository {

    private final TaskMapper taskMapper;
    private final ProcessConverter converter;

    public List<ProcessTask> pendingTasks(Long userId) {
        List<TaskDO> pendingTasks = taskMapper.pendingTasks(userId);
        return converter.toTasks(pendingTasks);
    }

    public List<ProcessTask> processedTasks(Long userId) {
        LambdaQueryWrapper<TaskDO> queryWrapper = Wrappers.lambdaQuery(TaskDO.class)
                .in(TaskDO::getTaskStatus, List.of(TaskStatus.APPROVED, TaskStatus.REJECTED))
                .eq(TaskDO::getApprovedBy, userId)
                .orderByDesc(TaskDO::getCreateTime);
        return converter.toTasks(taskMapper.selectList(queryWrapper));
    }

    public List<ProcessTask> list(TaskQ taskQ) {
        LambdaQueryWrapper<TaskDO> queryWrapper = Wrappers.lambdaQuery(TaskDO.class)
                .in(TaskDO::getTaskStatus, taskQ.getTaskState());
        return converter.toTasks(taskMapper.selectList(queryWrapper));
    }

    public void saveApproved(ProcessTask task) {
        TaskDO taskDO = converter.toTaskDO(task);
        taskMapper.updateById(taskDO);
    }

    public void add(ProcessTask task) {
        TaskDO taskDO = converter.toTaskDO(task);
        taskMapper.insert(taskDO);
    }

    public List<String> revoke(String processInstanceId) {
        LambdaQueryWrapper<TaskDO> queryWrapper = Wrappers.lambdaQuery(TaskDO.class).eq(TaskDO::getProcessInstanceId, processInstanceId);
        List<TaskDO> taskDOS = taskMapper.selectList(queryWrapper);
        taskMapper.delete(queryWrapper);
        return CollUtil.isEmpty(taskDOS) ? Collections.emptyList() : taskDOS.stream().map(TaskDO::getTaskId).toList();
    }
}
