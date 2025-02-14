package com.greenstone.mes.ces.application.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseUpdateE {

    private String fromWarehouseCode;

    private String toWarehouseCode;

}
