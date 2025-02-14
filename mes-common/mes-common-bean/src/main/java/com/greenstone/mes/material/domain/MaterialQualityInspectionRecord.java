package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 质检记录表
 *
 * @author wushaoqi
 * @date 2022-09-14-14:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_quality_inspection_record")
public class MaterialQualityInspectionRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;

    /**
     * 机加工单编号
     */
    @TableField
    private String partOrderCode;

    /**
     * 组件的编码
     */
    @TableField
    private String componentCode;

    /**
     * 零件号
     */
    @TableField
    private String code;

    /**
     * 版本
     */
    @TableField
    private String version;

    /**
     * 零件名称
     */
    @TableField
    private String name;

    /**
     * 质检结果
     */
    @TableField
    private String result;

    /**
     * 数量
     */
    @TableField
    private Long number;

    /**
     * NG数量
     */
    @TableField
    private Long ngNumber;

    /**
     * NG大类
     */
    @TableField
    private String ngType;

    /**
     * NG小类
     */
    @TableField
    private String ngSubclass;

    /**
     * NG说明备注
     */
    @TableField
    private String ngExplain;

    /**
     * 加工商
     */
    @TableField
    private String provider;
}
