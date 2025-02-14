package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineRealStockQuery {
    private String projectCode;
    private String partCode;
    private String partName;
    private String warehouseCode;
    private Integer stage;
    private Date startDate;
    private Date endDate;
    private Long stayDays;
}
