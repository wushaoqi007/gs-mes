package com.greenstone.mes.mq.dto.wxcp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxcpMsgDto implements Serializable {

    @Nullable
    private String cpName;

    private String agentName;

    private WxCpMessage message;

}
