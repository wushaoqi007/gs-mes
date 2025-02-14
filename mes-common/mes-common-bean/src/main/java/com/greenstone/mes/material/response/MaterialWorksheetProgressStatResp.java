package com.greenstone.mes.material.response;

import lombok.*;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-11-09-13:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialWorksheetProgressStatResp {

    private List<ProgressStat> statList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ProgressStat {

        private String projectCode;

        private String componentCode;

        private String componentName;

        /**
         * 类型（项目、组件）
         */
        private String progressType;

        /**
         * 零件总数
         */
        private Double partTotal;

        private Double partToReceivedTotal;

        private String partToReceivedRate;

        private Double partFinishedTotal;

        private String partFinishedRate;

        private Double partUsedTotal;

        private String partUsedRate;

        /**
         * 图纸总数
         */
        private Double paperTotal;

        private Double paperToReceivedTotal;

        private String paperToReceivedRate;

        private Double paperFinishedTotal;

        private String paperFinishedRate;

        private Double paperUsedTotal;

        private String paperUsedRate;

    }
}
