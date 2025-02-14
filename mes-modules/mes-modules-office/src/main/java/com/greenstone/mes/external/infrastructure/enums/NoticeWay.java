package com.greenstone.mes.external.infrastructure.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoticeWay {

    EMAIL(1, "邮件"),
    SYS_MESSAGE(2, "系统消息"),
    WX_WORK_MSG(3, "企业微信消息"),
    ;
    @EnumValue
    @JSONField
    private final int code;

    private final String name;
}
