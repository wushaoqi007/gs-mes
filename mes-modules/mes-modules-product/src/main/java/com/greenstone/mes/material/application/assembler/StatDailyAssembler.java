package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.application.dto.StatQuery;
import com.greenstone.mes.material.application.dto.result.StatDailyFinishR;
import com.greenstone.mes.material.application.dto.result.StatDailyR;
import com.greenstone.mes.material.application.dto.result.StatPartsDataSourceR;
import com.greenstone.mes.material.domain.converter.EnumConverter;
import com.greenstone.mes.material.domain.entity.StatPartsDataSource;
import com.greenstone.mes.material.domain.entity.StatResultDaily;
import com.greenstone.mes.material.dto.cmd.StatDailyCmd;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.infrastructure.util.StatUtil;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.*;
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
public interface StatDailyAssembler {

    default StatQuery toStatQuery(StatDailyCmd statDailyCmd) {
        StatQuery query = StatQuery.builder().build();
        if (statDailyCmd != null && StrUtil.isNotEmpty(statDailyCmd.getStatisticDate())) {
            query.setStatisticDate(StatUtil.strToDate(statDailyCmd.getStatisticDate()));
        }
        return query;
    }

    default StatDailyQuery toStatDailyQuery(StatQuery query) {
        if (StrUtil.isEmpty(query.getStartTime()) || StrUtil.isEmpty(query.getEndTime())) {
            StatDailyQuery statDailyQuery = StatDailyQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            // 默认搜索当前周数据
            Date[] currentWeek = StatUtil.getCurrentWeek();
            statDailyQuery.setStartTime(StatUtil.dateToSimpleStr(currentWeek[0]));
            statDailyQuery.setEndTime(StatUtil.dateToSimpleStr(StatUtil.reduceOneDay(currentWeek[1])));
            return statDailyQuery;
        } else {
            return StatDailyQuery.builder().startTime(query.getStartTime()).endTime(query.getEndTime()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
        }
    }

    default StatDailyQuery toOweSourceQuery(StatQuery query) {
        if (StrUtil.isEmpty(query.getStartTime()) || StrUtil.isEmpty(query.getEndTime())) {
            // 默认搜索今天至4个月之前数据
            return StatDailyQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).startTime(StatUtil.fourMonthAgo(new Date())).endTime(StatUtil.dateToSimpleStr(new Date())).build();
        } else {
            return StatDailyQuery.builder().startTime(StatUtil.fourMonthAgo(StatUtil.strToDate(query.getEndTime()))).endTime(query.getEndTime()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
        }
    }

    default StatDailyQuery toFinishSourceQuery(StatQuery query) {
        return toOweSourceQuery(query);
    }

    default StatDailyQuery toPlanSourceQuery(StatQuery query) {
        if (StrUtil.isEmpty(query.getStartTime()) || StrUtil.isEmpty(query.getEndTime())) {
            // 默认搜索最远到4个月之前数据
            return StatDailyQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).startTime(StatUtil.fourMonthAgo(new Date())).endTime(null).build();
        } else {
            return StatDailyQuery.builder().startTime(query.getStartTime()).endTime(query.getEndTime()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
        }
    }

    default StatDailyQuery toStatMonthQuery(StatQuery query) {
        if (StrUtil.isEmpty(query.getStartTime()) || StrUtil.isEmpty(query.getEndTime())) {
            StatDailyQuery statDailyQuery = StatDailyQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            // 默认搜索当前月数据
            statDailyQuery.setStartTime(StatUtil.dateToSimpleStr(StatUtil.monthStart(new Date())));
            statDailyQuery.setEndTime(StatUtil.dateToSimpleStr(StatUtil.monthEnd(new Date())));
            return statDailyQuery;
        } else {
            return StatDailyQuery.builder().startTime(query.getStartTime()).endTime(query.getEndTime()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
        }
    }

    default StatDailyQuery toStatPlanQuery(StatQuery query) {
        StatDailyQuery statDailyQuery = StatDailyQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).build();
        // 默认搜索后10天数据
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        statDailyQuery.setStartTime(StatUtil.dateToSimpleStr(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        statDailyQuery.setEndTime(StatUtil.dateToSimpleStr(calendar.getTime()));
        return statDailyQuery;
    }

    default List<StatDailyR> toDailyOweR(List<StatResultDaily> statResultDailyList, StatDailyQuery query) {
        List<StatDailyR> resultList = new ArrayList<>();
        // 按加工商分组
        Map<String, List<StatResultDaily>> groupByProvider = statResultDailyList.stream().collect(Collectors.groupingBy(StatResultDaily::getProvider));
        groupByProvider.forEach((provider, list) -> {
            List<StatDailyR.StatPartR> statList = new ArrayList<>();
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                List<StatResultDaily> filterData = list.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                int partOweTotal = filterData.stream().mapToInt(s -> s.getPartOweTotal() != null ? s.getPartOweTotal() : 0).sum();
                int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperOweTotal() != null ? s.getPaperOweTotal() : 0).sum();
                statList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d)).partNum(partOweTotal).paperNum(paperOweTotal).build());
            }
            resultList.add(StatDailyR.builder().provider(provider).statList(statList).build());
        });
        // 汇总
        if (StrUtil.isEmpty(query.getProvider())) {
            List<StatDailyR.StatPartR> totalStatList = new ArrayList<>();
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                List<StatResultDaily> filterData = statResultDailyList.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                int partOweTotal = filterData.stream().mapToInt(s -> s.getPartOweTotal() != null ? s.getPartOweTotal() : 0).sum();
                int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperOweTotal() != null ? s.getPaperOweTotal() : 0).sum();
                totalStatList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d)).partNum(partOweTotal).paperNum(paperOweTotal).build());
            }
            resultList.add(StatDailyR.builder().provider("total").statList(totalStatList).build());
        }
        return resultList;
    }

    default List<StatDailyR> toDailyOweDistributionR(List<StatResultDaily> statResultDailyList, StatDailyQuery query) {
        List<StatDailyR> resultList = new ArrayList<>();
        // 按加工商分组
        Map<String, List<StatResultDaily>> groupByProvider = statResultDailyList.stream().collect(Collectors.groupingBy(StatResultDaily::getProvider));
        groupByProvider.forEach((provider, list) -> {
            List<StatDailyR.StatPartR> statList = new ArrayList<>();
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                // 第一天多补充一条欠货总数
                if (d.getTime() == StatUtil.strToDate(query.getStartTime()).getTime()) {
                    List<StatResultDaily> filterData = list.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                    int partOweTotal = filterData.stream().mapToInt(s -> s.getPartOweTotal() != null ? s.getPartOweTotal() : 0).sum();
                    int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperOweTotal() != null ? s.getPaperOweTotal() : 0).sum();
                    statList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d) + "之前").partNum(partOweTotal).paperNum(paperOweTotal).build());
                }
                List<StatResultDaily> filterData = list.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                int partOweTotal = filterData.stream().mapToInt(s -> s.getPartOweNum() != null ? s.getPartOweNum() : 0).sum();
                int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperOweNum() != null ? s.getPaperOweNum() : 0).sum();
                statList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d)).partNum(partOweTotal).paperNum(paperOweTotal).build());
            }
            resultList.add(StatDailyR.builder().provider(provider).statList(statList).build());
        });
        // 汇总
        if (StrUtil.isEmpty(query.getProvider())) {
            List<StatDailyR.StatPartR> totalStatList = new ArrayList<>();
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                // 第一天多补充一条欠货总数
                if (d.getTime() == StatUtil.strToDate(query.getStartTime()).getTime()) {
                    List<StatResultDaily> filterData = statResultDailyList.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                    int partOweTotal = filterData.stream().mapToInt(s -> s.getPartOweTotal() != null ? s.getPartOweTotal() : 0).sum();
                    int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperOweTotal() != null ? s.getPaperOweTotal() : 0).sum();
                    totalStatList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d) + "之前").partNum(partOweTotal).paperNum(paperOweTotal).build());
                }
                List<StatResultDaily> filterData = statResultDailyList.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                int partOweTotal = filterData.stream().mapToInt(s -> s.getPartOweNum() != null ? s.getPartOweNum() : 0).sum();
                int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperOweNum() != null ? s.getPaperOweNum() : 0).sum();
                totalStatList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d)).partNum(partOweTotal).paperNum(paperOweTotal).build());
            }
            resultList.add(StatDailyR.builder().provider("total").statList(totalStatList).build());
        }
        return resultList;
    }

    default List<StatDailyR> toDailyDeliveryR(List<StatResultDaily> statResultDailyList, StatDailyQuery query) {
        List<StatDailyR> resultList = new ArrayList<>();
        // 按加工商分组
        Map<String, List<StatResultDaily>> groupByProvider = statResultDailyList.stream().collect(Collectors.groupingBy(StatResultDaily::getProvider));
        groupByProvider.forEach((provider, list) -> {
            List<StatDailyR.StatPartR> statList = new ArrayList<>();
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                List<StatResultDaily> filterData = list.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                int partOweTotal = filterData.stream().mapToInt(s -> s.getPartDeliveryNum() != null ? s.getPartDeliveryNum() : 0).sum();
                int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperDeliveryNum() != null ? s.getPaperDeliveryNum() : 0).sum();
                statList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d)).partNum(partOweTotal).paperNum(paperOweTotal).build());
            }
            resultList.add(StatDailyR.builder().provider(provider).statList(statList).build());
        });
        // 汇总
        if (StrUtil.isEmpty(query.getProvider())) {
            List<StatDailyR.StatPartR> totalStatList = new ArrayList<>();
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                List<StatResultDaily> filterData = statResultDailyList.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                int partOweTotal = filterData.stream().mapToInt(s -> s.getPartDeliveryNum() != null ? s.getPartDeliveryNum() : 0).sum();
                int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperDeliveryNum() != null ? s.getPaperDeliveryNum() : 0).sum();
                totalStatList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d)).partNum(partOweTotal).paperNum(paperOweTotal).build());
            }
            resultList.add(StatDailyR.builder().provider("total").statList(totalStatList).build());
        }
        return resultList;
    }

    default List<StatDailyFinishR> toDailyFinishR(List<StatResultDaily> statResultDailyList, StatDailyQuery query) {
        List<StatDailyFinishR> resultList = new ArrayList<>();
        // 按加工商分组
        Map<String, List<StatResultDaily>> groupByProvider = statResultDailyList.stream().collect(Collectors.groupingBy(StatResultDaily::getProvider));
        groupByProvider.forEach((provider, list) -> {
            List<StatDailyFinishR.StatR> statList = new ArrayList<>();
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                List<StatResultDaily> filterData = list.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                int paperPlanNum = filterData.stream().mapToInt(s -> s.getPaperPlanNum() != null ? s.getPaperPlanNum() : 0).sum();
                int paperActualNum = filterData.stream().mapToInt(s -> s.getPaperDeliveryTotal() != null ? s.getPaperDeliveryTotal() : 0).sum();
                statList.add(StatDailyFinishR.StatR.builder().day(StatUtil.dateToSimpleStr(d)).planNum(paperPlanNum).actualNum(paperActualNum).build());
            }
            resultList.add(StatDailyFinishR.builder().provider(provider).statList(statList).build());
        });
        // 汇总
        if (StrUtil.isEmpty(query.getProvider())) {
            List<StatDailyFinishR.StatR> totalStatList = new ArrayList<>();
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                List<StatResultDaily> filterData = statResultDailyList.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                int paperPlanNum = filterData.stream().mapToInt(s -> s.getPaperPlanNum() != null ? s.getPaperPlanNum() : 0).sum();
                int paperActualNum = filterData.stream().mapToInt(s -> s.getPaperDeliveryTotal() != null ? s.getPaperDeliveryTotal() : 0).sum();
                totalStatList.add(StatDailyFinishR.StatR.builder().day(StatUtil.dateToSimpleStr(d)).planNum(paperPlanNum).actualNum(paperActualNum).build());
            }
            resultList.add(StatDailyFinishR.builder().provider("total").statList(totalStatList).build());
        }
        return resultList;
    }

    default List<StatDailyR> toDailyPlanR(List<StatResultDaily> statResultDailyList, StatDailyQuery query) {
        List<StatDailyR> resultList = new ArrayList<>();
        // 按加工商分组
        Map<String, List<StatResultDaily>> groupByProvider = statResultDailyList.stream().collect(Collectors.groupingBy(StatResultDaily::getProvider));
        groupByProvider.forEach((provider, list) -> {
            List<StatDailyR.StatPartR> statList = new ArrayList<>();
            // 待制品总量=欠货总量+计划总量（每天的计划量+最后一天总计划）
            int partWaitTotal = 0;
            int paperWaitTotal = 0;
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                // 第一天:欠货总数
                if (d.getTime() == StatUtil.strToDate(query.getStartTime()).getTime()) {
                    List<StatResultDaily> filterData = list.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                    int partOweTotal = filterData.stream().mapToInt(s -> s.getPartOweTotal() != null ? s.getPartOweTotal() : 0).sum();
                    int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperOweTotal() != null ? s.getPaperOweTotal() : 0).sum();
                    statList.add(StatDailyR.StatPartR.builder().day("欠货数据").partNum(partOweTotal).paperNum(paperOweTotal).build());
                    partWaitTotal += partOweTotal;
                    paperWaitTotal += paperOweTotal;
                } else {
                    // 计划量
                    List<StatResultDaily> filterData = list.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                    int partPlanNum = filterData.stream().mapToInt(s -> s.getPartPlanNum() != null ? s.getPartPlanNum() : 0).sum();
                    int paperPlanNum = filterData.stream().mapToInt(s -> s.getPaperPlanNum() != null ? s.getPaperPlanNum() : 0).sum();
                    partWaitTotal += partPlanNum;
                    paperWaitTotal += paperPlanNum;
                    statList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d)).partNum(partPlanNum).paperNum(paperPlanNum).build());
                }
                // 最后一天：多统计一个总计划
                if (d.getTime() == StatUtil.strToDate(query.getEndTime()).getTime()) {
                    // 计划量
                    List<StatResultDaily> filterData = list.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                    int partPlanTotal = filterData.stream().mapToInt(s -> s.getPartPlanTotal() != null ? s.getPartPlanTotal() : 0).sum();
                    int paperPlanTotal = filterData.stream().mapToInt(s -> s.getPaperPlanTotal() != null ? s.getPaperPlanTotal() : 0).sum();
                    partWaitTotal += partPlanTotal;
                    paperWaitTotal += paperPlanTotal;
                    statList.add(StatDailyR.StatPartR.builder().day(">" + StatUtil.dateToSimpleStr(d)).partNum(partPlanTotal).paperNum(paperPlanTotal).build());
                }
            }
            statList.add(StatDailyR.StatPartR.builder().day("待制品总量").partNum(partWaitTotal).paperNum(paperWaitTotal).build());
            statList.sort(Comparator.comparing(StatDailyR.StatPartR::getDay));
            resultList.add(StatDailyR.builder().provider(provider).statList(statList).build());
        });
        // 汇总
        if (StrUtil.isEmpty(query.getProvider())) {
            List<StatDailyR.StatPartR> totalStatList = new ArrayList<>();
            // 待制品总量=欠货总量+计划总量（每天的计划量+最后一天总计划）
            int partWaitTotal = 0;
            int paperWaitTotal = 0;
            for (Date d = StatUtil.strToDate(query.getStartTime()); d.getTime() <= StatUtil.strToDate(query.getEndTime()).getTime(); d = StatUtil.plusOneDay(d)) {
                Date finalD = d;
                // 第一天:欠货总数,待制品总量
                if (d.getTime() == StatUtil.strToDate(query.getStartTime()).getTime()) {
                    List<StatResultDaily> filterData = statResultDailyList.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                    int partOweTotal = filterData.stream().mapToInt(s -> s.getPartOweTotal() != null ? s.getPartOweTotal() : 0).sum();
                    int paperOweTotal = filterData.stream().mapToInt(s -> s.getPaperOweTotal() != null ? s.getPaperOweTotal() : 0).sum();
                    totalStatList.add(StatDailyR.StatPartR.builder().day("欠货数据").partNum(partOweTotal).paperNum(paperOweTotal).build());
                    partWaitTotal += partOweTotal;
                    paperWaitTotal += paperOweTotal;
                } else {
                    // 计划量
                    List<StatResultDaily> filterData = statResultDailyList.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                    int partPlanNum = filterData.stream().mapToInt(s -> s.getPartPlanNum() != null ? s.getPartPlanNum() : 0).sum();
                    int paperPlanNum = filterData.stream().mapToInt(s -> s.getPaperPlanNum() != null ? s.getPaperPlanNum() : 0).sum();
                    partWaitTotal += partPlanNum;
                    paperWaitTotal += paperPlanNum;
                    totalStatList.add(StatDailyR.StatPartR.builder().day(StatUtil.dateToSimpleStr(d)).partNum(partPlanNum).paperNum(paperPlanNum).build());
                }
                // 最后一天：多统计一个总计划
                if (d.getTime() == StatUtil.strToDate(query.getEndTime()).getTime()) {
                    // 计划量
                    List<StatResultDaily> filterData = statResultDailyList.stream().filter(a -> a.getStatisticDate().equals(StatUtil.dateToSimpleStr(finalD))).collect(Collectors.toList());
                    int partPlanTotal = filterData.stream().mapToInt(s -> s.getPartPlanTotal() != null ? s.getPartPlanTotal() : 0).sum();
                    int paperPlanTotal = filterData.stream().mapToInt(s -> s.getPaperPlanTotal() != null ? s.getPaperPlanTotal() : 0).sum();
                    partWaitTotal += partPlanTotal;
                    paperWaitTotal += paperPlanTotal;
                    totalStatList.add(StatDailyR.StatPartR.builder().day(">" + StatUtil.dateToSimpleStr(d)).partNum(partPlanTotal).paperNum(paperPlanTotal).build());
                }
            }
            totalStatList.add(StatDailyR.StatPartR.builder().day("待制品总量").partNum(partWaitTotal).paperNum(paperWaitTotal).build());
            totalStatList.sort(Comparator.comparing(StatDailyR.StatPartR::getDay));
            resultList.add(StatDailyR.builder().provider("total").statList(totalStatList).build());
        }
        return resultList;
    }

    StatPartsDataSourceR toStatPartsDataSourceR(StatPartsDataSource statPartsDataSource);

    List<StatPartsDataSourceR> toStatPartsDataSourceRs(List<StatPartsDataSource> statPartsDataSourceList);

}
