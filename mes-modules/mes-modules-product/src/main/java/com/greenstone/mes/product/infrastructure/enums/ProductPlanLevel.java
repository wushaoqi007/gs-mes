package com.greenstone.mes.product.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
public enum ProductPlanLevel {
    TOP(0, "默认"),
    PROJECT(1, "项目"),
    EQUIPMENT(2, "设备"),
    COMPONENT_LEVEL_ONE(3, "一级组件"),
    COMPONENT_LEVEL_TWO(4, "二级组件"),
    COMPONENT_LEVEL_THREE(5, "三级组件"),
    ;
    @EnumValue
    private final Integer level;
    private final String name;


    ProductPlanLevel(Integer level, String name) {
        this.level = level;
        this.name = name;
    }

    public static ProductPlanLevel getByName(String name) {
        Optional<ProductPlanLevel> find = Arrays.stream(ProductPlanLevel.values()).filter(s -> Objects.equals(s.getName(), name)).findFirst();
        if (find.isEmpty()) {
            throw new ServiceException("请输入正确的类型：项目、设备、一级组件、二级组件、三级组件");
        }
        return find.get();
    }

    public static ProductPlanLevel getByLevel(Integer level) {
        Optional<ProductPlanLevel> find = Arrays.stream(ProductPlanLevel.values()).filter(s -> Objects.equals(s.getLevel(), level)).findFirst();
        if (find.isEmpty()) {
            throw new ServiceException("请选择正确的类型：1:项目、2:设备、3:一级组件、4:二级组件、5:三级组件");
        }
        return find.get();
    }

}
