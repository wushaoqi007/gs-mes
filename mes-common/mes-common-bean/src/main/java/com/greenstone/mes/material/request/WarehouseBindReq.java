package com.greenstone.mes.material.request;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 存放点绑定
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WarehouseBindReq {

    @NotEmpty(message = "common.attribute.validation.code")
    @Length(min = 1, max = 30, message = "common.attribute.validation.code")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "common.attribute.validation.code")
    private String code;

    @NotNull(message = "common.attribute.validation.warehouseId")
    private Long warehouseId;

    private Integer type;
}