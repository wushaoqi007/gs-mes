package com.greenstone.mes.external.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author gu_renkai
 * @date 2023/3/1 13:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("flow_process_definition")
public class ProcessDefinitionDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -5526521120449462727L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @TableField
    private String formId;
    @TableField
    private String formName;
    @TableField
    private String processDefinitionId;
    @TableField
    private String processDefinitionKey;
    @TableField
    private String processDefinitionName;
    @TableField
    private Integer version;
    @TableField
    private String jsonContent;
    @TableField
    private String xmlContent;


}
