package com.greenstone.mes.oa.infrastructure.enums;

import com.greenstone.mes.wxcp.domain.types.CpId;

/**
 * @author gu_renkai
 * @date 2022/8/17 8:22
 */

public enum WxCp {
    AUTOMATION("wx1dee7aa3b2526c66", "格林司通自动化"),
    ;
    private final String cpId;

    private final String cpName;

    WxCp(String cpId, String cpName) {
        this.cpId = cpId;
        this.cpName = cpName;
    }

    public String getCpId() {
        return cpId;
    }

    public String getCpName() {
        return cpName;
    }

    public boolean itsMe(CpId cpId) {
        return this.cpId.equals(cpId.id());
    }
}
