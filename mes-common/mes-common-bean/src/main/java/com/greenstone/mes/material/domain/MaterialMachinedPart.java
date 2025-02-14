package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 机加工件对象 material_machined_parts
 *
 * @author gu_renkai
 * @date 2022-03-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("material_machined_part")
public class MaterialMachinedPart extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
     * 物料ID
     */
    @TableField
    private Long materialId;

    /**
     * 物料编码
     */
    @Excel(name = "物料编码")
    @TableField
    private String materialCode;

    /**
     * 物料名称
     */
    @Excel(name = "物料名称")
    @TableField
    private String materialName;

    /**
     * 物料版本
     */
    @Excel(name = "物料版本")
    @TableField
    private String materialVersion;

    /**
     * 采购数量
     */
    @Excel(name = "采购数量")
    @TableField
    private Long number;

    /**
     * 加工单位
     */
    @Excel(name = "加工单位")
    @TableField
    private String provider;

    /**
     * 设计者
     */
    @Excel(name = "设计者")
    @TableField
    private String designer;

    /**
     * 组件ID
     */
    @TableField
    private Long componentId;

    /**
     * 组件编码
     */
    @Excel(name = "组件编码")
    @TableField
    private String componentCode;

    /**
     * 组件名称
     */
    @Excel(name = "组件名称")
    @TableField
    private String componentName;

    /**
     * 组件名称
     */
    @Excel(name = "组件版本")
    @TableField
    private String componentVersion;

    /**
     * 采购时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采购时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField
    private Date purchaseTime;

    /**
     * 项目代码
     */
    @Excel(name = "项目代码")
    @TableField
    private String projectCode;

    /**
     * 原材料
     */
    @Excel(name = "原材料")
    @TableField
    private String rawMaterial;

    /**
     * 纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "纳期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField
    private Date deliveryTime;

}