package com.greenstone.mes.common.core.enums;

/**
 * 清理单错误代码 16XXXX
 *
 */

public enum CesClearError implements ServiceError {
    E160101(160101, "选择的归还单不存在"),
    E160102(160102, "不允许修改非草稿状态的单据"),
    E160103(160103, "只能够关闭完成状态的单据"),
    E160104(160104, "无法变更单据为草稿或提交以外的状态"),

    ;
    public final int code;

    public final String msg;

    CesClearError(int code, String msg) {
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
