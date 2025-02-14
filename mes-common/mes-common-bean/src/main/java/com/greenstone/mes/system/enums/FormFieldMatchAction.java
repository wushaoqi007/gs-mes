package com.greenstone.mes.system.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author gu_renkai
 * @date 2023/2/24 14:23
 */
@Getter
public enum FormFieldMatchAction {
    EQ(0, "=", "等于"),
    NEQ(1, "!=", "不等于"),
    GT(2, ">", "大于"),
    GTE(3, ">=", "大于等于"),
    LT(4, "<", "小于"),
    LTE(5, "<=", "小于等于"),
    CONTAIN(6, "⊇", "包含"),
    BELONG(7, "⊆", "属于"),
    ;
    @JSONField
    @EnumValue
    private final int id;

    private final String symbol;

    private final String name;

    FormFieldMatchAction(int id, String symbol, String name) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
    }

    public void validFieldType(FormDataType formDataType) {
        if (!Arrays.asList(formDataType.getActions()).contains(this)) {
            throw new ServiceException(formDataType.getName() + " 类型数据不支持 " + this.getName() + " 操作");
        }
    }

    public static FormFieldMatchAction fromId(int id) {
        for (FormFieldMatchAction value : FormFieldMatchAction.values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        throw new ServiceException("请选择正确的匹配条件");
    }
}
