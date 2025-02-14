package com.greenstone.mes.wxcp.resp;

import lombok.Data;

@Data
public class WxOauth2Resp {

    private String cpId;
    private String openId;
    private String deviceId;
    private String userId;
    private String userTicket;
    private String expiresIn;
    private String externalUserId;
    private String parentUserId;
    private String studentUserId;

}
