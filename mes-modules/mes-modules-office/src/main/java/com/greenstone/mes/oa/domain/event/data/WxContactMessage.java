package com.greenstone.mes.oa.domain.event.data;

import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.Builder;
import lombok.Data;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;

/**
 * @author gu_renkai
 * @date 2022/11/16 16:03
 */
@Data
@Builder
public class WxContactMessage {

    private CpId cpId;
    private WxCpXmlMessage wxCpXmlMessage;
}
