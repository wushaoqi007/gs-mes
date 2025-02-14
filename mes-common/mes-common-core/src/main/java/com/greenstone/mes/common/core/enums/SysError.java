package com.greenstone.mes.common.core.enums;

/**
 * 系统错误代码 1XXXX
 *
 * @author gu_renkai
 * @date 2022/11/8 9:41
 */

public enum SysError implements ServiceError {
    E10001(10001, "未知错误"),
    E10002(10002, "内部错误"),
    E10003(10003, "服务不存在"),
    E10004(10004, "服务调用失败"),
    E10005(10005, "无法执行的操作"),

    E11001(11001, "无法连接到外部服务"),
    E11002(11002, "外部服务调用失败"),

    E12001(12001, "不支持的审批数据类型"),
    E12002(12002, "不支持的审批状态"),

    // 用户
    E13001(13001, "选择的用户不存在"),

    E14001(14001, "请求频繁，限制访问"),


    ;
    private final int code;

    private final String msg;

    SysError(int code, String msg) {
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
