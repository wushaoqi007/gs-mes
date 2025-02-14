package com.greenstone.mes.material.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-03-01-9:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatMonthR {

    private String provider;

    private List<StatR> statList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatR {
        private String month;
        private Integer paperNum;
        private Integer overdueNum;
        private String overdueRate;
        private Integer overdueThreeDaysNum;
        private String overdueThreeDaysRate;
    }
}
