package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.domain.converter.EnumConverter;
import com.greenstone.mes.material.infrastructure.enums.MonthTranslateAction;
import com.greenstone.mes.material.infrastructure.enums.NgType;
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
 * @date 2023-03-20-10:49
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = EnumConverter.class,
        imports = {List.class, StrUtil.class, StockAction.class}
)
public interface StatMonthChartAssembler {

    default StatChartBarR toMonthOweChartR(List<StatMonthR> statMonthRList, StatMonthQuery query) {
        // 月欠货分析图表：柱状图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statMonthRList)) {
            List<String> providerList = statMonthRList.stream().map(StatMonthR::getProvider).collect(Collectors.toList());
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
            List<Double> dataPaperList = new ArrayList<>();
            List<Double> dataOverdueList = new ArrayList<>();
            List<Double> dataOverdueThreeDaysList = new ArrayList<>();

            for (String provider : providerList) {
                // X轴为类目：加工商
                xAxisSimpleDataList.add(provider);
                // 数据集：订单图纸数量、超期交货数量、超期三天以上数量、超期占比，超期三天以上占比
                Optional<StatMonthR> providerData = statMonthRList.stream().filter(a -> a.getProvider().equals(provider)).findFirst();
                if (providerData.isPresent()) {
                    StatMonthR.StatR statR = providerData.get().getStatList().get(0);
                    dataPaperList.add((double) statR.getPaperNum());
                    dataOverdueList.add((double) statR.getOverdueNum());
                    dataOverdueThreeDaysList.add((double) statR.getOverdueThreeDaysNum());
                }
            }
            series.add(StatChartBarR.Series.builder().label(label).type("bar").name("订单图纸数").data(dataPaperList).build());
            series.add(StatChartBarR.Series.builder().label(label).type("bar").name("超期交货数").data(dataOverdueList).build());
            series.add(StatChartBarR.Series.builder().label(label).type("bar").name("超期三天以上数").data(dataOverdueThreeDaysList).build());
            statChartBarR.setSeries(series);
            // 其他设置
            statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarR.setTitle(StatChartBarR.Title.builder().text("月度欠货分析").subtext(query.getMonth()).left("center").build());
        }
        return statChartBarR;
    }

    default StatChartBarR toYearOweChartR(List<StatMonthR> statMonthRList, StatMonthQuery query) {
        // 年欠货分析图表：柱状图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        // 默认展示Total
        if (StrUtil.isEmpty(query.getProvider())) {
            query.setProvider("total");
            statMonthRList = statMonthRList.stream().filter(a -> a.getProvider().equals("total")).collect(Collectors.toList());
        }
        if (CollUtil.isNotEmpty(statMonthRList)) {
            List<StatMonthR.StatR> statList = statMonthRList.get(0).getStatList();
            List<String> monthList = statList.stream().map(a -> a.getMonth().length() < 6 ? a.getMonth() : MonthTranslateAction.getByMonth(Integer.parseInt(a.getMonth().substring(4))).getEnName()).collect(Collectors.toList());
            // X轴：月份
            List<Object> xAxis = new ArrayList<>();
            statChartBarR.setXAxis(xAxis);
            xAxis.add(StatChartBarR.XAxisSimple.builder().show(true).data(monthList).type("category").build());
            // Y轴为数值，echarts自动生成数值范围
            statChartBarR.setYAxis(StatChartBarR.YAxis.builder().type("value").build());
            // 数据集
            List<StatChartBarR.Series> series = new ArrayList<>();
            StatChartBarR.Series.Label label = StatChartBarR.Series.Label.builder().show(true).position("top").build();
            List<Double> dataPaperList = new ArrayList<>();
            List<Double> dataOverdueList = new ArrayList<>();
            List<Double> dataOverdueThreeDaysList = new ArrayList<>();

            for (StatMonthR.StatR statR : statList) {
                // 数据集：订单图纸数量、超期交货数量、超期三天以上数量、超期占比，超期三天以上占比
                dataPaperList.add((double) statR.getPaperNum());
                dataOverdueList.add((double) statR.getOverdueNum());
                dataOverdueThreeDaysList.add((double) statR.getOverdueThreeDaysNum());
            }

            series.add(StatChartBarR.Series.builder().label(label).type("bar").name("订单图纸数").data(dataPaperList).build());
            series.add(StatChartBarR.Series.builder().label(label).type("bar").name("超期交货数").data(dataOverdueList).build());
            series.add(StatChartBarR.Series.builder().label(label).type("bar").name("超期三天以上数").data(dataOverdueThreeDaysList).build());
            statChartBarR.setSeries(series);
            // 其他设置
            statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarR.setTitle(StatChartBarR.Title.builder().text("年度欠货分析 by Monthly").subtext(query.getProvider()).left("center").build());
        }
        return statChartBarR;
    }

    default StatChartBarR toReworkRateChartR(List<StatMonthReworkR> statMonthReworkRList, StatMonthQuery query) {
        // 月不良率汇总图表：折现图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statMonthReworkRList)) {
            List<StatMonthReworkR.StatR> statList = statMonthReworkRList.get(0).getStatList();
            List<String> monthList = statList.stream().map(a -> a.getMonth().length() < 6 ? a.getMonth() : MonthTranslateAction.getByMonth(Integer.parseInt(a.getMonth().substring(4))).getEnName()).collect(Collectors.toList());
            // X轴：月份
            List<Object> xAxis = new ArrayList<>();
            statChartBarR.setXAxis(xAxis);
            xAxis.add(StatChartBarR.XAxisSimple.builder().show(true).data(monthList).type("category").build());
            // Y轴为数值，echarts自动生成数值范围
            statChartBarR.setYAxis(StatChartBarR.YAxis.builder().axisLabel(StatChartBarR.YAxis.AxisLabel.builder().formatter("{value}%").build()).build());
            // 数据集
            List<StatChartBarR.Series> series = new ArrayList<>();
            StatChartBarR.Series.Label label = StatChartBarR.Series.Label.builder().show(true).position("top").formatter("{c}%").build();

            for (StatMonthReworkR statMonthReworkR : statMonthReworkRList) {
                List<Double> dataList = new ArrayList<>();
                for (StatMonthReworkR.StatR statR : statMonthReworkR.getStatList()) {
                    dataList.add(Double.parseDouble(statR.getReworkRate().substring(0, statR.getReworkRate().indexOf("%"))));
                }
                series.add(StatChartBarR.Series.builder().label(label).type("line").smooth(true).name(statMonthReworkR.getProvider()).data(dataList).build());
            }

            statChartBarR.setSeries(series);
            // 其他设置
            statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarR.setTitle(StatChartBarR.Title.builder().text("供应商零件不良率汇总").subtext(query.getYear()).left("center").build());
        }
        return statChartBarR;
    }

    default StatChartBarR toCheckChartR(List<StatMonthCheckR> statMonthCheckRList, StatMonthQuery query) {
        // 月送检数量汇总图表：柱状图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statMonthCheckRList)) {
            List<StatMonthCheckR.StatR> statList = statMonthCheckRList.get(0).getStatList();
            List<String> monthList = statList.stream().map(a -> a.getMonth().length() < 6 ? a.getMonth() : MonthTranslateAction.getByMonth(Integer.parseInt(a.getMonth().substring(4))).getEnName()).collect(Collectors.toList());
            // X轴：月份
            List<Object> xAxis = new ArrayList<>();
            statChartBarR.setXAxis(xAxis);
            xAxis.add(StatChartBarR.XAxisSimple.builder().show(true).data(monthList).type("category").build());
            // Y轴为数值，echarts自动生成数值范围
            statChartBarR.setYAxis(StatChartBarR.YAxis.builder().type("value").build());
            // 数据集
            List<StatChartBarR.Series> series = new ArrayList<>();
            StatChartBarR.Series.Label label = StatChartBarR.Series.Label.builder().show(true).position("top").build();

            for (StatMonthCheckR statMonthCheckR : statMonthCheckRList) {
                List<Double> dataList = new ArrayList<>();
                for (StatMonthCheckR.StatR statR : statMonthCheckR.getStatList()) {
                    dataList.add((double) statR.getCheckNum());
                }
                series.add(StatChartBarR.Series.builder().label(label).type("bar").name(statMonthCheckR.getProvider()).data(dataList).build());
            }

            statChartBarR.setSeries(series);
            // 其他设置
            statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarR.setTitle(StatChartBarR.Title.builder().text("供应商月送检种类数量").subtext(query.getYear()).left("center").build());
        }
        return statChartBarR;
    }

    default StatChartBarAndLineR toReworkTypeChartR(List<StatReworkTypeR> statReworkTypeRList, StatMonthQuery query) {
        // 零件不良统计图表：柱状重叠及折线图
        StatChartBarAndLineR statChartBarAndLineR = StatChartBarAndLineR.builder().build();
        // 默认不展示平均不良
        statReworkTypeRList = statReworkTypeRList.stream().filter(a -> !a.getProvider().equals("平均不良率")).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(statReworkTypeRList)) {
            List<String> providerList = statReworkTypeRList.stream().map(StatReworkTypeR::getProvider).collect(Collectors.toList());
            // X轴：加工商
            List<Object> xAxis = new ArrayList<>();
            statChartBarAndLineR.setXAxis(xAxis);
            xAxis.add(StatChartBarAndLineR.XAxisSimple.builder().show(true).data(providerList).type("category").build());
            // Y轴为双数值，echarts自动生成数值范围
            List<StatChartBarAndLineR.YAxis> yAxis = new ArrayList<>();
            yAxis.add(StatChartBarAndLineR.YAxis.builder().type("value").name("数量").build());
            yAxis.add(StatChartBarAndLineR.YAxis.builder().type("value").name("不良率").axisLabel(StatChartBarAndLineR.YAxis.AxisLabel.builder().formatter("{value}%").build()).build());
            statChartBarAndLineR.setYAxis(yAxis);
            // 数据集
            List<StatChartBarAndLineR.Series> series = new ArrayList<>();
            StatChartBarAndLineR.Series.Label rightLabel = StatChartBarAndLineR.Series.Label.builder().show(true).position("right").formatter("{c}%").build();

            List<NgType> parentNgType = NgType.getParentNgType();
            for (NgType ngType : parentNgType) {
                List<NgType> subNgType = NgType.getSubNgType(ngType.getType());
                for (NgType subType : subNgType) {
                    List<Double> dataList = new ArrayList<>();
                    series.add(StatChartBarAndLineR.Series.builder().type("bar").stack("1").name(ngType.getType() + subType.getName()).data(dataList).build());
                    for (String provider : providerList) {
                        double total = 0D;
                        Optional<StatReworkTypeR> providerData = statReworkTypeRList.stream().filter(a -> a.getProvider().equals(provider)).findFirst();
                        if (providerData.isPresent() && CollUtil.isNotEmpty(providerData.get().getStatList()) && CollUtil.isNotEmpty(providerData.get().getStatList().get(0).getReworkTypeList())) {
                            StatReworkTypeR.StatR statR = providerData.get().getStatList().get(0);
                            Optional<StatReworkTypeR.ReworkTypeR> reworkTypeR = statR.getReworkTypeList().stream().filter(a -> a.getNgType().equals(ngType.getType())).findFirst();
                            if (reworkTypeR.isPresent()) {
                                Optional<StatReworkTypeR.ReworkTypeR.SubNgTypeR> subNgTypeROptional = reworkTypeR.get().getSubNgTypeRList().stream().filter(a -> a.getSubNgType().equals(subType.getType())).findFirst();
                                if (subNgTypeROptional.isPresent()) {
                                    total = subNgTypeROptional.get().getNumber();
                                }
                            }
                        }
                        dataList.add(total);
                    }
                }
            }
            List<String> reworkRateList = statReworkTypeRList.stream().map(a -> a.getStatList().get(0).getReworkRate()).collect(Collectors.toList());
            List<Double> dataReworkList = reworkRateList.stream().map(a -> StrUtil.isEmpty(a) ? 0D : Double.parseDouble(a.substring(0, a.indexOf("%")))).collect(Collectors.toList());
            series.add(StatChartBarAndLineR.Series.builder().label(rightLabel).type("line").name("不良率").yAxisIndex(1).data(dataReworkList).build());

            statChartBarAndLineR.setSeries(series);
            // 其他设置
            statChartBarAndLineR.setLegend(StatChartBarAndLineR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarAndLineR.setTooltip(StatChartBarAndLineR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarAndLineR.setTitle(StatChartBarAndLineR.Title.builder().text("零件不良统计").subtext(query.getMonth()).left("center").build());
        }
        return statChartBarAndLineR;
    }

    default StatChartBarAndLineR toPlanChartR(List<StatMonthPlanR> statMonthPlanRList, StatMonthQuery query) {
        // 加工件数据汇总 by Monthly图表：折柱混合图
        StatChartBarAndLineR statChartBarAndLineR = StatChartBarAndLineR.builder().build();
        if (CollUtil.isNotEmpty(statMonthPlanRList)) {
            List<String> monthList = statMonthPlanRList.stream().map(a -> a.getMonth().length() < 6 ? a.getMonth() : MonthTranslateAction.getByMonth(Integer.parseInt(a.getMonth().substring(4))).getEnName()).collect(Collectors.toList());
            // X轴：月份
            List<Object> xAxis = new ArrayList<>();
            statChartBarAndLineR.setXAxis(xAxis);
            xAxis.add(StatChartBarAndLineR.XAxisSimple.builder().show(true).data(monthList).type("category").build());
            // Y轴为双数值，echarts自动生成数值范围
            List<StatChartBarAndLineR.YAxis> yAxis = new ArrayList<>();
            yAxis.add(StatChartBarAndLineR.YAxis.builder().type("value").name("零件数量").build());
            yAxis.add(StatChartBarAndLineR.YAxis.builder().type("value").name("图纸数量").build());
            statChartBarAndLineR.setYAxis(yAxis);
            // 数据集
            List<StatChartBarAndLineR.Series> series = new ArrayList<>();
            StatChartBarAndLineR.Series.Label topLabel = StatChartBarAndLineR.Series.Label.builder().show(true).position("top").build();
            StatChartBarAndLineR.Series.Label rightLabel = StatChartBarAndLineR.Series.Label.builder().show(true).position("right").build();

            List<Double> dataPartList = new ArrayList<>();
            List<Double> dataPaperList = new ArrayList<>();
            for (StatMonthPlanR statMonthPlanR : statMonthPlanRList) {
                dataPartList.add((double) statMonthPlanR.getPartNum());
                dataPaperList.add((double) statMonthPlanR.getPaperNum());
            }
            series.add(StatChartBarAndLineR.Series.builder().label(topLabel).type("bar").name("零件数量").data(dataPartList).build());
            series.add(StatChartBarAndLineR.Series.builder().label(rightLabel).type("line").name("图纸数量").yAxisIndex(1).data(dataPaperList).build());

            statChartBarAndLineR.setSeries(series);
            // 其他设置
            statChartBarAndLineR.setLegend(StatChartBarAndLineR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarAndLineR.setTooltip(StatChartBarAndLineR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarAndLineR.setTitle(StatChartBarAndLineR.Title.builder().text("加工件数据汇总 by Monthly").subtext(query.getYear()).left("center").build());
        }
        return statChartBarAndLineR;
    }

    default StatChartBarR toImportChartR(List<StatMonthPlanR> statMonthPlanRList, StatMonthQuery query) {
        // 设计出图数据汇总 by Monthly图表：柱状堆叠图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statMonthPlanRList)) {
            List<String> monthList = statMonthPlanRList.stream().map(a -> a.getMonth().length() < 6 ? a.getMonth() : MonthTranslateAction.getByMonth(Integer.parseInt(a.getMonth().substring(4))).getEnName()).collect(Collectors.toList());
            // X轴：月份
            List<Object> xAxis = new ArrayList<>();
            statChartBarR.setXAxis(xAxis);
            xAxis.add(StatChartBarR.XAxisSimple.builder().show(true).data(monthList).type("category").build());
            // Y轴为数值，echarts自动生成数值范围
            statChartBarR.setYAxis(StatChartBarR.YAxis.builder().type("value").build());
            // 数据集
            List<StatChartBarR.Series> series = new ArrayList<>();
            StatChartBarR.Series.Label topLabel = StatChartBarR.Series.Label.builder().show(true).position("top").build();

            List<Double> dataPartList = new ArrayList<>();
            List<Double> dataPaperList = new ArrayList<>();
            for (StatMonthPlanR statMonthPlanR : statMonthPlanRList) {
                dataPartList.add((double) statMonthPlanR.getPartNum());
                dataPaperList.add((double) statMonthPlanR.getPaperNum());
            }
            series.add(StatChartBarR.Series.builder().label(topLabel).type("bar").name("零件数量").data(dataPartList).build());
            series.add(StatChartBarR.Series.builder().label(topLabel).barGap("-100%").type("bar").name("图纸数量").data(dataPaperList).build());

            statChartBarR.setSeries(series);
            // 其他设置
            statChartBarR.setLegend(StatChartBarR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarR.setTitle(StatChartBarR.Title.builder().text("设计出图数据汇总 by Monthly").subtext(query.getYear()).left("center").build());
        }
        return statChartBarR;
    }

    default StatChartBarAndLineR toImportOverdueChartR(List<StatMonthDesignerOverdueR> statMonthDesignerOverdueRList, StatMonthQuery query) {
        // 出图及超期信息汇总 by Monthly图表：折柱混合图
        StatChartBarAndLineR statChartBarAndLineR = StatChartBarAndLineR.builder().build();
        if (CollUtil.isNotEmpty(statMonthDesignerOverdueRList)) {
            List<String> monthList = statMonthDesignerOverdueRList.stream().map(a -> a.getMonth().length() < 6 ? a.getMonth() : MonthTranslateAction.getByMonth(Integer.parseInt(a.getMonth().substring(4))).getEnName()).collect(Collectors.toList());
            // X轴：月份
            List<Object> xAxis = new ArrayList<>();
            statChartBarAndLineR.setXAxis(xAxis);
            xAxis.add(StatChartBarAndLineR.XAxisSimple.builder().show(true).data(monthList).type("category").build());
            // Y轴为双数值，echarts自动生成数值范围
            List<StatChartBarAndLineR.YAxis> yAxis = new ArrayList<>();
            yAxis.add(StatChartBarAndLineR.YAxis.builder().type("value").name("数量").build());
            yAxis.add(StatChartBarAndLineR.YAxis.builder().type("value").name("占比").axisLabel(StatChartBarAndLineR.YAxis.AxisLabel.builder().formatter("{value}%").build()).build());
            statChartBarAndLineR.setYAxis(yAxis);
            // 数据集
            List<StatChartBarAndLineR.Series> series = new ArrayList<>();
            StatChartBarAndLineR.Series.Label topLabel = StatChartBarAndLineR.Series.Label.builder().show(true).position("top").build();
            StatChartBarAndLineR.Series.Label rightLabel = StatChartBarAndLineR.Series.Label.builder().show(true).position("right").formatter("{c}%").build();

            List<Double> dataPlanList = new ArrayList<>();
            List<Double> dataActualList = new ArrayList<>();
            List<Double> dataOverdueList = new ArrayList<>();
            List<Double> dataRateList = new ArrayList<>();
            for (StatMonthDesignerOverdueR statMonthDesignerOverdueR : statMonthDesignerOverdueRList) {
                dataPlanList.add((double) statMonthDesignerOverdueR.getPlanNum());
                dataActualList.add((double) statMonthDesignerOverdueR.getActualNum());
                dataOverdueList.add((double) statMonthDesignerOverdueR.getOverdueNum());
                dataRateList.add(Double.parseDouble(statMonthDesignerOverdueR.getOverdueRate().substring(0, statMonthDesignerOverdueR.getOverdueRate().indexOf("%"))));
            }
            series.add(StatChartBarAndLineR.Series.builder().label(topLabel).type("bar").name("计划出图项目").data(dataPlanList).build());
            series.add(StatChartBarAndLineR.Series.builder().label(topLabel).type("bar").name("实际出图项目").data(dataActualList).build());
            series.add(StatChartBarAndLineR.Series.builder().label(topLabel).type("bar").name("超期项目").data(dataOverdueList).build());
            series.add(StatChartBarAndLineR.Series.builder().label(rightLabel).type("line").name("超期占比").yAxisIndex(1).data(dataRateList).build());

            statChartBarAndLineR.setSeries(series);
            // 其他设置
            statChartBarAndLineR.setLegend(StatChartBarAndLineR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarAndLineR.setTooltip(StatChartBarAndLineR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarAndLineR.setTitle(StatChartBarAndLineR.Title.builder().text("出图及超期信息汇总 by Monthly").subtext(query.getYear()).left("center").build());
        }
        return statChartBarAndLineR;
    }

    default StatChartBarAndLineR toSpecialChartR(List<StatMonthSpecialR> statMonthSpecialRList, StatMonthQuery query) {
        // 更新修加急数据汇总(图纸）by Monthly图表：折柱混合图
        StatChartBarAndLineR statChartBarAndLineR = StatChartBarAndLineR.builder().build();
        if (CollUtil.isNotEmpty(statMonthSpecialRList)) {
            List<String> monthList = statMonthSpecialRList.stream().map(a -> a.getMonth().length() < 6 ? a.getMonth() : MonthTranslateAction.getByMonth(Integer.parseInt(a.getMonth().substring(4))).getEnName()).collect(Collectors.toList());
            // X轴：月份
            List<Object> xAxis = new ArrayList<>();
            statChartBarAndLineR.setXAxis(xAxis);
            xAxis.add(StatChartBarAndLineR.XAxisSimple.builder().show(true).data(monthList).type("category").build());
            // Y轴为双数值，echarts自动生成数值范围
            List<StatChartBarAndLineR.YAxis> yAxis = new ArrayList<>();
            yAxis.add(StatChartBarAndLineR.YAxis.builder().type("value").name("数量").build());
            yAxis.add(StatChartBarAndLineR.YAxis.builder().type("value").name("占比").axisLabel(StatChartBarAndLineR.YAxis.AxisLabel.builder().formatter("{value}%").build()).build());
            statChartBarAndLineR.setYAxis(yAxis);
            // 数据集
            List<StatChartBarAndLineR.Series> series = new ArrayList<>();
            StatChartBarAndLineR.Series.Label topLabel = StatChartBarAndLineR.Series.Label.builder().show(true).position("top").build();
            StatChartBarAndLineR.Series.Label rightLabel = StatChartBarAndLineR.Series.Label.builder().show(true).position("top").formatter("{c}%").build();

            List<Double> dataPaperList = new ArrayList<>();
            List<Double> dataSpecialList = new ArrayList<>();
            List<Double> dataRateList = new ArrayList<>();
            for (StatMonthSpecialR statMonthSpecialR : statMonthSpecialRList) {
                dataPaperList.add((double) statMonthSpecialR.getTotal());
                dataSpecialList.add((double) statMonthSpecialR.getSpecialTotal());
                dataRateList.add(Double.parseDouble(statMonthSpecialR.getSpecialRate().substring(0, statMonthSpecialR.getSpecialRate().indexOf("%"))));
            }
            series.add(StatChartBarAndLineR.Series.builder().label(topLabel).type("line").areaStyle(new JSONObject()).name("图纸总数量").data(dataPaperList).build());
            series.add(StatChartBarAndLineR.Series.builder().label(topLabel).type("line").areaStyle(new JSONObject()).name("更新、修、加急总量").data(dataSpecialList).build());
            series.add(StatChartBarAndLineR.Series.builder().label(rightLabel).type("line").name("更新、修、加急占比").yAxisIndex(1).data(dataRateList).build());

            statChartBarAndLineR.setSeries(series);
            // 其他设置
            statChartBarAndLineR.setLegend(StatChartBarAndLineR.Legend.builder().orient("horizontal").x("center").y("bottom").build());
            statChartBarAndLineR.setTooltip(StatChartBarAndLineR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarAndLineR.setTitle(StatChartBarAndLineR.Title.builder().text("更新修加急数据汇总(图纸）by Monthly").subtext(query.getYear()).left("center").build());
        }
        return statChartBarAndLineR;
    }

}
