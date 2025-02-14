package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:18
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "sys_param_type")
public class ParamTypeDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -4917107967046745994L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String paramType;
    private String paramName;
    @TableField("is_multilevel")
    private Boolean multilevel;
    private Integer levels;
    private String status;
    private String remark;
    private Long deptId;
    private Long createById;

}
