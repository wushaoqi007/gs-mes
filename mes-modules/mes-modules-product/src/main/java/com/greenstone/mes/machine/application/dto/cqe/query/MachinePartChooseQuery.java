package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachinePartChooseQuery {
    private String part;

    private String partVersion;

    private String requirementSerialNo;

    private String projectCode;

}
