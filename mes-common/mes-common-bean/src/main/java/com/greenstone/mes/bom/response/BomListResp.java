package com.greenstone.mes.bom.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BomListResp {

    private Long bomId;

    private String bomCode;

    private String bomName;

    private String bomVersion;

    private String projectCode;

    private Integer publishStatus;

}
