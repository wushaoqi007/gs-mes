package com.greenstone.mes.material.enums;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;

/**
 * 零件进度类型
 */
public enum PartProgressType {

    DSJ(1, "待收件"),
    DZJ(2, "待质检"),
    ZJZ(3, "质检中"),
    HGP(4, "合格品"),
    DBC(5, "待表处"),
    DFG(6, "待返工"),
    BCZ(7, "表处中"),
    FGZ(8, "返工中"),
    YWC(9, "已完成"),
    YLY(10, "已领用");

    public static PartProgressType getNameByType(int type) {
        for (PartProgressType value : PartProgressType.values()) {
            if (value.type == type) {
                return value;
            }
        }
        throw new ServiceException(StrUtil.format("未找到对应零件进度类型：{}", type));
    }

    private final int type;

    private final String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    PartProgressType(int type, String name) {
        this.type = type;
        this.name = name;
    }

}
