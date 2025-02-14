package com.greenstone.mes.material.enums;

/**
 * @author wushaoqi
 * @date 2022-11-22-10:01
 */
public enum WarehouseType {
    WAREHOUSE(0, "货架"),
    BOARD(1, "砧板");

    private final int type;

    private final String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean match(int type) {
        return type == this.type;
    }

    WarehouseType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static WarehouseType getByName(String name) {
        for (WarehouseType type : WarehouseType.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new RuntimeException("错误的仓库类型【" + name + "】");
    }
}
