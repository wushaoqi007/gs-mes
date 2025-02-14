package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user_param")
public class UserParamDo extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotEmpty(message = "表单类型不能为空")
    private String billType;

    private Long userId;

    private String paramKey;

    private String paramValue;

}
