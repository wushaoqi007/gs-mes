package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.material.application.dto.result.StatChartBarR;
import com.greenstone.mes.material.application.dto.result.StatDailyFinishR;
import com.greenstone.mes.material.application.dto.result.StatDailyR;
import com.greenstone.mes.material.domain.converter.EnumConverter;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-03-16-10:49
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = EnumConverter.class,
        imports = {List.class, StrUtil.class, StockAction.class}
)
public interface StatDailyChartAssembler {

    default StatChartBarR toOweStatChartR(List<StatDailyR> statDailyRList) {
        // 欠货数据汇总图表：柱状图
        return toChartR(statDailyRList, "bar", "欠货数据汇总 by Weekly");
    }

    default StatChartBarR toOweDistributionStatChartR(List<StatDailyR> statDailyRList) {
        // 欠货数据分布图表：折线堆叠图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statDailyRList)) {
            // 默认不展示Total
            statDailyRList = statDailyRList.stream().filter(a -> !a.getProvider().equals("total")).collect(Collectors.toList());
            if (CollUtil.isEmpty(statDailyRList)) {
                return statChartBarR;
            }
            List<String> dayList = statDailyRList.get(0).getStatList().stream().map(StatDailyR.StatPartR::getDay).collect(Collectors.toList());
            // X轴：分为两行，第一行为零件图纸，第二行为日期
            List<Object> xAxis = new ArrayList<>();
            statChartBarR.setXAxis(xAxis);
            List<String> xAxisSimpleDataList = new ArrayList<>();
            xAxis.add(StatChartBarR.XAxisSimple.builder().data(xAxisSimpleDataList).type("category").build());
            List<StatChartBarR.XAxis.XAxisData> xAxisDataList = new ArrayList<>();
            xAxis.add(StatChartBarR.XAxis.builder().data(xAxisDataList).type("category").position("bottom").axisTick(StatChartBarR.XAxis.AxisTick.builder().length(50).build()).build());

            StatChartBarR.XAxis.XAxisData.TextStyle textStyle = StatChartBarR.XAxis.XAxisData.TextStyle.builder().fontSize(16).lineHeight(70).build();

            // Y轴为数值，echarts自动生成数值范围
            statChartBarR.setYAxis(StatChartBarR.YAxis.builder().type("value").build());
            for (String day : dayList) {
                // X轴为类目：零件图纸
                xAxisSimpleDataList.add("零件");
                xAxisSimpleDataList.add("图纸");
                // X轴上级分组：日期
                xAxisDataList.add(StatChartBarR.XAxis.XAxisData.builder().textStyle(textStyle).value(day.substring(day.indexOf("-") + 1)).build());
            }

            // 数据集
            List<StatChartBarR.Series> series = new ArrayList<>();
            StatChartBarR.Series.Label label = StatChartBarR.Series.Label.builder().show(true).position("top").build();
            for (StatDailyR dailyR : statDailyRList) {
                List<Double> dataList = new ArrayList<>();
                for (StatDailyR.StatPartR statPartR : dailyR.getStatList()) {
                    dataList.add((double) statPartR.getPartNum());
                    dataList.add((double) statPartR.getPaperNum());
                }
                series.add(StatChartBarR.Series.builder().label(label).areaStyle(new JSONObject()).type("line").name(dailyR.getProvider()).data(dataList).build());
            }
            statChartBarR.setSeries(series);
            // 其他设置
            statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarR.setTitle(StatChartBarR.Title.builder().text("欠货数据分布").left("center").build());
        }
        return statChartBarR;
    }

    default StatChartBarR toDailyDeliveryChartR(List<StatDailyR> statDailyRList) {
        // 日交货数据汇总图表：柱状图
        return toChartR(statDailyRList, "bar", "交货数据汇总 by Weekly");
    }

    default StatChartBarR toDailyFinishChartR(List<StatDailyFinishR> statDailyFinishRList) {
        // 日完成数据汇总图表：堆叠柱状图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statDailyFinishRList)) {
            // 默认不展示Total
            statDailyFinishRList = statDailyFinishRList.stream().filter(a -> !a.getProvider().equals("total")).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(statDailyFinishRList)) {
                List<String> providerList = statDailyFinishRList.stream().map(StatDailyFinishR::getProvider).collect(Collectors.toList());
                // X轴：加工商
                List<Object> xAxis = new ArrayList<>();
                statChartBarR.setXAxis(xAxis);
                List<String> xAxisSimpleDataList = new ArrayList<>();
                xAxis.add(StatChartBarR.XAxisSimple.builder().show(true).data(xAxisSimpleDataList).type("category").build());
                // Y轴为数值，echarts自动生成数值范围
                statChartBarR.setYAxis(StatChartBarR.YAxis.builder().type("value").build());
                // 数据集
                List<StatChartBarR.Series> series = new ArrayList<>();
                StatChartBarR.Series.Label label = StatChartBarR.Series.Label.builder().show(true).position("top").build();
                List<Double> dataPlanList = new ArrayList<>();
                List<Double> dataActualList = new ArrayList<>();

                for (String provider : providerList) {
                    // X轴为类目：加工商
                    xAxisSimpleDataList.add(provider);
                    // 数据集：计划和实际数量
                    Optional<StatDailyFinishR> providerData = statDailyFinishRList.stream().filter(a -> a.getProvider().equals(provider)).findFirst();
                    if (providerData.isPresent()) {
                        StatDailyFinishR statDailyFinishR = providerData.get();
                        dataPlanList.add((double) statDailyFinishR.getStatList().get(0).getPlanNum());
                        dataActualList.add((double) statDailyFinishR.getStatList().get(0).getActualNum());
                    }
                }
                // 数据堆叠分类：日期
                String day = statDailyFinishRList.get(0).getStatList().get(0).getDay();
                series.add(StatChartBarR.Series.builder().label(label).type("bar").name("计划").data(dataPlanList).build());
                series.add(StatChartBarR.Series.builder().label(label).type("bar").barGap("-100%").name("实际").data(dataActualList).build());
                statChartBarR.setSeries(series);
                // 其他设置
                statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
                statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
                statChartBarR.setTitle(StatChartBarR.Title.builder().text("计划与实际数据对比（图纸）by Daily").subtext(day).left("center").build());
            }
        }
        return statChartBarR;
    }

    default StatChartBarR toDailyFinishDistributionChartR(List<StatDailyFinishR> statDailyFinishRList) {
        // 日完成数据分布图表：重叠柱状图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statDailyFinishRList)) {
            // 默认展示Total
            StatDailyFinishR statDailyFinishR = statDailyFinishRList.get(0);
            if (statDailyFinishRList.size() > 1) {
                Optional<StatDailyFinishR> total = statDailyFinishRList.stream().filter(a -> a.getProvider().equals("total")).findFirst();
                if (total.isPresent()) {
                    statDailyFinishR = total.get();
                }
            }
            List<StatDailyFinishR.StatR> statList = statDailyFinishR.getStatList();
            if (CollUtil.isNotEmpty(statList)) {
                List<String> dayList = statList.stream().map(StatDailyFinishR.StatR::getDay).collect(Collectors.toList());
                // X轴为类目：日期
                // X轴：日期
                List<Object> xAxis = new ArrayList<>();
                statChartBarR.setXAxis(xAxis);
                xAxis.add(StatChartBarR.XAxisSimple.builder().show(true).data(dayList).type("category").build());
                // Y轴为数值，echarts自动生成数值范围
                statChartBarR.setYAxis(StatChartBarR.YAxis.builder().type("value").build());
                // 数据集
                List<StatChartBarR.Series> series = new ArrayList<>();
                StatChartBarR.Series.Label label = StatChartBarR.Series.Label.builder().show(true).position("top").build();
                List<Double> dataPlanList = new ArrayList<>();
                List<Double> dataActualList = new ArrayList<>();
                for (StatDailyFinishR.StatR statR : statList) {
                    dataPlanList.add((double) statR.getPlanNum());
                    dataActualList.add((double) statR.getActualNum());
                }
                series.add(StatChartBarR.Series.builder().label(label).type("bar").name("计划").data(dataPlanList).build());
                series.add(StatChartBarR.Series.builder().label(label).type("bar").barGap("-100%").name("实际").data(dataActualList).build());
                statChartBarR.setSeries(series);
                // 其他设置
                statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
                statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
                statChartBarR.setTitle(StatChartBarR.Title.builder().text("日完成数据分布").subtext(statDailyFinishR.getProvider()).left("center").build());
            }
        }
        return statChartBarR;
    }

    default StatChartBarR toDailyPlanChartR(List<StatDailyR> statDailyRList) {
        // 待加工数据汇总图表：堆叠柱状图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statDailyRList)) {
            // 默认不展示Total
            statDailyRList = statDailyRList.stream().filter(a -> !a.getProvider().equals("total")).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(statDailyRList)) {
                List<String> providerList = statDailyRList.stream().map(StatDailyR::getProvider).collect(Collectors.toList());
                // X轴：加工商
                List<Object> xAxis = new ArrayList<>();
                statChartBarR.setXAxis(xAxis);
                List<String> xAxisSimpleDataList = new ArrayList<>();
                xAxis.add(StatChartBarR.XAxisSimple.builder().show(true).data(xAxisSimpleDataList).type("category").build());
                // Y轴为数值，echarts自动生成数值范围
                statChartBarR.setYAxis(StatChartBarR.YAxis.builder().type("value").build());
                // 数据集
                List<StatChartBarR.Series> series = new ArrayList<>();
                StatChartBarR.Series.Label label = StatChartBarR.Series.Label.builder().show(true).position("top").build();
                List<Double> dataPartList = new ArrayList<>();
                List<Double> dataPaperList = new ArrayList<>();

                for (String provider : providerList) {
                    // X轴为类目：加工商
                    xAxisSimpleDataList.add(provider);
                    // 数据集：计划和实际数量
                    Optional<StatDailyR> providerData = statDailyRList.stream().filter(a -> a.getProvider().equals(provider)).findFirst();
                    if (providerData.isPresent()) {
                        StatDailyR statDailyR = providerData.get();
                        Optional<StatDailyR.StatPartR> planOption = statDailyR.getStatList().stream().filter(a -> a.getDay().equals("待制品总量")).findFirst();
                        if (planOption.isPresent()) {
                            StatDailyR.StatPartR plan = planOption.get();
                            dataPartList.add((double) plan.getPartNum());
                            dataPaperList.add((double) plan.getPaperNum());
                        }
                    }
                }
                // 数据堆叠分类：日期
                String day = statDailyRList.get(0).getStatList().get(0).getDay();
                series.add(StatChartBarR.Series.builder().label(label).type("bar").name("零件数量").data(dataPartList).build());
                series.add(StatChartBarR.Series.builder().label(label).type("bar").barGap("-100%").name("图纸数量").data(dataPaperList).build());
                statChartBarR.setSeries(series);
                // 其他设置
                statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
                statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
                statChartBarR.setTitle(StatChartBarR.Title.builder().text("待加工件数据汇总").subtext(day).left("center").build());
            }
        }
        return statChartBarR;
    }

    default StatChartBarR toChartR(List<StatDailyR> statDailyRList, String chartType, String title) {
        // 日交货数据汇总图表：柱状图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statDailyRList)) {
            // 默认展示Total
            StatDailyR statDailyR = statDailyRList.get(0);
            if (statDailyRList.size() > 1) {
                Optional<StatDailyR> total = statDailyRList.stream().filter(a -> a.getProvider().equals("total")).findFirst();
                if (total.isPresent()) {
                    statDailyR = total.get();
                }
            }
            List<StatDailyR.StatPartR> statList = statDailyR.getStatList();
            if (CollUtil.isNotEmpty(statList)) {
                List<String> dayList = statList.stream().map(StatDailyR.StatPartR::getDay).collect(Collectors.toList());
                // X轴：分为两行，第一行为零件图纸，第二行为日期
                List<Object> xAxis = new ArrayList<>();
                statChartBarR.setXAxis(xAxis);
                List<String> xAxisSimpleDataList = new ArrayList<>();
                xAxis.add(StatChartBarR.XAxisSimple.builder().data(xAxisSimpleDataList).type("category").build());
                List<StatChartBarR.XAxis.XAxisData> xAxisDataList = new ArrayList<>();
                xAxis.add(StatChartBarR.XAxis.builder().data(xAxisDataList).type("category").position("bottom").axisTick(StatChartBarR.XAxis.AxisTick.builder().length(50).build()).build());

                StatChartBarR.XAxis.XAxisData.TextStyle textStyle = StatChartBarR.XAxis.XAxisData.TextStyle.builder().fontSize(16).lineHeight(70).build();

                // Y轴为数值，echarts自动生成数值范围
                statChartBarR.setYAxis(StatChartBarR.YAxis.builder().type("value").build());
                for (String day : dayList) {
                    // X轴为类目：零件图纸
                    xAxisSimpleDataList.add("零件");
                    xAxisSimpleDataList.add("图纸");
                    // X轴上级分组：日期
                    xAxisDataList.add(StatChartBarR.XAxis.XAxisData.builder().textStyle(textStyle).value(day.substring(day.indexOf("-") + 1)).build());
                }

                // 数据集
                List<StatChartBarR.Series> series = new ArrayList<>();
                StatChartBarR.Series.Label label = StatChartBarR.Series.Label.builder().show(true).position("top").build();
                List<Double> dataList = new ArrayList<>();
                for (StatDailyR.StatPartR statPartR : statDailyR.getStatList()) {
                    dataList.add((double) statPartR.getPartNum());
                    dataList.add((double) statPartR.getPaperNum());
                }
                series.add(StatChartBarR.Series.builder().label(label).areaStyle(new JSONObject()).type(chartType).name(statDailyR.getProvider()).data(dataList).build());
                statChartBarR.setSeries(series);
                // 其他设置
                statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
                statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
                statChartBarR.setTitle(StatChartBarR.Title.builder().text(title).subtext(statDailyR.getProvider()).left("center").build());
            }
        }
        return statChartBarR;
    }

}
