package com.greenstone.mes.wxcp.domain.service;

import com.greenstone.mes.wxcp.resp.WxOauth2Resp;
import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;

public interface WxAuthService {

    WxOauth2Resp getOauth2User(WorkwxOauth2Cmd oauth2Cmd);

}
