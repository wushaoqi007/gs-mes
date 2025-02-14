package com.greenstone.mes.system.interfaces.rest.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2023-03-30-14:54
 */
@Data
public class WxOauth2SyncCmd {

    @NotEmpty(message = "code can not empty")
    private String code;
    /**
     * oauth2时本程序传给企业微信的参数，格式：cpId-changeType
     */
    @NotEmpty(message = "state can not empty")
    private String state;

}
