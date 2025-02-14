package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import com.greenstone.mes.oa.response.WxLoginQrCodeR;
import com.greenstone.mes.system.api.model.LoginUser;

/**
 * @author wushaoqi
 * @date 2023-07-13-8:58
 */
public interface WxLoginService {
    WxLoginQrCodeR getLoginQrCode(String cpId);

    LoginUser qrCodeLoginCallback(WorkwxOauth2Cmd loginCallBackCmd);
}
