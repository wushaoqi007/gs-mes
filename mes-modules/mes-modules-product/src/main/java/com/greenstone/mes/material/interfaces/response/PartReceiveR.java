package com.greenstone.mes.material.interfaces.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartReceiveR {

    private Long id;

    private Long partsGroupId;

    private Long materialId;

    private String worksheetCode;

    private String projectCode;

    private String componentCode;

    private String partCode;

    private String partVersion;

    private String partName;

    private Long number;

}
