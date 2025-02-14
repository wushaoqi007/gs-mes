package com.greenstone.mes.external.domain.entity.node;

import com.greenstone.mes.external.infrastructure.enums.FlowNodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/24 13:51
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FlowNode {

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 上一个节点id
     */
    private String prevId;

    /**
     * 节点标题
     */
    private String title;

    /**
     * 节点类型
     */
    private FlowNodeType type;

    /**
     * 节点展示内容
     */
    private String content;

    /**
     * 子节点
     */
    private List<FlowNode> childNodes;

    /**
     * 条件子节点 子节点有条件分支时有此属性
     */
    private List<FlowNode> conditionNodes;

    /**
     * 节点参数
     */
    private NodeParams nodeParams;
}
