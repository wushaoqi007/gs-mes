package com.greenstone.mes.common.core.enums;

/**
 * 归还单错误代码 15XXXX
 *
 */

public enum CesReturnError implements ServiceError {
    E150101(150101, "选择的归还单不存在"),
    E150102(150102, "不允许修改非草稿状态的单据"),
    E150103(150103, "只能够关闭完成状态的单据"),
    E150104(150104, "无法变更单据为草稿或提交以外的状态"),

    ;
    public final int code;

    public final String msg;

    CesReturnError(int code, String msg) {
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
