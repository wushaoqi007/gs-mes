package com.greenstone.mes.material.application.dto.cmd;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;

import java.util.List;

@Data
public class WarehouseImportCmd {

    @Excel(name = "编号")
    private String code;

    @Excel(name = "类型")
    private String type;

    @Excel(name = "阶段")
    private String stage;

}
