package com.greenstone.mes.material.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdateRecordListQuery {

    private String sponsor;

    private Long warehouseId;

    private Date operationTimeFrom;

    private Date operationTimeTo;

}
