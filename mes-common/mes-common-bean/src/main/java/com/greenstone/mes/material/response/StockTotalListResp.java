package com.greenstone.mes.material.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StockTotalListResp {

    private String materialId;

    private String materialCode;

    private String materialName;

    private String materialVersion;

    private String materialType;

    private String projectCode;

    private Long totalNumber;

    private String unit;

}
