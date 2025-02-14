package com.greenstone.mes.bom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialImportDto {

    private String code;

    private String name;

    private String version;

    /**
     * remark
     */
    private String source;

    /**
     * 未使用
     */
    private String type;

    private Long number;

    private String unit;

    private String rawMaterial;

    private String surfaceTreatment;

    private Integer paperNumber;

    private String weight;

    private String componentName;

    private String buyLimit;

    private String printTime;

    private String designer;

    private String index;

}
