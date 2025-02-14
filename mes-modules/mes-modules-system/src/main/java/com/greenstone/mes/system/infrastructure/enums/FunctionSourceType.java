package com.greenstone.mes.system.infrastructure.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FunctionSourceType {
    PREDEFINE("predefine", "预定义"),
    USERDEFINE("userdefine", "自定义"),
    ;
    @EnumValue
    private String type;

    private String name;

    public static String getNameByType(String type) {
        for (FunctionSourceType sourceType : values()) {
            if (sourceType.getType().equals(type)) {
                return sourceType.getName();
            }
        }
        throw new ServiceException(StrUtil.format("功能来源类型未找到:{}", type));
    }
}
