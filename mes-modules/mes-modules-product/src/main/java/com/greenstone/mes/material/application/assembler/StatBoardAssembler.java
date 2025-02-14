package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.StatWeekQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.domain.converter.EnumConverter;
import com.greenstone.mes.material.domain.entity.StatPartsProgress;
import com.greenstone.mes.material.domain.entity.StatResultWeek;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.infrastructure.util.StatUtil;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-03-01-10:49
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = EnumConverter.class,
        imports = {List.class, StrUtil.class, StockAction.class}
)
public interface StatBoardAssembler {

    default StatWeekQuery toStatReworkQuery(StatWeekQuery query) {
        if (StrUtil.isEmpty(query.getMonth())) {
            StatWeekQuery statWeekQuery = StatWeekQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            // 默认搜索本月及上月数据
            statWeekQuery.setMonthStart(StatUtil.reduceOneMonth(new Date()));
            statWeekQuery.setMonthEnd(StatUtil.monthToStr(new Date()));
            return statWeekQuery;
        } else {
            return StatWeekQuery.builder().monthStart(StatUtil.reduceOneMonth(StatUtil.monthStrToDate(query.getMonth()))).monthEnd(query.getMonth()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
        }
    }

    default List<StatProjectAnalyseR> toStatProjectAnalyseR(List<StatPartsProgress> statPartsProgressList) {
        List<StatProjectAnalyseR> result = new ArrayList<>();
        Map<String, List<StatPartsProgress>> groupByCustomer = statPartsProgressList.stream().collect(Collectors.groupingBy(StatPartsProgress::getCustomerShortName));
        groupByCustomer.forEach((customer, list) -> {
            AtomicInteger sum = new AtomicInteger();
            Map<String, Long> groupByProject = list.stream().collect(Collectors.groupingBy(StatPartsProgress::getProjectCode, Collectors.counting()));
            groupByProject.forEach((projectCode, num) -> sum.addAndGet(num.intValue()));
            result.add(StatProjectAnalyseR.builder().customer(customer).number(sum.get()).build());
        });
        return result;
    }

    default StatProjectCountR toStatProjectCountR(List<StatPartsProgress> statPartsProgressList) {
        Map<String, List<StatPartsProgress>> groupByProject = statPartsProgressList.stream().collect(Collectors.groupingBy(StatPartsProgress::getProjectCode));
        int paperNum = statPartsProgressList.stream().mapToInt(s -> s.getPaperNum() != null ? s.getPaperNum() : 0).sum();
        int partNum = statPartsProgressList.stream().mapToInt(s -> s.getPartNum() != null ? s.getPartNum() : 0).sum();
        return StatProjectCountR.builder().projectNum(groupByProject == null ? 0 : groupByProject.size()).partNum(partNum).paperNum(paperNum).build();
    }

    default List<StatWeekReworkR> toStatWeekReworkR(List<StatResultWeek> statResultWeekList, StatWeekQuery query) {
        List<StatWeekReworkR> result = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
            for (int i = 1; i < 5; i++) {
                int finalD = d;
                int finalI = i;
                List<StatResultWeek> findList = statResultWeekList.stream().filter(s -> s.getStatisticMonth().equals(String.valueOf(finalD)) && s.getMonthWeek() == finalI).collect(Collectors.toList());
                int paperCheckNum = findList.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
                int paperReworkNum = findList.stream().mapToInt(s -> s.getPaperReworkNum() != null ? s.getPaperReworkNum() : 0).sum();
                String reworkRate = paperCheckNum == 0 ? "0%" : decimalFormat.format((double) paperReworkNum / (double) paperCheckNum);
                result.add(StatWeekReworkR.builder().day(StatUtil.getDayStrByWeek(String.valueOf(finalD), finalI)).reworkRate(reworkRate).build());
            }
        }
        return result;
    }

    default List<StatProjectProgressR> toStatProjectProgressR(List<StatPartsProgress> statPartsProgressList) {
        List<StatProjectProgressR> statProjectProgressRList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        Map<String, List<StatPartsProgress>> groupByProject = statPartsProgressList.stream().collect(Collectors.groupingBy(StatPartsProgress::getProjectCode));
        groupByProject.forEach((projectCode, list) -> {
            int partNum = list.stream().mapToInt(s -> s.getPartNum() != null ? s.getPartNum() : 0).sum();
            int deliverNum = list.stream().mapToInt(s -> s.getDeliverPartNum() != null ? s.getDeliverPartNum() : 0).sum();
            int finishedNum = list.stream().mapToInt(s -> s.getFinishedPartNum() != null ? s.getFinishedPartNum() : 0).sum();
            String deliverRate = partNum == 0 ? "0%" : decimalFormat.format((double) deliverNum / (double) partNum);
            String finishedRate = partNum == 0 ? "0%" : decimalFormat.format((double) finishedNum / (double) partNum);
            statProjectProgressRList.add(StatProjectProgressR.builder().projectCode(projectCode).deliverRate(deliverRate).finishedRate(finishedRate).build());
        });
        return statProjectProgressRList;
    }

    StatPartsProgressR toStatPartsProgressR(StatPartsProgress statPartsProgress);

    List<StatPartsProgressR> toStatPartsProgressRList(List<StatPartsProgress> statPartsProgressList);
}
