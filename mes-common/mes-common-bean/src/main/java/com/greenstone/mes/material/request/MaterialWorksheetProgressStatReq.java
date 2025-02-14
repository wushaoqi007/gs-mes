package com.greenstone.mes.material.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialWorksheetProgressStatReq {

    private String projectCode;

    private String componentCode;

    private String partCode;

}
