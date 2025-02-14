package com.greenstone.mes.oa.enums;

/**
 * 企业微信消息类型
 */
public enum WxMsgType {

    TEXT("text", "文本消息"),
    TEXT_CARD("textcard", "文本卡片消息");


    private final String type;
    private final String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    WxMsgType(String type, String name) {
        this.type = type;
        this.name = name;
    }

}
