package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StockTimeoutSearchReq {

    private String partCode;

    @Pattern(regexp = "^[0-9]*$", message = "common.attribute.validation.duration")
    private String duration;

    private Long warehouseId;

    private Boolean containsChildren;

}
