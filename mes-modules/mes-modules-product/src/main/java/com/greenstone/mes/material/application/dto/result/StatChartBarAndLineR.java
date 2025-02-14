package com.greenstone.mes.material.application.dto.result;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统计图表通用数据格式
 *
 * @author wushaoqi
 * @date 2023-03-16-9:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatChartBarAndLineR {

    /**
     * 标题
     */
    private Title title;

    /**
     * 图例解释
     */
    private Legend legend;
    /**
     * 提示组件
     */
    private Tooltip tooltip;

    @JsonProperty(value = "xAxis")
    private List<Object> xAxis;
    @JsonProperty(value = "yAxis")
    private List<YAxis> yAxis;
    /**
     * 系列：数据集
     */
    private List<Series> series;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Title {
        /**
         * 大标题
         */
        private String text;
        /**
         * 小标题
         */
        private String subtext;
        /**
         * left(左)；center(居中)
         */
        private String left;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class XAxis {
        /**
         * 类目型（category）
         */
        private String type;
        private String position;
        private AxisTick axisTick;
        private List<XAxisData> data;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class XAxisData {
            private String value;
            private TextStyle textStyle;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class TextStyle {
                private Integer fontSize;
                private Integer lineHeight;
            }

        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AxisTick {
            private Integer length;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class XAxisSimple {
        /**
         * 类目型（category）
         */
        private String type;
        private List<String> data;
        private boolean show;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YAxis {
        /**
         * 数值型（value）
         */
        private String type;
        private AxisLabel axisLabel;
        private String name;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AxisLabel {
            private String formatter;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Legend {
        /**
         * 朝向：竖（vertical），横（horizontal）
         */
        private String orient;
        /**
         * left(左)；center(中)；bottom（下）；bottom（右）
         */
        private String x;
        private String y;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tooltip {
        /**
         * 是否显示提示框组件
         */
        private boolean show;
        /**
         * 触发类型（'item'，数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用；'axis'，坐标轴触发，主要在柱状图，折线图等会使用类目轴的图表中使用；'none'，不触发。）
         */
        private String trigger;
        private String formatter;
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
        private Integer yAxisIndex;
        private boolean smooth;
        private List<Double> data;
        /**
         * stack相同的数据，会堆叠在一组
         */
        private String stack;

        /**
         * 文本标签
         */
        private Label label;
        private JSONObject areaStyle;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Label {
            /**
             * 是否显示数值
             */
            private boolean show;
            /**
             * 显示位置：上（top）
             */
            private String position;
            private String formatter;
        }
    }
}
