package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class WarehouseRemoveCmd {
    @NotEmpty(message = "请选择仓库")
    private String warehouseCode;
}
