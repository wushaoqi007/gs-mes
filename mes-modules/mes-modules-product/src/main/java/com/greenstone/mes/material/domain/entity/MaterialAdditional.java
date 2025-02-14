package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialAdditional {

    /**
     * 物料编码
     */
    @NotEmpty(message = "零件编号不能为空")
    private String code;

    /**
     * 物料名称
     */
    private String name;

    /**
     * 物料版本
     */
    @NotEmpty(message = "零件版本不能为空")
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

}
