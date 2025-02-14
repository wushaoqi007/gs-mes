package com.greenstone.mes.wxcp.domain.helper.impl;

import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpServiceConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class WxcpServiceImpl implements WxcpService {

    private final WxConfig wxConfig;

    @Override
    public Map<String, WxCpService> getServiceMap(Integer agentId) {
        return WxCpServiceConfig.getCpServiceMap(agentId);
    }

    @Override
    public WxCpOaService getOaService(@NotNull(message = "CpId is necessary") CpId cpId) {
        WxCpService cpService = getWxCpService(cpId.id(), wxConfig.getAgentId(WxConfig.SYSTEM));
        return cpService.getOaService();
    }

    @Override
    public WxCpOaService getOaSpService(@NotNull(message = "CpId is necessary") CpId cpId) {
        WxCpService cpService = getWxCpService(cpId.id(), wxConfig.getAgentId(WxConfig.SYSTEM));
        return cpService.getOaService();
    }

    @Override
    public CustomOaServiceImpl getCustomOaService(@NotNull(message = "CpId is necessary") CpId cpId) {
        WxCpService cpService = getWxCpService(cpId.id(), wxConfig.getAgentId(WxConfig.SYSTEM));
        return new CustomOaServiceImpl(cpService);
    }

    /**
     * getDepartmentService的重载方法：寻找对应企业微信服务
     *
     * @param cpId
     */
    @Override
    public WxCpDepartmentService getDepartmentService(String cpId) {
        WxCpService cpService = getWxCpService(cpId, wxConfig.getAgentId(WxConfig.SYSTEM));
        return cpService.getDepartmentService();
    }

    /**
     * 企业微信规定不能直接使用通讯录获取员工信息，因所有人都需要进行打卡，考勤应用有所有员工信息的权限，所以这里使用考勤应用来获取
     *
     * @param cpId 企业id
     */
    @Override
    public WxCpUserService getUserService(String cpId) {
        WxCpService cpService = getWxCpService(cpId, wxConfig.getAgentId(WxConfig.SYSTEM));
        return cpService.getUserService();
    }

    @Override
    public WxCpMessageService getMsgService() {
        WxCpService cpService = getWxCpService(wxConfig.getAgentId(WxConfig.SYSTEM));
        return cpService.getMessageService();
    }

    @Override
    public WxCpMessageService getMsgService(CpId cpId) {
        WxCpService cpService = getWxCpService(cpId.id(), wxConfig.getAgentId(WxConfig.SYSTEM));
        return cpService.getMessageService();
    }

    @Override
    public WxCpMediaService getMediaService(String cpId) {
        WxCpService cpService = getWxCpService(cpId, wxConfig.getAgentId(WxConfig.APPLICATION));
        return cpService.getMediaService();
    }

    /**
     * 获取企业微信服务对象
     *
     * @param agentId 企业微信服务ID
     */
    @Override
    public WxCpService getWxCpService(Integer agentId) {
        return WxCpServiceConfig.getCpService(wxConfig.getDefaultCpId(), agentId);
    }

    @Override
    public WxCpService getWxCpService(String agentName) {
        Integer appId = wxConfig.getAgentId(agentName);
        return getWxCpService(appId);
    }

    /**
     * 获取企业微信服务对象
     *
     * @param cpId    企业微信企业ID
     * @param agentId 企业微信服务ID
     */
    @Override
    public WxCpService getWxCpService(@NotNull String cpId, @NotNull Integer agentId) {
        return WxCpServiceConfig.getCpService(cpId, agentId);
    }

    public WxCpTagService getWxTagService(CpId cpId) {
        WxCpService cpService = WxCpServiceConfig.getCpService(cpId.id(), wxConfig.getAgentId(cpId.id(), WxConfig.CONTACTS));
        return cpService.getTagService();
    }

    @Override
    public WxCpOAuth2Service getWxCpOAuth2Service(Integer agentId) {
        WxCpService cpService = WxCpServiceConfig.getCpService(wxConfig.getDefaultCpId(), agentId);
        return cpService.getOauth2Service();
    }

}
