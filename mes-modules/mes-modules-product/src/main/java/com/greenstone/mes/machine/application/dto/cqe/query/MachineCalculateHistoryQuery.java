package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MachineCalculateHistoryQuery {

    @NotEmpty(message = "零件编码不为空")
    private String partCode;
    @NotEmpty(message = "零件版本不为空")
    private String partVersion;

}
