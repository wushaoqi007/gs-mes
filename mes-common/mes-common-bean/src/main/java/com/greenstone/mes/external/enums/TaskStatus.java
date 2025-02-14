package com.greenstone.mes.external.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2023/3/1 16:46
 */
@Getter
public enum TaskStatus {
    COMMIT(0, "发起审批"),
    PENDING(1, "审批中"),
    APPROVED(2, "同意"),
    REJECTED(3, "驳回"),
    REVOKED(4, "撤回"),
    YZS(5, "已转审"),
    ;
    @EnumValue
    private final int status;

    private final String name;

    TaskStatus(int status, String name) {
        this.status = status;
        this.name = name;
    }
}
