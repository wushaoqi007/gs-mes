package com.greenstone.mes.oa.infrastructure.enums;

import com.greenstone.mes.common.core.enums.SysError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Getter;
import me.chanjar.weixin.cp.bean.oa.WxCpSpStatus;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2022/11/17 13:31
 */
@Getter
public enum ApprovalStatus {
    AUDITING(1, "审批中", WxCpSpStatus.AUDITING),
    PASSED(2, "已通过", WxCpSpStatus.PASSED),
    REJECTED(3, "已驳回", WxCpSpStatus.REJECTED),
    UNDONE(4, "已撤销", WxCpSpStatus.UNDONE),
    PASS_UNDONE(6, "通过后撤销", WxCpSpStatus.PASS_UNDONE),
    DELETED(7, "已删除", WxCpSpStatus.DELETED),
    ALREADY_PAY(10, "已支付", WxCpSpStatus.ALREADY_PAY);

    private final int status;

    private final String name;

    private final WxCpSpStatus wxCpSpStatus;

    ApprovalStatus(int status, String name, WxCpSpStatus wxCpSpStatus) {
        this.status = status;
        this.name = name;
        this.wxCpSpStatus = wxCpSpStatus;
    }

    public boolean needUpdateAttendance() {
        return this == PASSED || this == PASS_UNDONE || this == DELETED;
    }

    public static ApprovalStatus from(WxCpSpStatus wxCpSpStatus) {
        for (ApprovalStatus value : ApprovalStatus.values()) {
            if (value.getWxCpSpStatus() == wxCpSpStatus) {
                return value;
            }
        }
        throw new ServiceException(SysError.E12002, wxCpSpStatus.toString());
    }

    public static ApprovalStatus from(Integer status) {
        for (ApprovalStatus value : ApprovalStatus.values()) {
            if (Objects.equals(value.getStatus(), status)) {
                return value;
            }
        }
        throw new ServiceException(SysError.E12002, String.valueOf(status));
    }

    public static ApprovalStatus getByName(String name) {
        return Arrays.stream(ApprovalStatus.values()).filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }
}
