package com.greenstone.mes.wxcp.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class WorkwxOauth2Cmd {

    @NotEmpty(message = "code can not empty")
    private String code;

    /**
     * 企业id-企业应用id-业务描述
     */
    @NotEmpty(message = "state can not empty")
    private String state;


    public String getCpId() {
        String[] split = state.split("-");
        if (split.length > 0) {
            return split[0];
        } else {
            throw new RuntimeException("企业微信登录缺少企业ID信息");
        }
    }

    public Integer getAgentId() {
        String[] split = state.split("-");
        if (split.length > 1) {
            return Integer.valueOf(split[1]);
        } else {
            throw new RuntimeException("企业微信登录缺少应用ID信息");
        }
    }


}
