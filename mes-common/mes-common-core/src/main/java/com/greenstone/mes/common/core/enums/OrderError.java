package com.greenstone.mes.common.core.enums;

/**
 * 采购错误代码 9XXXX
 *
 */

public enum OrderError implements ServiceError {
    //  采购订单单
    E90101(90101, "选择的订单不存在"),
    E90102(90102, "不允许修改非草稿状态的单据"),
    E90103(90103, "只能够关闭完成状态的单据"),
    E90104(90104, "无法变更单据为草稿或提交以外的状态"),

    ;
    public final int code;

    public final String msg;

    OrderError(int code, String msg) {
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
