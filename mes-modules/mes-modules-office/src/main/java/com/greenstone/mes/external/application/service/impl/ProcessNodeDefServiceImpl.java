package com.greenstone.mes.external.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.external.domain.repository.NodeDefinitionRepository;
import com.greenstone.mes.external.application.service.ProcessNodeDefService;
import com.greenstone.mes.external.domain.entity.NodeDefinition;
import com.greenstone.mes.external.domain.entity.node.FlowNode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/2 13:59
 */
@AllArgsConstructor
@Service
public class ProcessNodeDefServiceImpl implements ProcessNodeDefService {

    private final NodeDefinitionRepository nodeDefinitionRepository;

    @Override
    public void save(FlowNode flowNode, String billTypeId, String procdefId) {
        List<NodeDefinition> nodeDefinitionList = new ArrayList<>();
        addNodeDef(flowNode, procdefId, billTypeId, nodeDefinitionList);
        nodeDefinitionRepository.save(nodeDefinitionList);
    }

    private void addNodeDef(FlowNode flowNode, String procDefId, String billType, List<NodeDefinition> nodeDefinitionList) {
        NodeDefinition nodeDefinition = NodeDefinition.builder().nodeId(flowNode.getNodeId())
                .processDefinitionId(procDefId)
                .formId(billType)
                .nodeType(flowNode.getType())
                .paramsJson(JSON.toJSONString(flowNode.getNodeParams())).build();
        nodeDefinitionList.add(nodeDefinition);

        if (CollUtil.isNotEmpty(flowNode.getConditionNodes())) {
            for (FlowNode conditionNode : flowNode.getConditionNodes()) {
                addNodeDef(conditionNode, procDefId, billType, nodeDefinitionList);
            }
        }

        if (CollUtil.isNotEmpty(flowNode.getChildNodes())) {
            for (FlowNode child : flowNode.getChildNodes()) {
                addNodeDef(child, procDefId, billType, nodeDefinitionList);
            }
        }
    }


}
