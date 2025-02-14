package com.greenstone.mes.material.response;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class MaterialInfoResp {

    private Long id;

    private String code;

    private String name;

    private String version;

    private Integer type;

    private String unit;

    private Long number;

}
