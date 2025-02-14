package com.greenstone.mes.wxcp.interfaces.rest.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxOauth2Config {

    private String cpId;

    private Integer agentId;

    private String state;

    private String redirectUrl;
}
