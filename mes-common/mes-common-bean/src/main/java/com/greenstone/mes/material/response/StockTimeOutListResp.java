package com.greenstone.mes.material.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StockTimeOutListResp {

    private String materialId;

    private String partCode;

    private String partName;

    private String partVersion;

    private String duration;

    private String warehouseId;

    private String warehouseName;

    private String parentId;

    private Long number;

}
