package com.greenstone.mes.material.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueScanResult {

    private String processCode;

    private String materialCode;

    private String totalNumber;

}
