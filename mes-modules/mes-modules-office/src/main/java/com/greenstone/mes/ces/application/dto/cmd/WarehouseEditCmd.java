package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class WarehouseEditCmd {
    @NotNull(message = "请选择仓库")
    private Long id;

    private String parentWarehouseCode;

    @NotEmpty(message = "请填写仓库编码")
    private String warehouseCode;

    @NotEmpty(message = "请填写仓库名称")
    private String warehouseName;

}
