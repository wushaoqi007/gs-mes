package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PurchaseOrderDetailReq {

    @NotNull(message = "采购单ID不为空")
    private Long id;

    private String status;

}
