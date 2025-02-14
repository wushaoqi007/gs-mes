package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.oa.request.WxMsgSendCmd;

/**
 * @author wushaoqi
 * @date 2022-11-01-15:08
 */
public interface WxMsgSendService {
    /**
     * 群发消息
     *
     */
    void sendMsgToWx(WxMsgSendCmd msgSendReq);

}
