package com.greenstone.mes.bom.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomImportReq {

    private String projectCode;

    private String componentCode;

    private String componentName;

    private String componentVersion;

}
