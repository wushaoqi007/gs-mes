package com.greenstone.mes.oa.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-07-13-9:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WxLoginQrCodeR {
    /**
     * CorpID
     */
    private String appId;
    /**
     * 应用ID
     */
    private String agentId;
    private String redirectUrl;
    /**
     * 用于保持请求和回调的状态，回调时原样带回
     */
    private String state;
}
