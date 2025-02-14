package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineRecordFuzzyQuery {
    private String serialNo;
    private String projectCode;
    private String partCode;
    private String partName;
    private String key;
}
