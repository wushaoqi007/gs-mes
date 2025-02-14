package com.greenstone.mes.form.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * @author gu_renkai
 * @date 2023/3/3 13:40
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("form")
public class FormDo extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String formId;
    private String formName;
    private Integer formSource;
    private boolean usingProcess;
    private String icon;
    private String processDefinitionId;
    private String formDefinitionId;
    private String defaultJson;
    private String customJson;

}
