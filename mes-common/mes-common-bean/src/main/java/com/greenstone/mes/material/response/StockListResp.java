package com.greenstone.mes.material.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StockListResp {

    private Long materialId;

    private Long warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private String number;

    private String unit;

    private String materialCode;

    private String materialName;

    private String materialVersion;

    private String worksheetCode;

    private String componentCode;

    private Long receivingDetailId;

}
