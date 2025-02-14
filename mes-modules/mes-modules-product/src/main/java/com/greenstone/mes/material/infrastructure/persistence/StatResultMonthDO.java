package com.greenstone.mes.material.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 加工商月统计表
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("material_stat_result_month")
public class StatResultMonthDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 7682983929011815338L;

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField
    private String statisticDate;
    @TableField
    private String statisticMonth;
    @TableField
    private String provider;
    @TableField
    private String projectCode;
    @TableField
    private Integer partOweNum;
    @TableField
    private Integer paperOweNum;
    @TableField
    private Integer partDeliveryNum;
    @TableField
    private Integer paperDeliveryNum;
    @TableField
    private Integer partPlanNum;
    @TableField
    private Integer paperPlanNum;
    @TableField
    private Integer partOverdueNum;
    @TableField
    private Integer paperOverdueNum;
    @TableField
    private Integer partOverdueThreeDaysNum;
    @TableField
    private Integer paperOverdueThreeDaysNum;
    @TableField
    private Integer partReworkNum;
    @TableField
    private Integer paperReworkNum;
    @TableField
    private Integer partCheckNum;
    @TableField
    private Integer paperCheckNum;
}
