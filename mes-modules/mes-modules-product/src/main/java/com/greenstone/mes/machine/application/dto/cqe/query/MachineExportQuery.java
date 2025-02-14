package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MachineExportQuery {

    @NotEmpty(message = "单号不为空")
    private String serialNo;

}
