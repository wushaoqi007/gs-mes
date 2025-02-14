package com.greenstone.mes.material.request;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.material.domain.BaseMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialInfo {

    private Long materialId;

    private String materialCode;

    private String materialVersion;

    private BaseMaterial material;

    @NotNull(message = "material.stock.lack.material.number")
    @Min(value = 1, message = "common.attribute.validation.amount.mix")
    @Max(value = 999999, message = "common.attribute.validation.amount.max")
    private Long number;

    private Long numberAfterOperation;

    private String orderCode;

    private String componentCode;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
