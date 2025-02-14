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
@TableName("flow_hi_process_definition")
public class HiProcessDefinitionDo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -5526521120449462727L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String formId;
    private String formName;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private Integer version;
    private String jsonContent;
    private String xmlContent;


}
