package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MachineCheckPartListQuery {

    private String projectCode;

    private String checkSerialNo;
    private String orderSerialNo;

    private String designer;

    private String part;

    private String checked;

    private String checkBy;

    private LocalDate checkDateStart;

    private LocalDate checkDateEnd;

    private Integer checkResult;
    private Integer checkResultType;

    private String ngType;

    private String subNgType;

}
