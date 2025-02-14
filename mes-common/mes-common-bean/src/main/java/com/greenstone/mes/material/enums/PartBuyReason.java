package com.greenstone.mes.material.enums;

import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2022/11/1 11:11
 */
@Getter
public enum PartBuyReason {
    NORMAL(1, "正常新增"),
    MISS_OF_DESIGNER(2, "设计失误"),
    REQUIREMENT_CHANGE(3, "需求变更"),
    LOSS_BY_WAREHOUSE(4, "仓库丢失"),
    LOSS_BY_ASSEMBLY(5, "装配丢失"),
    OTHER(6, "其他"),
    ;

    private final int code;

    private final String name;

    PartBuyReason(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public boolean needUpdateBom() {
        return this == NORMAL || this == MISS_OF_DESIGNER || this == REQUIREMENT_CHANGE;
    }

    public static PartBuyReason getByName(String name) {
        for (PartBuyReason value : PartBuyReason.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        throw new ServiceException(BizError.E25007, name);
    }

    public static PartBuyReason getByCode(Integer code) {
        for (PartBuyReason value : PartBuyReason.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new ServiceException(BizError.E25007, String.valueOf(code));
    }
}
