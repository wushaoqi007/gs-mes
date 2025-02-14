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
public class StatDailyR {

    private String provider;

    private List<StatPartR> statList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatPartR {
        private String day;
        private Integer partNum;
        private Integer paperNum;
    }
}
