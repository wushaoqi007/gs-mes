package com.greenstone.mes.product.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public enum ProductPlanStatus {
    NOT_STARTED("未开始"),
    IN_PROGRESS("进行中"),
    PAUSE("暂停"),
    FINISHED("已完成"),
    CANCELLED("已取消"),
    ;
    @EnumValue
    private final String status;


    ProductPlanStatus(String status) {
        this.status = status;
    }

    public static ProductPlanStatus getByStatus(String status) {
        Optional<ProductPlanStatus> find = Arrays.stream(ProductPlanStatus.values()).filter(s -> Objects.equals(s.getStatus(), status)).findFirst();
        if (find.isEmpty()) {
            throw new ServiceException("请输入正确的计划状态：未开始、进行中、暂停、已完成、已取消");
        }
        return find.get();
    }

    /**
     * 项目状态显示优先级：进行中 未开始 暂停 已完成 取消
     *
     * @return 计划状态优先级列表
     */
    public static List<ProductPlanStatus> priorityList() {
        return Arrays.asList(ProductPlanStatus.IN_PROGRESS, ProductPlanStatus.NOT_STARTED, ProductPlanStatus.PAUSE, ProductPlanStatus.FINISHED, ProductPlanStatus.CANCELLED);
    }

}
