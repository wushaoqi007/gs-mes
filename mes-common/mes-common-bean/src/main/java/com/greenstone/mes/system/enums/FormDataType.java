package com.greenstone.mes.system.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Getter;

/**
 * 模型属性类型
 *
 * @author gu_renkai
 * @date 2023/2/24 14:14
 */
@Getter
public enum FormDataType {
    STRING(0, "字符串", new FormFieldMatchAction[]{FormFieldMatchAction.EQ, FormFieldMatchAction.NEQ, FormFieldMatchAction.CONTAIN}),
    NUMBER(1, "整数", new FormFieldMatchAction[]{FormFieldMatchAction.EQ, FormFieldMatchAction.NEQ, FormFieldMatchAction.GT, FormFieldMatchAction.GTE
            , FormFieldMatchAction.LT, FormFieldMatchAction.LTE}),
    FLOAT(2, "浮点数", new FormFieldMatchAction[]{FormFieldMatchAction.EQ, FormFieldMatchAction.NEQ, FormFieldMatchAction.GT, FormFieldMatchAction.GTE
            , FormFieldMatchAction.LT, FormFieldMatchAction.LTE}),
    DATE(3, "日期", new FormFieldMatchAction[]{FormFieldMatchAction.EQ, FormFieldMatchAction.NEQ, FormFieldMatchAction.GT, FormFieldMatchAction.GTE,
            FormFieldMatchAction.LT, FormFieldMatchAction.LTE}),
    DATETIME(4, "时间", new FormFieldMatchAction[]{FormFieldMatchAction.EQ, FormFieldMatchAction.NEQ, FormFieldMatchAction.GT,
            FormFieldMatchAction.GTE, FormFieldMatchAction.LT, FormFieldMatchAction.LTE}),
    SYS_OBJECT(5, "系统对象", new FormFieldMatchAction[]{FormFieldMatchAction.EQ, FormFieldMatchAction.NEQ, FormFieldMatchAction.BELONG}),
    ;
    @JSONField
    @EnumValue
    private final int id;

    private final String name;

    private final FormFieldMatchAction[] actions;

    FormDataType(int id, String name, FormFieldMatchAction[] actions) {
        this.id = id;
        this.name = name;
        this.actions = actions;
    }

    public static FormDataType fromId(int id) {
        for (FormDataType value : FormDataType.values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        throw new ServiceException("请选择正确的字段类型");
    }
}
