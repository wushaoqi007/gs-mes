package com.greenstone.mes.oa.infrastructure.enums;

import lombok.Getter;

@Getter
public enum WxUserSyncType {
    insert(0, "create_user"),
    update(1, "update_user"),
    delete(2, "delete_user"),
    ;

    private final int type;

    private final String name;

    WxUserSyncType(int type, String name) {
        this.type = type;
        this.name = name;
    }

}
