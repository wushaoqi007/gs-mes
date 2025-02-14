package com.greenstone.mes.wxcp.domain.helper;

import com.greenstone.mes.wxcp.domain.types.CpId;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;

/**
 * @author gu_renkai
 * @date 2022/10/26 9:10
 */

public interface WxMsgService {

    void sendMsg(String agentName, WxCpMessage message);

    void sendMsg(CpId cpId, WxCpMessage message);

    void sendMsg(CpId cpId, Integer appId, WxCpMessage message);

    void updateTemplateCard(CpId cpId, Integer appId, String message);
}
