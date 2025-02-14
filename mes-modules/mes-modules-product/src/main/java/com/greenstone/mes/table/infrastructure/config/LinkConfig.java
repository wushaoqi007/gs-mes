package com.greenstone.mes.table.infrastructure.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LinkConfig {

    @Value("${app.domain}")
    private String domain;

    public String getDetailLink(Long functionId, Long itemId) {
        return StrUtil.format("https://{}/tables/{}/items/{}", domain, functionId, itemId);
    }

    public String getDetailLink(String functionId, String itemId) {
        return StrUtil.format("https://{}/tables/{}/items/{}", domain, functionId, itemId);
    }
}
