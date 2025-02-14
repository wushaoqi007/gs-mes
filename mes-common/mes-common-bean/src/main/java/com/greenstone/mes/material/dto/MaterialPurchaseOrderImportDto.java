package com.greenstone.mes.material.dto;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/8/9 10:13
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialPurchaseOrderImportDto {

    private String partOrderCode;

    private String projectCode;

    private String partCode;

    private String partName;

    private String partVersion;

    private String rawMaterial;

    private Long partNumber;

    private String remark;

    private String weight;

    private String componentCode;

    private String componentName;

    private String operation;

    private Date printData;

    private String designer;

    private String surfaceTreatment;

    private Integer paperNumber;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
