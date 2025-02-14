package com.greenstone.mes.external.domain.repository;

import com.greenstone.mes.external.domain.converter.ProcessConverter;
import com.greenstone.mes.external.domain.entity.NodeDefinition;
import com.greenstone.mes.external.infrastructure.mapper.NodeDefinitionMapper;
import com.greenstone.mes.external.infrastructure.persistence.NodeDefinitionDO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/2 13:02
 */
@AllArgsConstructor
@Service
public class NodeDefinitionRepository {

    private final NodeDefinitionMapper nodeDefinitionMapper;
    private final ProcessConverter converter;

    public NodeDefinition get(String nodeId, String processDefinitionId) {
        NodeDefinitionDO nodeDefinitionDO = nodeDefinitionMapper.getOneOnly(
                NodeDefinitionDO.builder().nodeId(nodeId).processDefinitionId(processDefinitionId).build());
        return converter.toNodeDef(nodeDefinitionDO);
    }

    public void save(List<NodeDefinition> nodeDefinitionList) {
        nodeDefinitionMapper.insertBatchSomeColumn(converter.toNodeDefDOList(nodeDefinitionList));
    }

}
