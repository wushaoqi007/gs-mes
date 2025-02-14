package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.domain.converter.EnumConverter;
import com.greenstone.mes.material.domain.entity.Project;
import com.greenstone.mes.material.domain.entity.StatResultDesigner;
import com.greenstone.mes.material.domain.entity.StatResultMonth;
import com.greenstone.mes.material.domain.entity.StatResultRework;
import com.greenstone.mes.material.infrastructure.enums.NgType;
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
public interface StatMonthAssembler {

    default StatMonthQuery toStatMonthQuery(StatMonthQuery query) {
        if (StrUtil.isEmpty(query.getMonth())) {
            StatMonthQuery statMonthQuery = StatMonthQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            // 默认搜索本年12个月数据
            statMonthQuery.setMonthStart(StatUtil.firstMonthOfYear(new Date()));
            statMonthQuery.setMonthEnd(StatUtil.lastMonthOfYear(new Date()));
            return statMonthQuery;
        } else {
            return StatMonthQuery.builder().monthStart(query.getMonth()).monthEnd(query.getMonth()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
        }
    }

    default StatMonthQuery toStatYearMonthQuery(StatMonthQuery query) {
        if (StrUtil.isEmpty(query.getYear())) {
            StatMonthQuery statMonthQuery = StatMonthQuery.builder().year(StatUtil.toYearStr(new Date())).days(query.getDays()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            // 默认搜索本年12个月数据
            statMonthQuery.setMonthStart(StatUtil.firstMonthOfYear(new Date()));
            statMonthQuery.setMonthEnd(StatUtil.lastMonthOfYear(new Date()));
            return statMonthQuery;
        } else {
            StatMonthQuery statMonthQuery = StatMonthQuery.builder().year(query.getYear()).days(query.getDays()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            Date time = StatUtil.strToYear(Integer.parseInt(query.getYear()));
            statMonthQuery.setMonthStart(StatUtil.firstMonthOfYear(time));
            statMonthQuery.setMonthEnd(StatUtil.lastMonthOfYear(time));
            return statMonthQuery;
        }
    }

    default StatMonthQuery toStatYearOweQuery(StatMonthQuery query) {
        if (StrUtil.isEmpty(query.getMonth())) {
            StatMonthQuery statMonthQuery = StatMonthQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            // 默认搜索本年12个月数据
            statMonthQuery.setMonthStart(StatUtil.firstMonthOfYear(new Date()));
            statMonthQuery.setMonthEnd(StatUtil.lastMonthOfYear(new Date()));
            return statMonthQuery;
        } else {
            StatMonthQuery statMonthQuery = StatMonthQuery.builder().year(query.getYear()).days(query.getDays()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            Date time = StatUtil.monthStrToDate(query.getMonth());
            statMonthQuery.setMonthStart(StatUtil.firstMonthOfYear(time));
            statMonthQuery.setMonthEnd(StatUtil.lastMonthOfYear(time));
            return statMonthQuery;
        }
    }

    default StatMonthQuery toStatReworkQuery(StatMonthQuery query) {
        if (StrUtil.isEmpty(query.getMonth())) {
            StatMonthQuery statMonthQuery = StatMonthQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            // 默认搜索本月及上月数据
            statMonthQuery.setMonthStart(StatUtil.reduceOneMonth(new Date()));
            statMonthQuery.setMonthEnd(StatUtil.monthToStr(new Date()));
            return statMonthQuery;
        } else {
            return StatMonthQuery.builder().monthStart(StatUtil.reduceOneMonth(StatUtil.monthStrToDate(query.getMonth()))).monthEnd(query.getMonth()).projectCode(query.getProjectCode()).provider(query.getProvider()).build();
        }
    }

    default StatDailyQuery toStatReworkTypeQuery(StatMonthQuery query) {
        if (StrUtil.isEmpty(query.getMonth())) {
            StatDailyQuery statDailyQuery = StatDailyQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            // 默认搜索本月数据
            statDailyQuery.setStartTime(StatUtil.dateToSimpleStr(StatUtil.monthStart(new Date())));
            statDailyQuery.setEndTime(StatUtil.dateToSimpleStr(StatUtil.monthEnd(new Date())));
            return statDailyQuery;
        } else {
            StatDailyQuery statDailyQuery = StatDailyQuery.builder().projectCode(query.getProjectCode()).provider(query.getProvider()).build();
            Date time = StatUtil.monthStrToDate(query.getMonth());
            statDailyQuery.setStartTime(StatUtil.dateToSimpleStr(StatUtil.monthStart(time)));
            statDailyQuery.setEndTime(StatUtil.dateToSimpleStr(StatUtil.monthEnd(time)));
            return statDailyQuery;
        }
    }

    default List<StatMonthR> toMonthOweR(List<StatResultMonth> statResultMonthList, StatMonthQuery query) {
        List<StatMonthR> resultList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        // 按加工商分组
        Map<String, List<StatResultMonth>> groupByProvider = statResultMonthList.stream().collect(Collectors.groupingBy(StatResultMonth::getProvider));
        groupByProvider.forEach((provider, list) -> {
            List<StatMonthR.StatR> statList = new ArrayList<>();
            for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
                int finalD = d;
                List<StatResultMonth> filterData = list.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
                int paperPlanNum = filterData.stream().mapToInt(s -> s.getPaperPlanNum() != null ? s.getPaperPlanNum() : 0).sum();
                int paperOverdueNum = filterData.stream().mapToInt(s -> s.getPaperOverdueNum() != null ? s.getPaperOverdueNum() : 0).sum();
                int paperOverdueThreeDaysNum = filterData.stream().mapToInt(s -> s.getPaperOverdueThreeDaysNum() != null ? s.getPaperOverdueThreeDaysNum() : 0).sum();
                String overdueRate = paperPlanNum == 0 ? "0%" : decimalFormat.format((double) paperOverdueNum / (double) paperPlanNum);
                String overdueThreeDaysRate = paperPlanNum == 0 ? "0%" : decimalFormat.format((double) paperOverdueThreeDaysNum / (double) paperPlanNum);
                statList.add(StatMonthR.StatR.builder().month(String.valueOf(finalD))
                        .paperNum(paperPlanNum).overdueNum(paperOverdueNum)
                        .overdueThreeDaysNum(paperOverdueThreeDaysNum)
                        .overdueRate(overdueRate).overdueThreeDaysRate(overdueThreeDaysRate).build());
            }
            resultList.add(StatMonthR.builder().provider(provider).statList(statList).build());
        });
        // 汇总
        if (StrUtil.isEmpty(query.getProvider())) {
            List<StatMonthR.StatR> totalStatList = new ArrayList<>();
            for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
                int finalD = d;
                List<StatResultMonth> filterData = statResultMonthList.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
                int paperPlanNum = filterData.stream().mapToInt(s -> s.getPaperPlanNum() != null ? s.getPaperPlanNum() : 0).sum();
                int paperOverdueNum = filterData.stream().mapToInt(s -> s.getPaperOverdueNum() != null ? s.getPaperOverdueNum() : 0).sum();
                int paperOverdueThreeDaysNum = filterData.stream().mapToInt(s -> s.getPaperOverdueThreeDaysNum() != null ? s.getPaperOverdueThreeDaysNum() : 0).sum();
                String overdueRate = paperPlanNum == 0 ? "0%" : decimalFormat.format((double) paperOverdueNum / (double) paperPlanNum);
                String overdueThreeDaysRate = paperPlanNum == 0 ? "0%" : decimalFormat.format((double) paperOverdueThreeDaysNum / (double) paperPlanNum);
                totalStatList.add(StatMonthR.StatR.builder().month(String.valueOf(finalD))
                        .paperNum(paperPlanNum).overdueNum(paperOverdueNum)
                        .overdueThreeDaysNum(paperOverdueThreeDaysNum)
                        .overdueRate(overdueRate).overdueThreeDaysRate(overdueThreeDaysRate).build());
            }
            resultList.add(StatMonthR.builder().provider("total").statList(totalStatList).build());
        }
        return resultList;
    }

    default List<StatMonthReworkR> toMonthReworkR(List<StatResultMonth> statResultMonthList, StatMonthQuery query) {
        List<StatMonthReworkR> resultList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        // 按加工商分组
        Map<String, List<StatResultMonth>> groupByProvider = statResultMonthList.stream().collect(Collectors.groupingBy(StatResultMonth::getProvider));
        groupByProvider.forEach((provider, list) -> {
            List<StatMonthReworkR.StatR> statList = new ArrayList<>();
            for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
                int finalD = d;
                List<StatResultMonth> filterData = list.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
                int paperCheckNum = filterData.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
                int paperReworkNum = filterData.stream().mapToInt(s -> s.getPaperReworkNum() != null ? s.getPaperReworkNum() : 0).sum();
                String reworkRate = paperCheckNum == 0 ? "0%" : decimalFormat.format((double) paperReworkNum / (double) paperCheckNum);

                statList.add(StatMonthReworkR.StatR.builder().month(String.valueOf(finalD))
                        .reworkRate(reworkRate).build());
            }
            // 该加工商所有月的平均
            int paperCheckTotal = list.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
            int paperReworkTotal = list.stream().mapToInt(s -> s.getPaperReworkNum() != null ? s.getPaperReworkNum() : 0).sum();
            String reworkRateAvg = paperCheckTotal == 0 ? "0%" : decimalFormat.format((double) paperReworkTotal / (double) paperCheckTotal);

            statList.add(StatMonthReworkR.StatR.builder().month("AVG")
                    .reworkRate(reworkRateAvg).build());
            resultList.add(StatMonthReworkR.builder().provider(provider).statList(statList).build());
        });
        // 汇总
        if (StrUtil.isEmpty(query.getProvider())) {
            List<StatMonthReworkR.StatR> totalStatList = new ArrayList<>();
            for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
                int finalD = d;
                List<StatResultMonth> filterData = statResultMonthList.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
                int paperCheckNum = filterData.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
                int paperReworkNum = filterData.stream().mapToInt(s -> s.getPaperReworkNum() != null ? s.getPaperReworkNum() : 0).sum();
                String reworkRate = paperCheckNum == 0 ? "0%" : decimalFormat.format((double) paperReworkNum / (double) paperCheckNum);

                totalStatList.add(StatMonthReworkR.StatR.builder().month(String.valueOf(finalD))
                        .reworkRate(reworkRate).build());
            }
            // 该加工商所有月的平均
            int paperCheckTotal = statResultMonthList.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
            int paperReworkTotal = statResultMonthList.stream().mapToInt(s -> s.getPaperReworkNum() != null ? s.getPaperReworkNum() : 0).sum();
            String reworkRateAvg = paperCheckTotal == 0 ? "0%" : decimalFormat.format((double) paperReworkTotal / (double) paperCheckTotal);

            totalStatList.add(StatMonthReworkR.StatR.builder().month("AVG")
                    .reworkRate(reworkRateAvg).build());
            resultList.add(StatMonthReworkR.builder().provider("AVG").statList(totalStatList).build());
        }
        return resultList;
    }

