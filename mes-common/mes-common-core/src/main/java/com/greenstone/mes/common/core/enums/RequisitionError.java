package com.greenstone.mes.common.core.enums;

/**
 * 领用错误代码 14XXXX
 *
 */

public enum RequisitionError implements ServiceError {
    E140101(140101, "选择的领用单不存在"),
    E140102(140102, "不允许修改非草稿状态的单据"),
    E140103(140103, "只能够关闭完成状态的单据"),
    E140104(140104, "无法变更单据为草稿或提交以外的状态"),

    ;
    public final int code;

    public final String msg;

    RequisitionError(int code, String msg) {
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
