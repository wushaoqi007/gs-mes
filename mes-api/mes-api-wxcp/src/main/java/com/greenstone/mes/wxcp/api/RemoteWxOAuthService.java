package com.greenstone.mes.wxcp.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import com.greenstone.mes.wxcp.resp.WxOauth2Resp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@FeignClient(contextId = "remoteWxOauthService", value = ServiceNameConstants.WXCP_SERVICE)
public interface RemoteWxOAuthService {

    @PostMapping("/wxcp/oauth2")
    WxOauth2Resp getUserByOauth2(@RequestBody WorkwxOauth2Cmd oauth2Cmd);

}
