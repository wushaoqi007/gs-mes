package com.greenstone.mes.product.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ProductPlanType {
    ASSEMBLY(1, "装配"),
    WIRING(2, "接线"),
    PROGRAM(3, "程序"),
    DEBUGGING(4, "调试"),
    ;
    @EnumValue
    private final int type;

    private final String name;

    ProductPlanType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static ProductPlanType getByType(String typeString) {
        int type;
        try {
            type = Integer.parseInt(typeString);
        } catch (NumberFormatException e) {
            throw new ServiceException("请选择正确的计划类型：1（装配）、2（接线）、3（程序）、4（调试）");
        }
        Optional<ProductPlanType> find = Arrays.stream(ProductPlanType.values()).filter(s -> s.getType() == type).findFirst();
        if (find.isEmpty()) {
            throw new ServiceException("请选择正确的计划类型：1（装配）、2（接线）、3（程序）、4（调试）");
        }
        return find.get();
    }
}
