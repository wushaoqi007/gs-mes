package com.greenstone.mes.market.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "message.mail")
@Component
@Data
public class BuyerConfig {

    private String buyer;

    private String wxcg;

}
