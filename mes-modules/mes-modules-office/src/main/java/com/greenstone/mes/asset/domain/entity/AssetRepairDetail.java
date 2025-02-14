package com.greenstone.mes.asset.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssetRepairDetail {

    private String id;
    private String serialNo;
    private String barCode;
    private String name;
    private String specification;
    private Long receivedId;
    private String receivedBy;
    private String location;
}
