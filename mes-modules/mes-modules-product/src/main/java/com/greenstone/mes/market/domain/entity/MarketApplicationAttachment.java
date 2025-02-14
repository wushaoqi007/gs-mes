package com.greenstone.mes.market.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketApplicationAttachment {

    private String id;

    private String serialNo;

    private String name;

    private String path;

}
