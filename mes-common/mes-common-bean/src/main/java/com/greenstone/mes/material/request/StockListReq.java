package com.greenstone.mes.material.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StockListReq {

    private Long materialId;

    private String warehouseCode;

    /**
     * 仓库类型
     */
    private Integer type;

}
