package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WarehouseBindProjectCmd {

    @NotNull(message = "请选择仓库")
    private Long id;

    private String projectCode;

}