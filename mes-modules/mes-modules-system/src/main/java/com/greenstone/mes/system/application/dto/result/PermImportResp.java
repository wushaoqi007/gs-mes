package com.greenstone.mes.system.application.dto.result;

import lombok.Data;

@Data
public class PermImportResp {

    private String permCode;

    private String permName;

    private String errorMsg;
}
