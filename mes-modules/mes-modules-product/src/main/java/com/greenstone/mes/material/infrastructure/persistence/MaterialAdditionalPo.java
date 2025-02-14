package com.greenstone.mes.material.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.form.infrastructure.persistence.FormBaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 机加工物料补录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("machine_material_additional")
public class MaterialAdditionalPo extends FormBaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 物料编码
     */
    private String code;

    /**
     * 物料名称
     */
    private String name;

    /**
     * 物料版本
     */
    private String version;

    /**
     * 表面处理
     */
    private String surfaceTreatment;

    /**
     * 原材料
     */
    private String rawMaterial;

    /**
     * 设计人
     */
    private String designer;

    /**
     * 物料单位
     */
    private String unit;

    /**
     * 重量g
     */
    private String weight;

    /**
     * 物料类型 (1:原料 2:半成品 3:成品)
     */
    @TableField
    private Integer type = 1;

}