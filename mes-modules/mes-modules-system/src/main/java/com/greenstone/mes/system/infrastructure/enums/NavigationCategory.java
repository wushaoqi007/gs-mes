package com.greenstone.mes.system.infrastructure.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.enums.MaterialPurchaseReasonCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NavigationCategory {
    MODULE("module", "模块"),
    CATEGORY("category", "分类"),
    GROUP("group", "分组"),
    NAVIGATION("navigation", "导航"),
    ;
    @EnumValue
    private String type;

    private String name;

    public static String getNameByType(String type) {
        for (NavigationCategory category : values()) {
            if (category.getType().equals(type)) {
                return category.getName();
            }
        }
        throw new ServiceException(StrUtil.format("导航分类未找到:{}", type));
    }
}
