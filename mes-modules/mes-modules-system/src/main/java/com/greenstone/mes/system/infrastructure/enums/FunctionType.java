package com.greenstone.mes.system.infrastructure.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FunctionType {
    TABLE("table", "表格"),
    PAGE("page", "页面"),
    ;
    @EnumValue
    private String type;

    private String name;

    public static String getNameByType(String type) {
        for (FunctionType functionType : values()) {
            if (functionType.getType().equals(type)) {
                return functionType.getName();
            }
        }
        throw new ServiceException(StrUtil.format("功能类型未找到:{}", type));
    }
}
