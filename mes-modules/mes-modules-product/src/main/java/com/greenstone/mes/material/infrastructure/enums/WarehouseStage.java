package com.greenstone.mes.material.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * 零件阶段
 *
 * @author gu_renkai
 * @date 2022/12/14 9:44
 */
@Getter
public enum WarehouseStage {
    /**
     * 1 待收货
     */
    WAIT_RECEIVE(1, "待收货"),
    /**
     * 2 待质检
     */
    WAIT_CHECK(2, "待质检"),
    /**
     * 3 质检中
     */
    @Deprecated
    CHECKING(3, "质检中"),
    /**
     * 4 合格品
     */
    CHECKED_OK(4, "合格品"),
    /**
     * 5 待表处
     */
    WAIT_TREAT_SURFACE(5, "待表处"),
    /**
     * 6 待返工
     */
    @Deprecated
    WAIT_REWORKED(6, "待返工"),
    /**
     * 7 表处中
     */
    TREATING(7, "表处中"),
    /**
     * 8 返工中
     */
    REWORKING(8, "返工中"),
    /**
     * 9 良品
     */
    GOOD(9, "良品"),
    ;
    @EnumValue
    private final int id;

    private final String name;

    WarehouseStage(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static WarehouseStage getById(int id) {
        return Arrays.stream(WarehouseStage.values()).filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    /**
     * 砧板阶段
     */
    public boolean isBoardStage() {
        return this == WAIT_CHECK || this == WAIT_TREAT_SURFACE || this == WAIT_REWORKED || this == GOOD;
    }

    public static WarehouseStage getByName(String name) {
        for (WarehouseStage type : WarehouseStage.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new RuntimeException("错误的阶段名称【" + name + "】");
    }

}
