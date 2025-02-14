package com.greenstone.mes.external.application.dto.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ProcessDetailQuery {

    @NotEmpty
    private String serialNo;

}