    default List<StatMonthCheckR> toMonthCheckR(List<StatResultMonth> statResultMonthList, StatMonthQuery query) {
        List<StatMonthCheckR> resultList = new ArrayList<>();
        // 按加工商分组
        Map<String, List<StatResultMonth>> groupByProvider = statResultMonthList.stream().collect(Collectors.groupingBy(StatResultMonth::getProvider));
        groupByProvider.forEach((provider, list) -> {
            List<StatMonthCheckR.StatR> statList = new ArrayList<>();
            for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
                int finalD = d;
                List<StatResultMonth> filterData = list.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
                int paperCheckNum = filterData.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();

                statList.add(StatMonthCheckR.StatR.builder().month(String.valueOf(finalD))
                        .checkNum(paperCheckNum).build());
            }
            // 该加工商所有月的平均
            int paperCheckTotal = list.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
            statList.add(StatMonthCheckR.StatR.builder().month("AVG")
                    .checkNum(paperCheckTotal / 12).build());
            resultList.add(StatMonthCheckR.builder().provider(provider).statList(statList).build());
        });
        // 汇总
        if (StrUtil.isEmpty(query.getProvider()) && statResultMonthList.size() > 0) {
            List<StatMonthCheckR.StatR> totalStatList = new ArrayList<>();
            int paperCheckTotal = 0;
            for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
                int finalD = d;
                List<StatResultMonth> filterData = statResultMonthList.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
                int paperCheckNum = filterData.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
                paperCheckTotal += paperCheckNum / groupByProvider.size();
                totalStatList.add(StatMonthCheckR.StatR.builder().month(String.valueOf(finalD))
                        .checkNum(paperCheckNum / groupByProvider.size()).build());
            }
            // 所有月的平均
            totalStatList.add(StatMonthCheckR.StatR.builder().month("AVG")
                    .checkNum(paperCheckTotal / 12).build());
            resultList.add(StatMonthCheckR.builder().provider("AVG").statList(totalStatList).build());
        }
        return resultList;
    }

    default List<StatMonthPlanR> toMonthPlanROfImport(List<StatResultDesigner> statResultDesignerList, StatMonthQuery query) {
        List<StatMonthPlanR> resultList = new ArrayList<>();
        for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
            int finalD = d;
            List<StatResultDesigner> filterData = statResultDesignerList.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
            int partPlanNum = filterData.stream().mapToInt(s -> s.getPartTotal() != null ? s.getPartTotal() : 0).sum();
            int paperPlanNum = filterData.stream().mapToInt(s -> s.getPaperTotal() != null ? s.getPaperTotal() : 0).sum();

            resultList.add(StatMonthPlanR.builder().month(String.valueOf(finalD))
                    .partNum(partPlanNum).paperNum(paperPlanNum).build());
        }
        // 汇总
        int partPlanTotal = statResultDesignerList.stream().mapToInt(s -> s.getPartTotal() != null ? s.getPartTotal() : 0).sum();
        int paperPlanTotal = statResultDesignerList.stream().mapToInt(s -> s.getPaperTotal() != null ? s.getPaperTotal() : 0).sum();

        resultList.add(StatMonthPlanR.builder().month("total")
                .partNum(partPlanTotal).paperNum(paperPlanTotal).build());
        return resultList;
    }

    default List<StatMonthSpecialR> toMonthSpecialR(List<StatResultDesigner> statResultDesignerList, StatMonthQuery query) {
        List<StatMonthSpecialR> resultList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
            int finalD = d;
            List<StatResultDesigner> filterData = statResultDesignerList.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
            int total = filterData.stream().mapToInt(s -> s.getPaperTotal() != null ? s.getPaperTotal() : 0).sum();
            int updateNum = filterData.stream().mapToInt(s -> s.getPaperUpdateTotal() != null ? s.getPaperUpdateTotal() : 0).sum();
            int urgentNum = filterData.stream().mapToInt(s -> s.getPaperUrgentTotal() != null ? s.getPaperUrgentTotal() : 0).sum();
            int repairNum = filterData.stream().mapToInt(s -> s.getPaperRepairTotal() != null ? s.getPaperRepairTotal() : 0).sum();
            int specialTotal = updateNum + urgentNum + repairNum;
            String specialRate = total == 0 ? "0%" : decimalFormat.format((double) specialTotal / (double) total);

            resultList.add(StatMonthSpecialR.builder().month(String.valueOf(finalD))
                    .total(total).updateNum(updateNum).repairNum(repairNum).urgentNum(urgentNum).specialRate(specialRate).specialTotal(specialTotal).build());
        }
        // 汇总
        int total = statResultDesignerList.stream().mapToInt(s -> s.getPaperTotal() != null ? s.getPaperTotal() : 0).sum();
        int updateNum = statResultDesignerList.stream().mapToInt(s -> s.getPaperUpdateTotal() != null ? s.getPaperUpdateTotal() : 0).sum();
        int urgentNum = statResultDesignerList.stream().mapToInt(s -> s.getPaperUrgentTotal() != null ? s.getPaperUrgentTotal() : 0).sum();
        int repairNum = statResultDesignerList.stream().mapToInt(s -> s.getPaperRepairTotal() != null ? s.getPaperRepairTotal() : 0).sum();
        int specialTotal = updateNum + urgentNum + repairNum;
        String specialRate = total == 0 ? "0%" : decimalFormat.format((double) specialTotal / (double) total);

        resultList.add(StatMonthSpecialR.builder().month("total")
                .total(total).updateNum(updateNum).repairNum(repairNum).urgentNum(urgentNum).specialRate(specialRate).specialTotal(specialTotal).build());
        return resultList;
    }

    default List<StatMonthDesignerOverdueR> toMonthDesignerOverdueR(List<StatResultDesigner> statResultDesignerList, List<Project> projectList, StatMonthQuery query) {
        List<StatMonthDesignerOverdueR> resultList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
            int finalD = d;
            List<StatResultDesigner> filterData = statResultDesignerList.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
            List<StatResultDesigner> overdueData = filterData.stream().filter(a -> (query.getDays() == null && a.getOverdue()) || (query.getDays() != null && a.getOverdueDays() >= query.getDays())).collect(Collectors.toList());
            List<Project> monthProjects = projectList.stream().filter(a -> StatUtil.monthToStr(a.getDesignDeadline()).equals(String.valueOf(finalD))).collect(Collectors.toList());

            int actualNum = CollUtil.isNotEmpty(filterData) ? filterData.size() : 0;
            int overdueNum = CollUtil.isNotEmpty(overdueData) ? overdueData.size() : 0;
            int planNum = CollUtil.isNotEmpty(monthProjects) ? monthProjects.size() : 0;
            String overdueRate = planNum == 0 ? "0%" : decimalFormat.format((double) overdueNum / (double) planNum);
            resultList.add(StatMonthDesignerOverdueR.builder().month(String.valueOf(finalD)).planNum(planNum)
                    .actualNum(actualNum).overdueNum(overdueNum).overdueRate(overdueRate).build());
        }
        return resultList;
    }

    default List<StatMonthPlanR> toMonthPlanR(List<StatResultMonth> statResultMonthList, StatMonthQuery query) {
        List<StatMonthPlanR> resultList = new ArrayList<>();
        for (int d = Integer.parseInt(query.getMonthStart()); d <= Integer.parseInt(query.getMonthEnd()); d++) {
            int finalD = d;
            List<StatResultMonth> filterData = statResultMonthList.stream().filter(a -> a.getStatisticMonth().equals(String.valueOf(finalD))).collect(Collectors.toList());
            int partPlanNum = filterData.stream().mapToInt(s -> s.getPartPlanNum() != null ? s.getPartPlanNum() : 0).sum();
            int paperPlanNum = filterData.stream().mapToInt(s -> s.getPaperPlanNum() != null ? s.getPaperPlanNum() : 0).sum();

            resultList.add(StatMonthPlanR.builder().month(String.valueOf(finalD))
                    .partNum(partPlanNum).paperNum(paperPlanNum).build());
        }
        // 该加工商所有月的汇总
        int partPlanTotal = statResultMonthList.stream().mapToInt(s -> s.getPartPlanNum() != null ? s.getPartPlanNum() : 0).sum();
        int paperPlanTotal = statResultMonthList.stream().mapToInt(s -> s.getPaperPlanNum() != null ? s.getPaperPlanNum() : 0).sum();

        resultList.add(StatMonthPlanR.builder().month("total")
                .partNum(partPlanTotal).paperNum(paperPlanTotal).build());
        return resultList;
    }

    default List<StatReworkTypeR.ReworkTypeR> toReworkTypeList(List<StatResultRework> statResultReworkList, String provider) {
        List<StatReworkTypeR.ReworkTypeR> reworkTypeRList = new ArrayList<>();
        if (StrUtil.isNotEmpty(provider)) {
            statResultReworkList = statResultReworkList.stream().filter(a -> a.getProvider().equals(provider)).collect(Collectors.toList());
        }
        List<NgType> parentNgType = NgType.getParentNgType();
        for (NgType ngType : parentNgType) {
            List<NgType> subNgType = NgType.getSubNgType(ngType.getType());
            List<StatReworkTypeR.ReworkTypeR.SubNgTypeR> subNgTypeRList = new ArrayList<>();
            for (NgType subType : subNgType) {
                List<StatResultRework> subNgTypeReworkData = statResultReworkList.stream().filter(a -> a.getSubNgType().equals(subType.getType())).collect(Collectors.toList());
                int total = 0;
                if (CollUtil.isNotEmpty(subNgTypeReworkData)) {
                    total = subNgTypeReworkData.stream().mapToInt(s -> s.getTotal() != null ? s.getTotal() : 0).sum();
                }
                subNgTypeRList.add(StatReworkTypeR.ReworkTypeR.SubNgTypeR.builder().subNgType(subType.getType()).number(total).build());
            }
            reworkTypeRList.add(StatReworkTypeR.ReworkTypeR.builder().ngType(ngType.getType()).subNgTypeRList(subNgTypeRList).build());
        }
        return reworkTypeRList;
    }

    default List<StatReworkTypeR> toReworkTypeR(List<StatResultMonth> statResultMonthList, List<StatResultRework> statResultReworkList, StatMonthQuery query) {
        List<StatReworkTypeR> resultList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        // 按加工商分组
        Map<String, List<StatResultMonth>> groupByProvider = statResultMonthList.stream().collect(Collectors.groupingBy(StatResultMonth::getProvider));
        groupByProvider.forEach((provider, list) -> {
            List<StatReworkTypeR.StatR> statList = new ArrayList<>();
            // 上个月数据
            List<StatResultMonth> lastMonthData = list.stream().filter(a -> a.getStatisticMonth().equals(query.getMonthStart())).collect(Collectors.toList());
            // 当月数据
            List<StatResultMonth> currentMonthData = list.stream().filter(a -> a.getStatisticMonth().equals(query.getMonthEnd())).collect(Collectors.toList());

            int paperCheckNumLast = lastMonthData.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
            int paperReworkNumLast = lastMonthData.stream().mapToInt(s -> s.getPaperReworkNum() != null ? s.getPaperReworkNum() : 0).sum();
            String reworkRateLast = paperCheckNumLast == 0 ? "0%" : decimalFormat.format((double) paperReworkNumLast / (double) paperCheckNumLast);

            int paperCheckNum = currentMonthData.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
            int paperReworkNum = currentMonthData.stream().mapToInt(s -> s.getPaperReworkNum() != null ? s.getPaperReworkNum() : 0).sum();
            String reworkRate = paperCheckNum == 0 ? "0%" : decimalFormat.format((double) paperReworkNum / (double) paperCheckNum);

            statList.add(StatReworkTypeR.StatR.builder().reworkTypeList(toReworkTypeList(statResultReworkList, provider))
                    .lastTimeReworkRate(reworkRateLast).reworkRate(reworkRate)
                    .checkNum(paperCheckNum).reworkNum(paperReworkNum).build());
            resultList.add(StatReworkTypeR.builder().provider(provider).statList(statList).build());
        });
        // 汇总
        if (StrUtil.isEmpty(query.getProvider())) {
            List<StatReworkTypeR.StatR> totalStatList = new ArrayList<>();
            // 上个月数据
            List<StatResultMonth> lastMonthData = statResultMonthList.stream().filter(a -> a.getStatisticMonth().equals(query.getMonthStart())).collect(Collectors.toList());
            // 当月数据
            List<StatResultMonth> currentMonthData = statResultMonthList.stream().filter(a -> a.getStatisticMonth().equals(query.getMonthEnd())).collect(Collectors.toList());

            int paperCheckNumLast = lastMonthData.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
            int paperReworkNumLast = lastMonthData.stream().mapToInt(s -> s.getPaperReworkNum() != null ? s.getPaperReworkNum() : 0).sum();
            String reworkRateLast = paperCheckNumLast == 0 ? "0%" : decimalFormat.format((double) paperReworkNumLast / (double) paperCheckNumLast);

            int paperCheckNum = currentMonthData.stream().mapToInt(s -> s.getPaperCheckNum() != null ? s.getPaperCheckNum() : 0).sum();
            int paperReworkNum = currentMonthData.stream().mapToInt(s -> s.getPaperReworkNum() != null ? s.getPaperReworkNum() : 0).sum();
            String reworkRate = paperCheckNum == 0 ? "0%" : decimalFormat.format((double) paperReworkNum / (double) paperCheckNum);

            totalStatList.add(StatReworkTypeR.StatR.builder().reworkTypeList(toReworkTypeList(statResultReworkList, ""))
                    .lastTimeReworkRate(reworkRateLast).reworkRate(reworkRate)
                    .checkNum(paperCheckNum).reworkNum(paperReworkNum).build());
            resultList.add(StatReworkTypeR.builder().provider("平均不良率").statList(totalStatList).build());
        }
        return resultList;
    }

}
