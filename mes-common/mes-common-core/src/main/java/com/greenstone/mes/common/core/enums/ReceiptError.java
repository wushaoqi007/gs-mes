package com.greenstone.mes.common.core.enums;

/**
 * 采购错误代码 10XXXX
 *
 */

public enum ReceiptError implements ServiceError {
    //  采购收货单单
    E100101(100101, "选择的收货单不存在"),
    E100102(100102, "不允许修改非草稿状态的单据"),
    E100103(100103, "只能够关闭完成状态的单据"),
    E100104(100104, "无法变更单据为草稿或提交以外的状态"),

    ;
    public final int code;

    public final String msg;

    ReceiptError(int code, String msg) {
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
