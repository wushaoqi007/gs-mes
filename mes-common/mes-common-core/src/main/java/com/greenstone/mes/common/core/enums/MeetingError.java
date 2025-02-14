package com.greenstone.mes.common.core.enums;

/**
 * 会议室错误代码 13XXXX
 *
 */

public enum MeetingError implements ServiceError {
    E130101(130101, "会议室名称不能重复"),
    E130102(130102, "预约记录未找到"),
    E130103(130103, "会议已结束，不可修改"),
    E130104(130104, "该会议室已被预约，请选择其他会议室，或其他时间"),
    E130105(130105, "请选择正确的预约时间"),
    E130106(130106, "会议已开始，无法删除"),
    ;
    public final int code;

    public final String msg;

    MeetingError(int code, String msg) {
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
