package com.greenstone.mes.asset.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/3 9:14
 */

public enum AssetState {

    IN_USE(1, "在用"),
    IDLE(2, "闲置"),
    SCRAP(3, "报废"),
    REPAIRING(4, "维修中"),
    ;
    
    @EnumValue
    @Getter
    private final int state;

    @Getter
    private final String name;

    AssetState(int state, String name) {
        this.state = state;
        this.name = name;
    }

    public static List<AssetState> statesByName(String nameLike){
        List<AssetState> stateList = new ArrayList<>();
        for (AssetState value : AssetState.values()) {
            if (value.getName().contains(nameLike)){
                stateList.add(value);
            }
        }
        return stateList;
    }
}
