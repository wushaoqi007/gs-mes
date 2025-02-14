package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MachineOrderProgressQuery {

    private String provider;

    private String projectCode;

    private Date orderStartTime;
    private Date orderEndTime;

    private Date processDeadlineStartTime;
    private Date processDeadlineEndTime;

    private String partCode;

    private String partName;

}
