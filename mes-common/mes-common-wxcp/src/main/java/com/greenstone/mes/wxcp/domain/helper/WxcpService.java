package com.greenstone.mes.wxcp.domain.helper;

import com.greenstone.mes.wxcp.domain.types.CpId;
import me.chanjar.weixin.cp.api.*;

import java.util.Map;

public interface WxcpService {

    Map<String, WxCpService> getServiceMap(Integer agentId);

    WxCpOaService getOaService(CpId cpId);

    WxCpOaService getOaSpService(CpId cpId);

    CustomOaService getCustomOaService(CpId cpId);

    WxCpDepartmentService getDepartmentService(String cpId);

    WxCpUserService getUserService(String cpId);

    WxCpMessageService getMsgService();

    WxCpMessageService getMsgService(CpId cpId);

    WxCpMediaService getMediaService(String cpId);

    WxCpService getWxCpService(String cpId, Integer agentId);

    WxCpService getWxCpService(Integer agentId);

    WxCpService getWxCpService(String agentName);

    WxCpTagService getWxTagService(CpId cpId);

    WxCpOAuth2Service getWxCpOAuth2Service(Integer agentId);

}
