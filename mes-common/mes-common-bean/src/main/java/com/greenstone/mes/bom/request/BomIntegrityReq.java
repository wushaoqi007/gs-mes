package com.greenstone.mes.bom.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BomIntegrityReq {

    private String projectCode;

    private String componentCode;

    private String componentVersion;

    private String componentName;

    private Long suiteNumber;

}
