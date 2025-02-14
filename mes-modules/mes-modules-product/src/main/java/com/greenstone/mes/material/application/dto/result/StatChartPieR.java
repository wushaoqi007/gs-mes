package com.greenstone.mes.material.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统计图表（饼图）数据格式
 *
 * @author wushaoqi
 * @date 2023-03-16-9:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatChartPieR {

    private Title title;

    /**
     * 图表说明
     */
    private StatChartBarSimpleR.Legend legend;

    /**
     * 系列：数据集
     */
    private List<Series> series;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Title {
        private String text;
        private String subtext;
        private String left;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Legend {
        /**
         * 朝向：竖（vertical）
         */
        private String orient;
        /**
         * left(左)；center(居中)
         */
        private String left;
        /**
         * 图例类型数据
         */
        private List<String> data;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Series {
        private String name;
        /**
         * 柱状图：bar;折线图：line
         */
        private String type;
        private List<SeriesData> data;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SeriesData {
            private Integer value;
            private String name;
        }
    }
}
