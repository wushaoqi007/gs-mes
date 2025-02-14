package com.greenstone.mes.external.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:26
 */
@Getter
public enum ProcessStatus {
    DRAFT(0, "草稿"),
    COMMITTED(1, "已提交"),
    APPROVING(2, "审批中"),
    APPROVED(3, "已审批"),
    REJECTED(4, "已驳回"),
    CLOSED(5, "已关闭"),
    ISSUED(6, "已发放"),
    REVOKED(7, "已撤销"),
    WAIT_APPROVE(8, "待审批"),
    FINISH(9, "已完成"),
    ;
    @EnumValue
    private final int status;

    private final String name;

    ProcessStatus(int status, String name) {
        this.status = status;
        this.name = name;
    }
}
