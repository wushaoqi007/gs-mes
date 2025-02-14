package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.application.service.WxLoginService;
import com.greenstone.mes.oa.application.service.WxMsgSendService;
import com.greenstone.mes.oa.application.service.WxSyncService;
import com.greenstone.mes.oa.enums.WxMsgType;
import com.greenstone.mes.oa.infrastructure.enums.WxCp;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import com.greenstone.mes.oa.response.WxLoginQrCodeR;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.model.LoginUser;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.dto.result.UserAddResult;
import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpOAuth2Service;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-07-13-8:58
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WxLoginServiceImpl implements WxLoginService {

    private final WxConfig wxConfig;
    private final WxcpService externalWxService;
    private final RemoteUserService userService;
    private final WxSyncService wxSyncService;
    private final WxMsgSendService wxMsgSendService;

    @Override
    public WxLoginQrCodeR getLoginQrCode(String cpId) {
        if (StrUtil.isEmpty(cpId)) {
            cpId = WxCp.AUTOMATION.getCpId();
        }
        String appId = cpId;
        Integer agentId = wxConfig.getAgentId(cpId, WxConfig.SYSTEM);
        String state = appId + "-" + agentId + "-" + "web_login";
        String redirectUrl = URLEncodeUtil.encode(wxConfig.getQrLoginRedirectUri());

        return WxLoginQrCodeR.builder().appId(appId).agentId(agentId.toString()).state(state).redirectUrl(redirectUrl).build();
    }

    @Override
    public LoginUser qrCodeLoginCallback(WorkwxOauth2Cmd loginCallBackCmd) {
        LoginUser loginUser = new LoginUser();
        User sysUser = new User();
        String[] split = loginCallBackCmd.getState().split("-");
        if (split.length < 1) {
            log.info("qrCode login callback failed, can not find param: cpId.");
            throw new ServiceException("登录失败，请使用其他方式登录");
        }
        String cpId = split[0];
        WxCpService cpService = externalWxService.getWxCpService(cpId, wxConfig.getAgentId(cpId, WxConfig.SYSTEM));
        WxCpOAuth2Service oauth2Service = cpService.getOauth2Service();
        WxCpOauth2UserInfo userInfo;
        try {
            userInfo = oauth2Service.getUserInfo(loginCallBackCmd.getCode());
            log.info("企业微信二维码登录，code换取usrId：{}", userInfo);
            if (Objects.nonNull(userInfo) && userInfo.getUserId() != null) {
                sysUser = userService.getByWx(cpId, userInfo.getUserId());
                log.info("企业微信二维码登录，对应系统用户信息：{}", sysUser);
                if (Objects.isNull(sysUser)) {
                    // 新增用户（无手机号账户）并发送授权链接（获得手机号）
                    UserAddResult userAddResult = wxSyncService.addUserByWxUserId(cpId, userInfo.getUserId());
                    if (userAddResult.isSuccess()) {
                        sysUser = userService.getByWx(cpId, userInfo.getUserId());
                        log.info("企业微信二维码登录，新用户创建：{}", sysUser);
                        String state = cpId + wxConfig.getAgentId(cpId, WxConfig.SYSTEM) + "-text-update_user";
                        String url = oauth2Service.buildAuthorizationUrl(wxConfig.getOauth2RedirectUri(), state, WxConsts.OAuth2Scope.SNSAPI_PRIVATEINFO);
                        sendOauth2(cpId, userInfo.getUserId(), url);
                    }
                }
            }
        } catch (WxErrorException e) {
            log.info("获取用户扫码授权信息失败,cmd:{},error：{}", loginCallBackCmd, e.getMessage());
            throw new ServiceException("登录失败，请使用其他方式登录");
        }
        loginUser.setUser(sysUser);
        return loginUser;
    }

    public void sendOauth2(String cpId, String wxUserId, String url) {
        log.info("auth url:{}", url);
        // 发送链接
        WxMsgSendCmd msgSendCmd = WxMsgSendCmd.builder().agentId(wxConfig.getAgentId(cpId, WxConfig.SYSTEM))
                .cpId(cpId)
                .url(url)
                .msgType(WxMsgType.TEXT_CARD)
                .title("用户信息同步")
                .content("请点击卡片进行授权，同步完成后可使用手机号作为账号登录系统。")
                .toUser(Collections.singletonList(WxMsgSendCmd.WxMsgUser.builder().wxUserId(wxUserId).build())).build();
        wxMsgSendService.sendMsgToWx(msgSendCmd);
    }
}
