package com.greenstone.mes.wxcp.domain.service.impl;

import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.service.WxAuthService;
import com.greenstone.mes.wxcp.resp.WxOauth2Resp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpOAuth2Service;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class WxAuthServiceImpl implements WxAuthService {

    private final WxcpService wxcpService;

    @Override
    public WxOauth2Resp getOauth2User(WorkwxOauth2Cmd oauth2Cmd) {
        String cpId = oauth2Cmd.getCpId();
        Integer agentId = oauth2Cmd.getAgentId();
        WxCpOAuth2Service wxOAuth2Service = wxcpService.getWxCpOAuth2Service(agentId);
        try {
            WxCpOauth2UserInfo userInfo = wxOAuth2Service.getUserInfo(oauth2Cmd.getCode());
            log.info("WxCpOauth2UserInfo {}", userInfo);
            WxOauth2Resp wxOauth2Resp = toOauthResp(userInfo, oauth2Cmd.getCpId());
            wxOauth2Resp.setCpId(cpId);
            return wxOauth2Resp;
        } catch (WxErrorException e) {
            throw new RuntimeException("企业微信登录失败，无法获取企业微信用户。", e);
        }
    }

    private WxOauth2Resp toOauthResp(WxCpOauth2UserInfo userInfo, String cpId) {
        WxOauth2Resp wxOauth2Resp = new WxOauth2Resp();
        wxOauth2Resp.setCpId(cpId);
        wxOauth2Resp.setOpenId(userInfo.getOpenId());
        wxOauth2Resp.setUserId(userInfo.getUserId());
        wxOauth2Resp.setUserTicket(userInfo.getUserTicket());
        wxOauth2Resp.setExpiresIn(userInfo.getExpiresIn());
        wxOauth2Resp.setParentUserId(userInfo.getParentUserId());
        wxOauth2Resp.setExternalUserId(userInfo.getExternalUserId());
        wxOauth2Resp.setStudentUserId(userInfo.getStudentUserId());
        wxOauth2Resp.setDeviceId(userInfo.getDeviceId());
        return wxOauth2Resp;
    }

}
