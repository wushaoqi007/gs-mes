package com.greenstone.mes.ces.application.dto.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CesClearQuery {

    @NotEmpty(message = "归还单号不能为空")
    private String serialNo;

}
