package com.greenstone.mes.material.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WorksheetDetailResp {


    private String worksheetCode;

    private String projectCode;

    private String componentCode;

    private String componentName;

    private Integer partId;

    private String partCode;

    private String partVersion;

    private String partName;

    private String number;

    private String designer;

}
