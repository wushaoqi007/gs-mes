package com.greenstone.mes.ces.application.dto.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RequisitionQuery {

    @NotEmpty(message = "领用单号不能为空")
    private String serialNo;

}
