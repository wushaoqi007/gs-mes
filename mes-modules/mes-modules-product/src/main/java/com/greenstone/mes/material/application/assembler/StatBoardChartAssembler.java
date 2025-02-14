package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.application.dto.result.StatChartBarR;
import com.greenstone.mes.material.application.dto.result.StatMonthR;
import com.greenstone.mes.material.application.dto.result.StatProjectAnalyseR;
import com.greenstone.mes.material.domain.converter.EnumConverter;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-03-23-10:49
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = EnumConverter.class,
        imports = {List.class, StrUtil.class, StockAction.class}
)
public interface StatBoardChartAssembler {

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

    default StatChartBarR toProjectAnalyseChartR(List<StatProjectAnalyseR> statProjectAnalyseRList) {
        // 在制项目分析图表：柱状图
        StatChartBarR statChartBarR = StatChartBarR.builder().build();
        if (CollUtil.isNotEmpty(statProjectAnalyseRList)) {
            // 排个序
            statProjectAnalyseRList.sort(Comparator.comparing(StatProjectAnalyseR::getNumber));
            List<String> customerList = statProjectAnalyseRList.stream().map(StatProjectAnalyseR::getCustomer).collect(Collectors.toList());
            // X轴为数值，echarts自动生成数值范围
            List<Object> xAxis = new ArrayList<>();
            statChartBarR.setXAxis(xAxis);
            xAxis.add(StatChartBarR.XAxisSimple.builder().type("value").build());
            // Y轴为客户名称
            statChartBarR.setYAxis(StatChartBarR.YAxis.builder().type("category").data(customerList).build());
            // 数据集
            List<StatChartBarR.Series> series = new ArrayList<>();
            StatChartBarR.Series.Label label = StatChartBarR.Series.Label.builder().show(true).position("top").build();
            List<Double> dataList = statProjectAnalyseRList.stream().map(a -> (double) a.getNumber()).collect(Collectors.toList());
            series.add(StatChartBarR.Series.builder().label(label).type("bar").name("项目数量").data(dataList).build());
            statChartBarR.setSeries(series);
            // 其他设置
            statChartBarR.setTooltip(StatChartBarR.Tooltip.builder().show(true).trigger("axis").build());
            statChartBarR.setTitle(StatChartBarR.Title.builder().text("在制项目数据分析").left("center").build());
        }
        return statChartBarR;
    }
}
