package com.greenstone.mes.system.application.dto.cmd;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;

@Data
public class PermImportReq {

    @Excel(name = "模块名称")
    private String moduleName;

    @Excel(name = "模块标识")
    private String moduleCode;

    @Excel(name = "功能名称")
    private String functionName;

    @Excel(name = "功能标识")
    private String functionCode;

    @Excel(name = "权限名称")
    private String permName;

    @Excel(name = "权限标识")
    private String permCode;
}
