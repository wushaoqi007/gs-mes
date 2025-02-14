package com.greenstone.mes.external.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.infrastructure.enums.FlowNodeType;
import lombok.*;

import java.io.Serial;

/**
 * @author gu_renkai
 * @date 2023/3/2 11:36
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("flow_node_definition")
public class NodeDefinitionDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 3765744993132067849L;
    @TableId
    private String nodeId;
    @TableField
    private FlowNodeType nodeType;
    @TableField
    private String paramsJson;
    @TableField
    private String processDefinitionId;
    @TableField
    private String formId;

}
