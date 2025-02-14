package com.greenstone.mes.common.core.enums;

/**
 * 微信错误代码 7XXXX
 *
 */

public enum WxError implements ServiceError {
    // 消息发送
    E70101(70101, "接收通知的人员未找到"),
    E70102(70102, "接收通知的人员不为空"),
    E70103(70103, "接收通知的人员企业微信为空"),
    ;
    public final int code;

    public final String msg;

    WxError(int code, String msg) {
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
