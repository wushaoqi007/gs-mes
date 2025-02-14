package com.greenstone.mes.material.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 加工商不良类型统计表
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("material_stat_result_rework")
public class StatResultReworkDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -7141180295385820023L;

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField
    private String statisticDate;
    @TableField
    private String provider;
    @TableField
    private String projectCode;
    @TableField
    private String ngType;
    @TableField
    private String subNgType;
    @TableField
    private Integer total;
}
