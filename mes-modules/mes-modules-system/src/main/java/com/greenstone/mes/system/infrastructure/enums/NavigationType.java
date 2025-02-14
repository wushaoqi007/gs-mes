package com.greenstone.mes.system.infrastructure.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NavigationType {
    FUNCTION("function", "功能"),
    EXTERNAL("external", "外链"),
    ;
    @EnumValue
    private String type;

    private String name;

    public static String getNameByType(String type) {
        for (NavigationType navigationType : values()) {
            if (navigationType.getType().equals(type)) {
                return navigationType.getName();
            }
        }
        throw new ServiceException(StrUtil.format("导航类型未找到:{}", type));
    }

}
