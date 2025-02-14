package com.greenstone.mes.office.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("custom_param")
public class CustomParam extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String moduleCode;

    private String paramKey;

    private String paramValue;

}
