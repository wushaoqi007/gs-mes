package com.greenstone.mes.bom.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BomDetailEditReq {

    @NotNull(message = "common.attribute.validation.id")
    private Long id;

    private Long materialNumber;

}
