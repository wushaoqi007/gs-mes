package com.greenstone.mes.material.event.data;

import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockUpdateEventData {

    private String worksheetCode;
    private String projectCode;
    private String componentCode;
    private String partName;
    private String partCode;
    private String partVersion;
    private Long number;
    private BaseMaterial material;
    private BaseWarehouse warehouse;
    private Long stockOffset;
    private String sponsor;
    private String remark;
}
