package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WarehouseUnbindReq {


    @NotNull(message = "common.attribute.validation.name.id")
    private Long id;

}