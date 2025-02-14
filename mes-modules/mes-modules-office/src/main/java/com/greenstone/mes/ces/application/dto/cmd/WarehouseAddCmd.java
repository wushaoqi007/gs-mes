package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class WarehouseAddCmd {
    

    private String parentWarehouseCode;

    @NotEmpty(message = "请填写仓库编码")
    private String warehouseCode;

    @NotEmpty(message = "请填写仓库名称")
    private String warehouseName;


}
