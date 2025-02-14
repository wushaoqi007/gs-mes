package com.greenstone.mes.common.core.enums;

/**
 * 仓库错误代码 11XXXX
 */

public enum WarehouseError implements ServiceError {
    //  仓库
    E110101(110101, "选择的仓库不存在"),

    E110104(110104, "所属仓库不存在"),
    E110105(110105, "仓库编码已被使用"),
    E110106(110106, "不能将仓库移动到末级仓库下"),

    E110109(110109, "删除的仓库不存在"),
    E110110(110110, "该仓库下包含子仓库，不允许删除"),
    E110111(110111, "父仓库不能是自身"),

    ;
    public final int code;

    public final String msg;

    WarehouseError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
