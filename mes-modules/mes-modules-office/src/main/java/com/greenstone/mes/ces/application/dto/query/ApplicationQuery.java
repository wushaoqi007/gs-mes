package com.greenstone.mes.ces.application.dto.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ApplicationQuery {

    @NotEmpty(message = "审批单号不能为空")
    private String serialNo;

}
