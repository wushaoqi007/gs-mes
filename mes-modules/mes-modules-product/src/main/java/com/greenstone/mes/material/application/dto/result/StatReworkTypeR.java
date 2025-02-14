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
public class StatReworkTypeR {

    private String provider;

    private List<StatR> statList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatR {
        private String lastTimeReworkRate;
        private String reworkRate;
        private Integer checkNum;
        private Integer reworkNum;
        private List<ReworkTypeR> reworkTypeList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReworkTypeR {
        private String ngType;
        private List<SubNgTypeR> subNgTypeRList;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SubNgTypeR {
            private String subNgType;
            private Integer number;
        }
    }
}
