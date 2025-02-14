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
@TableName("form_definition")
public class FormDefinitionDo extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String formId;
    private String defaultJson;
    private String customJson;
    private String systemJson;

}
