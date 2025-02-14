package com.greenstone.mes.external.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.external.domain.converter.ProcessConverter;
import com.greenstone.mes.external.domain.entity.TaskIdentityLink;
import com.greenstone.mes.external.infrastructure.mapper.TaskIdentityLinkMapper;
import com.greenstone.mes.external.infrastructure.persistence.TaskIdentityLinkDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/7 15:41
 */
@AllArgsConstructor
@Slf4j
@Service
public class TaskIdentityLinkRepository {

    private final TaskIdentityLinkMapper taskIdentityLinkMapper;
    private final ProcessConverter converter;

    public void saveLinks(List<TaskIdentityLink> links) {
        taskIdentityLinkMapper.insertBatchSomeColumn(converter.toTaskIdentityLinkDOs(links));
    }

    public List<TaskIdentityLink> findLinks(String taskId) {
        List<TaskIdentityLinkDO> linkDOS = taskIdentityLinkMapper.list(TaskIdentityLinkDO.builder().taskId(taskId).build());
        return converter.toTaskIdentityLinks(linkDOS);
    }

    public void removeLinks(String taskId) {
        TaskIdentityLinkDO taskIdentityLinkDO = taskIdentityLinkMapper.getOneOnly(TaskIdentityLinkDO.builder().taskId(taskId).build());
        if (taskIdentityLinkDO != null) {
            taskIdentityLinkMapper.deleteById(taskIdentityLinkDO.getId());
        }
    }

    public void revoke(List<String> taskIds) {
        LambdaQueryWrapper<TaskIdentityLinkDO> queryWrapper = Wrappers.lambdaQuery(TaskIdentityLinkDO.class).in(TaskIdentityLinkDO::getTaskId, taskIds);
        taskIdentityLinkMapper.delete(queryWrapper);
    }

}
