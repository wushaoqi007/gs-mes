package com.greenstone.mes.material.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatProjectProgressR {

    private String projectCode;
    private String deliverRate;
    private String finishedRate;
}
