package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.application.dto.StatQuery;
import com.greenstone.mes.material.application.dto.result.StatChartBarR;
import com.greenstone.mes.material.application.dto.result.StatDailyFinishR;
import com.greenstone.mes.material.application.dto.result.StatDailyR;
import com.greenstone.mes.material.application.dto.result.StatPartsDataSourceR;
import com.greenstone.mes.material.application.assembler.StatDailyAssembler;
import com.greenstone.mes.material.application.assembler.StatDailyChartAssembler;
import com.greenstone.mes.material.domain.entity.*;
import com.greenstone.mes.material.dto.cmd.StatDailyCmd;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.util.StatUtil;
import com.greenstone.mes.material.domain.repository.StatResultDailyRepository;
import com.greenstone.mes.material.domain.repository.StatResultReworkRepository;
import com.greenstone.mes.material.domain.service.StatResultDailyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-02-23-8:49
 */
@Slf4j
@Service
public class StatResultDailyServiceImpl implements StatResultDailyService {


    private final StatResultDailyRepository statResultDailyRepository;
    private final StatResultReworkRepository statResultReworkRepository;
    private final StatDailyAssembler statDailyAssembler;
    private final StatDailyChartAssembler statDailyChartAssembler;


    public StatResultDailyServiceImpl(StatResultDailyRepository statResultDailyRepository, StatResultReworkRepository statResultReworkRepository,
                                      StatDailyAssembler statDailyAssembler, StatDailyChartAssembler statDailyChartAssembler) {
        this.statResultDailyRepository = statResultDailyRepository;
        this.statResultReworkRepository = statResultReworkRepository;
        this.statDailyAssembler = statDailyAssembler;
        this.statDailyChartAssembler = statDailyChartAssembler;
    }

    @Override
    public void dailyStatistics(StatDailyCmd statDailyCmd) {
        StatQuery query = statDailyAssembler.toStatQuery(statDailyCmd);
        String todayStr = StatUtil.dateToSimpleStr(getStatisticDate(query));
        log.info("统计日期：{}", todayStr);
        // 查询日统计数据源
        List<StatDataDaily> statDataFromPartStageList = statResultDailyRepository.selectStatDataDaily(getStatDailyQuery(query));
        // 日统计：计划、交货、欠货、超期的数据
        List<StatResultDaily> statResultDailyList = statDaily(statDataFromPartStageList, todayStr);

        // 查询当天出入库数据
        List<StatDataDaily> statDataFromStockRecordList = statResultDailyRepository.statDataFromStockRecord(getTodayQuery(query));
        // 检验数据统计：不良、送检
        statCheck(statResultDailyList, statDataFromStockRecordList, todayStr);

        // 查询当天质检记录数据
        List<StatDataCheckRecord> statDataCheckRecordList = statResultDailyRepository.statDataFromCheckRecord(getTodayQuery(query));
        // 不良类型数据统计
        List<StatResultRework> statResultReworkList = statReworkType(statDataCheckRecordList, todayStr);

        // 保存每日统计
        statResultDailyRepository.saveStatResultDaily(statResultDailyList, todayStr);
        // 保存不良类型统计
        statResultReworkRepository.saveStatResultRework(statResultReworkList, todayStr);
    }

    @Override
    public List<String> listOweProvider(StatQuery query) {
        // 查询
        List<StatResultDaily> statResultDailyList = statResultDailyRepository.selectStatResultList(statDailyAssembler.toStatDailyQuery(query));
        // 按加工商分组
        Map<String, List<StatResultDaily>> groupByProvider = statResultDailyList.stream().collect(Collectors.groupingBy(x -> StrUtil.isEmpty(x.getProvider()) ? "其他" : x.getProvider()));
        return new ArrayList<>(groupByProvider.keySet());
    }

    @Override
    public List<StatDailyR> selectDailyOwe(StatQuery query) {
        // 校验
        validate(query);
        // 查询
        StatDailyQuery statDailyQuery = statDailyAssembler.toStatDailyQuery(query);
        List<StatResultDaily> statResultDailyList = statResultDailyRepository.selectStatResultList(statDailyQuery);
        return statDailyAssembler.toDailyOweR(statResultDailyList, statDailyQuery);
    }

    @Override
    public StatChartBarR selectDailyOweChart(StatQuery query) {
        List<StatDailyR> statDailyRList = selectDailyOwe(query);
        return statDailyChartAssembler.toOweStatChartR(statDailyRList);
    }

    @Override
    public List<StatDailyR> selectDailyOweDistribution(StatQuery query) {
        // 校验
        validate(query);
        // 查询
        StatDailyQuery statDailyQuery = statDailyAssembler.toStatDailyQuery(query);
        List<StatResultDaily> statResultDailyList = statResultDailyRepository.selectStatResultList(statDailyQuery);
        return statDailyAssembler.toDailyOweDistributionR(statResultDailyList, statDailyQuery);
    }

    @Override
    public StatChartBarR selectDailyOweDistributionChart(StatQuery query) {
        List<StatDailyR> statDailyRList = selectDailyOweDistribution(query);
        return statDailyChartAssembler.toOweDistributionStatChartR(statDailyRList);
    }

    @Override
    public List<StatDailyR> selectDailyDelivery(StatQuery query) {
        // 校验
        validate(query);
        // 查询
        StatDailyQuery statDailyQuery = statDailyAssembler.toStatDailyQuery(query);
        List<StatResultDaily> statResultDailyList = statResultDailyRepository.selectStatResultList(statDailyQuery);
        return statDailyAssembler.toDailyDeliveryR(statResultDailyList, statDailyQuery);
    }

    @Override
    public StatChartBarR selectDailyDeliveryChart(StatQuery query) {
        List<StatDailyR> statDailyRList = selectDailyDelivery(query);
        return statDailyChartAssembler.toDailyDeliveryChartR(statDailyRList);
    }

    @Override
    public List<StatDailyFinishR> selectDailyFinish(StatQuery query) {
        // 校验
        validate(query);
        // 查询
        StatDailyQuery statDailyQuery = statDailyAssembler.toStatMonthQuery(query);
        List<StatResultDaily> statResultDailyList = statResultDailyRepository.selectStatResultList(statDailyQuery);
        return statDailyAssembler.toDailyFinishR(statResultDailyList, statDailyQuery);
    }

    @Override
    public StatChartBarR selectDailyFinishChart(StatQuery query) {
        // 默认查询昨天的统计结果
        if (StrUtil.isEmpty(query.getStartTime()) || StrUtil.isEmpty(query.getEndTime())) {
            String yesterday = StatUtil.dateToCompleteStr(StatUtil.reduceOneDay(new Date()));
            query.setStartTime(yesterday);
            query.setEndTime(yesterday);
        }
        List<StatDailyFinishR> statDailyFinishRList = selectDailyFinish(query);
        return statDailyChartAssembler.toDailyFinishChartR(statDailyFinishRList);
    }

    @Override
    public StatChartBarR selectDailyFinishDistributionChart(StatQuery query) {
        // 默认搜索当前周数据
        if (StrUtil.isEmpty(query.getStartTime()) || StrUtil.isEmpty(query.getEndTime())) {
            Date[] currentWeek = StatUtil.getCurrentWeek();
            query.setStartTime(StatUtil.dateToSimpleStr(currentWeek[0]));
            query.setEndTime(StatUtil.dateToSimpleStr(StatUtil.reduceOneDay(currentWeek[1])));
        }
        List<StatDailyFinishR> statDailyFinishRList = selectDailyFinish(query);
        return statDailyChartAssembler.toDailyFinishDistributionChartR(statDailyFinishRList);
    }

    @Override
    public List<StatDailyR> selectDailyPlan(StatQuery query) {
        // 查询
        StatDailyQuery statDailyQuery = statDailyAssembler.toStatPlanQuery(query);
        List<StatResultDaily> statResultDailyList = statResultDailyRepository.selectStatResultList(statDailyQuery);
        return statDailyAssembler.toDailyPlanR(statResultDailyList, statDailyQuery);
    }

    @Override
    public StatChartBarR selectDailyPlanChart(StatQuery query) {
        List<StatDailyR> statDailyRList = selectDailyPlan(query);
        return statDailyChartAssembler.toDailyPlanChartR(statDailyRList);
    }

    @Override
    public List<StatPartsDataSourceR> selectDailyOweSource(StatQuery query) {
        // 查询
        List<StatPartsDataSource> statPartsDataSourceList = statResultDailyRepository.selectDailyOweSource(statDailyAssembler.toOweSourceQuery(query));
        return statDailyAssembler.toStatPartsDataSourceRs(statPartsDataSourceList);
    }

    @Override
    public List<StatPartsDataSourceR> selectDailyFinishSource(StatQuery query) {
        // 查询
        List<StatPartsDataSource> statPartsDataSourceList = statResultDailyRepository.selectDailyFinishSource(statDailyAssembler.toFinishSourceQuery(query));
        return statDailyAssembler.toStatPartsDataSourceRs(statPartsDataSourceList);
    }

    @Override
    public List<StatPartsDataSourceR> selectDailyDeliverySource(StatQuery query) {
        // 校验
        validate(query);
        // 查询
        StatDailyQuery statDailyQuery = statDailyAssembler.toStatDailyQuery(query);
        List<StatPartsDataSource> statPartsDataSourceList = statResultDailyRepository.selectDailyDeliverySource(statDailyQuery);
        return statDailyAssembler.toStatPartsDataSourceRs(statPartsDataSourceList);
    }

    @Override
    public List<StatPartsDataSourceR> selectDailyPlanSource(StatQuery query) {
        // 查询
        List<StatPartsDataSource> statPartsDataSourceList = statResultDailyRepository.selectDailyPlanSource(statDailyAssembler.toPlanSourceQuery(query));
        return statDailyAssembler.toStatPartsDataSourceRs(statPartsDataSourceList);
    }


    private void validate(StatQuery query) {
        // 校验：日期搜索最多选31天
        boolean isInRange = StatUtil.checkDateRange(query.getStartTime(), query.getEndTime(), 30);
        if (!isInRange) {
            throw new ServiceException(BizError.E50001);
        }
    }


    /**
     * 统计当天不良类型
     *
     * @param statDataCheckRecordList 数据来源
     * @return 统计结果
     */
    private List<StatResultRework> statReworkType(List<StatDataCheckRecord> statDataCheckRecordList, String todayStr) {
        log.info("开始统计当天不良类型,数据量：{}", statDataCheckRecordList.size());
        List<StatResultRework> result = new ArrayList<>();
        // 按加工商、项目代码分组统计
        Map<String, List<StatDataCheckRecord>> groupByProvider = statDataCheckRecordList.stream().collect(Collectors.groupingBy(x -> StrUtil.isEmpty(x.getProvider()) ? "其他" : x.getProvider()));
        groupByProvider.forEach((provider, list1) -> {
            Map<String, List<StatDataCheckRecord>> groupByProjectCode = list1.stream().collect(Collectors.groupingBy(StatDataCheckRecord::getProjectCode));
            groupByProjectCode.forEach((projectCode, list2) -> {
                Map<String, Integer> sumByType = list2.stream().collect(Collectors.groupingBy(a -> a.getNgType() + "_" + a.getSubNgType(), Collectors.summingInt(StatDataCheckRecord::getPaperNumber)));
                sumByType.forEach((type, total) -> {
                    String[] types = type.split("_");
                    result.add(StatResultRework.builder().statisticDate(todayStr).provider(provider).projectCode(projectCode)
                            .ngType(types[0]).subNgType(types[1]).total(total).build());
                });
            });
        });
        return result;
    }

    /**
     * 统计检验数据
     *
     * @param statResultDailyList         统计结果
     * @param statDataFromStockRecordList 数据来源
     */
    private void statCheck(List<StatResultDaily> statResultDailyList, List<StatDataDaily> statDataFromStockRecordList, String todayStr) {
        log.info("开始统计检验的数据：当天的不良、送检,数据量：{}", statDataFromStockRecordList.size());
        // 按加工商、项目代码分组统计
        Map<String, List<StatDataDaily>> groupByProvider = statDataFromStockRecordList.stream().collect(Collectors.groupingBy(x -> StrUtil.isEmpty(x.getProvider()) ? "其他" : x.getProvider()));
        groupByProvider.forEach((provider, list1) -> {
            Map<String, List<StatDataDaily>> groupByProjectCode = list1.stream().collect(Collectors.groupingBy(StatDataDaily::getProjectCode));
            groupByProjectCode.forEach((projectCode, list2) -> {
                // 取出（stage_operation=3，4，5）的合格、表处、返工：入库量=送检量
                List<StatDataDaily> checkedList = list2.stream().filter(a -> a.getStageOperation() == BillOperation.CHECKED_OK_CREATE.getId() ||
                        a.getStageOperation() == BillOperation.CHECKED_TREAT_CREATE.getId() || a.getStageOperation() == BillOperation.CHECKED_NG_CREATE.getId()).collect(Collectors.toList());
                int[] checkStat = statReceiveByMaterial(checkedList);
                int partCheckNum = checkStat[0];
                int paperCheckNum = checkStat[1];

                // 取出（stage_operation=5）的返工：入库量=不良量
                List<StatDataDaily> reworkList = list2.stream().filter(a -> a.getStageOperation() == BillOperation.CHECKED_NG_CREATE.getId()).collect(Collectors.toList());
                int[] reworkStat = statReceiveByMaterial(reworkList);
                int partReworkNum = reworkStat[0];
                int paperReworkNum = reworkStat[1];

                Optional<StatResultDaily> optional = statResultDailyList.stream().filter(a -> provider.equals(a.getProvider())
                        && projectCode.equals(a.getProjectCode()) && todayStr.equals(a.getStatisticDate())).findFirst();
                if (optional.isPresent()) {
                    StatResultDaily statResultDaily = optional.get();
                    statResultDaily.setPartCheckNum(partCheckNum);
                    statResultDaily.setPaperCheckNum(paperCheckNum);
                    statResultDaily.setPartReworkNum(partReworkNum);
                    statResultDaily.setPaperReworkNum(paperReworkNum);
                } else {
                    statResultDailyList.add(StatResultDaily.builder().provider(provider).projectCode(projectCode)
                            .partCheckNum(partCheckNum).paperCheckNum(paperCheckNum)
                            .partReworkNum(partReworkNum).paperReworkNum(paperReworkNum)
                            .statisticDate(todayStr).build());
                }
            });
        });

    }

    public List<StatResultDaily> statDaily(List<StatDataDaily> statDataDailyList, String todayStr) {
        List<StatResultDaily> statResultDailyList = new ArrayList<>();
        long todayOffWorkTime = StatUtil.todayOffWorkTime(todayStr);
        long todayBeginTime = StatUtil.todayBeginTime(todayStr);
        // 按加工商、项目代码分组统计
        Map<String, List<StatDataDaily>> groupByProvider = statDataDailyList.stream().collect(Collectors.groupingBy(x -> StrUtil.isEmpty(x.getProvider()) ? "其他" : x.getProvider()));
        groupByProvider.forEach((provider, list1) -> {
            Map<String, List<StatDataDaily>> groupByProjectCode = list1.stream().collect(Collectors.groupingBy(StatDataDaily::getProjectCode));
            groupByProjectCode.forEach((projectCode, list2) -> {
                // 计划：总计划、日计划
                // 之后的总计划:今天及之后的计划
                // 日计划：当天计划
                // 加工纳期>=当天
                List<StatDataDaily> processAfterTodayList = list2.stream().filter(a -> a.getProcessingTime() != null &&
                        a.getProcessingTime().getTime() / 1000 >= todayBeginTime).collect(Collectors.toList());
                // 之后的总计划:截至到今天的计划
                int[] planAfterTotalStat = statPlanByMaterial(processAfterTodayList);
                int partPlanAfterTotal = planAfterTotalStat[0];
                int paperPlanAfterTotal = planAfterTotalStat[1];
                // 加工纳期==当天
                List<StatDataDaily> processInTodayList = processAfterTodayList.stream().filter(a -> a.getProcessingTime() != null &&
                        StatUtil.inToday(a.getProcessingTime(), todayStr)).collect(Collectors.toList());
                // 日计划
                int[] planNumStat = statPlanByMaterial(processInTodayList);
                int partPlanNum = planNumStat[0];
                int paperPlanNum = planNumStat[1];

                // 交货：总交货、日交货
                // 当天实际总交货：加工纳期当天的实际交货
                // 日交货：当天的交货量
                // 加工纳期==当天，收货入库时间<=当天
                List<StatDataDaily> receiveList = list2.stream().filter(a -> a.getProcessingTime() != null && a.getInStockTime() != null &&
                        StatUtil.inToday(a.getProcessingTime(), todayStr) &&
                        a.getInStockTime().getTime() / 1000 <= todayOffWorkTime).collect(Collectors.toList());
                // 当天实际总交货：加工纳期当天的实际交货
                int[] receiveActualStat = statReceiveByMaterial(receiveList);
                int partDeliveryActualTotal = receiveActualStat[0];
                int paperDeliveryActualTotal = receiveActualStat[1];
                // 收货入库时间==当天
                List<StatDataDaily> receiveInTodayList = list2.stream().filter(a -> a.getInStockTime() != null &&
                        StatUtil.inToday(a.getInStockTime(), todayStr)).collect(Collectors.toList());
                // 日交货：当天的交货量
                int[] receiveNumStat = statReceiveByMaterial(receiveInTodayList);
                int partDeliveryNum = receiveNumStat[0];
                int paperDeliveryNum = receiveNumStat[1];

                // 欠货：总欠货、日欠货
                // 总欠货=当天之前的总计划-当天之前的总交货
                // 日欠货=当天计划-当天实际总交货
                // 加工纳期<=当天
                List<StatDataDaily> processBeforeTodayList = list2.stream().filter(a -> a.getProcessingTime() != null &&
                        a.getProcessingTime().getTime() / 1000 <= todayBeginTime).collect(Collectors.toList());
                // 之前的总计划:截至到今天的计划
                int[] planBeforeTotalStat = statPlanByMaterial(processBeforeTodayList);
                int partPlanBeforeTotal = planBeforeTotalStat[0];
                int paperPlanBeforeTotal = planBeforeTotalStat[1];
                // 加工纳期<=当天，收货入库时间<=当天
                List<StatDataDaily> processAndReceiveBeforeTodayList = processBeforeTodayList.stream().filter(a -> a.getInStockTime() != null &&
                        a.getInStockTime().getTime() / 1000 <= todayOffWorkTime).collect(Collectors.toList());
                // 当天之前的总交货：用于计算欠货
                int[] receiveTotalStat = statReceiveByMaterial(processAndReceiveBeforeTodayList);
                int partDeliveryBeforeTotal = receiveTotalStat[0];
                int paperDeliveryBeforeTotal = receiveTotalStat[1];
                // 总欠货=当天之前的总计划-当天之前的总交货
                int partOweTotal = partPlanBeforeTotal - partDeliveryBeforeTotal;
                int paperOweTotal = paperPlanBeforeTotal - paperDeliveryBeforeTotal;
                // 日欠货=当天计划-当天实际总交货
                int partOweNum = partPlanNum - partDeliveryActualTotal;
                int paperOweNum = paperPlanNum - paperDeliveryActualTotal;

                // 超期
                // 超期3天的=计划-实际交货
                List<StatDataDaily> overDueThreeDaysList = list2.stream().filter(a -> a.getProcessingTime() != null
                        && ((int) ((StatUtil.strToDate(todayStr).getTime() - a.getProcessingTime().getTime()) / (1000 * 3600 * 24)) == 3)).collect(Collectors.toList());
                int[] threeDaysBeforePlanNumStat = statPlanByMaterial(overDueThreeDaysList);
                int partThreeDaysBeforePlanNum = threeDaysBeforePlanNumStat[0];
                int paperThreeDaysBeforePlanNum = threeDaysBeforePlanNumStat[1];
                int[] threeDaysBeforeDeliveryStat = statReceiveByMaterial(overDueThreeDaysList);
                int partThreeDaysBeforeDeliveryTotal = threeDaysBeforeDeliveryStat[0];
                int paperThreeDaysBeforeDeliveryTotal = threeDaysBeforeDeliveryStat[1];
                int partOverDueThreeDaysNum = partThreeDaysBeforePlanNum - partThreeDaysBeforeDeliveryTotal;
                int paperOverDueThreeDaysNum = paperThreeDaysBeforePlanNum - paperThreeDaysBeforeDeliveryTotal;

                statResultDailyList.add(StatResultDaily.builder().provider(provider).projectCode(projectCode)
                        .partPlanTotal(partPlanAfterTotal).paperPlanTotal(paperPlanAfterTotal)
                        .partPlanNum(partPlanNum).paperPlanNum(paperPlanNum)
                        .partDeliveryTotal(partDeliveryActualTotal).paperDeliveryTotal(paperDeliveryActualTotal)
                        .partDeliveryNum(partDeliveryNum).paperDeliveryNum(paperDeliveryNum)
                        .partOweTotal(Math.max(partOweTotal, 0)).paperOweTotal(Math.max(paperOweTotal, 0))
                        .partOweNum(Math.max(partOweNum, 0)).paperOweNum(Math.max(paperOweNum, 0))
                        .partOverdueNum(Math.max(partOweNum, 0)).paperOverdueNum(Math.max(paperOweNum, 0))
                        .partOverdueThreeDaysNum(partOverDueThreeDaysNum).paperOverdueThreeDaysNum(paperOverDueThreeDaysNum)
                        .statisticDate(todayStr).build());
            });
        });
        // 统计今天之后10天的,计划数据
        statResultDailyList.addAll(statAfterToday(statDataDailyList, todayStr));
        return statResultDailyList;
    }

    /**
     * 统计今天及之后的数据
     *
     * @param statDataDailyList 数据来源
     * @return 统计结果
     */
    public List<StatResultDaily> statAfterToday(List<StatDataDaily> statDataDailyList, String todayStr) {
        log.info("开始统计今天及之后的数据：计划总量，日计划：{}", statDataDailyList.size());
        List<StatResultDaily> statResultDailyList = new ArrayList<>();
        // 只有当天的统计才计算之后天的。补算的不计算之后的
        if (StatUtil.dateToSimpleStr(new Date()).equals(todayStr)) {
            // 计算后10天每天的待加工量
            Date calDay = StatUtil.strToDate(todayStr);
            for (int i = 0; i < 10; i++) {
                calDay = StatUtil.plusOneDay(calDay);
                Date finalCalDay = calDay;
                long todayBeginTime = StatUtil.todayBeginTime(StatUtil.dateToSimpleStr(finalCalDay));
                // 加工纳期>=当天
                List<StatDataDaily> processAfterTodayList = statDataDailyList.stream().filter(a -> a.getProcessingTime() != null &&
                        a.getProcessingTime().getTime() / 1000 >= todayBeginTime).collect(Collectors.toList());
                // 按加工商、项目代码分组统计
                Map<String, List<StatDataDaily>> groupByProvider = processAfterTodayList.stream().collect(Collectors.groupingBy(x -> StrUtil.isEmpty(x.getProvider()) ? "其他" : x.getProvider()));
                groupByProvider.forEach((provider, list1) -> {
                    Map<String, List<StatDataDaily>> groupByProjectCode = list1.stream().collect(Collectors.groupingBy(StatDataDaily::getProjectCode));
                    groupByProjectCode.forEach((projectCode, list2) -> {
                        // 计划：总计划、日计划
                        // 之后的总计划:今天及之后的计划
                        int[] planAfterTotalStat = statPlanByMaterial(list2);
                        int partPlanAfterTotal = planAfterTotalStat[0];
                        int paperPlanAfterTotal = planAfterTotalStat[1];
                        // 加工纳期==当天
                        List<StatDataDaily> processInTodayList = list2.stream().filter(a -> a.getProcessingTime() != null &&
                                StatUtil.inToday(a.getProcessingTime(), StatUtil.dateToSimpleStr(finalCalDay))).collect(Collectors.toList());
                        // 日计划
                        int[] planNumStat = statPlanByMaterial(processInTodayList);
                        int partPlanNum = planNumStat[0];
                        int paperPlanNum = planNumStat[1];
                        StatResultDaily statResultDaily = StatResultDaily.builder().provider(provider).projectCode(projectCode)
                                .partPlanNum(partPlanNum).paperPlanNum(paperPlanNum)
                                .partPlanTotal(partPlanAfterTotal).paperPlanTotal(paperPlanAfterTotal)
                                .statisticDate(StatUtil.dateToSimpleStr(finalCalDay)).build();
                        statResultDailyList.add(statResultDaily);
                    });
                });
            }
        }
        return statResultDailyList;
    }

    public int[] statPlanByMaterial(List<StatDataDaily> list) {
        Map<Long, List<StatDataDaily>> groupByMaterial = list.stream().collect(Collectors.groupingBy(StatDataDaily::getMaterialId));
        AtomicInteger partNum = new AtomicInteger();
        AtomicInteger paperNum = new AtomicInteger();
        groupByMaterial.forEach((materialId, list3) -> {
            partNum.addAndGet(list3.get(0).getPartNumber().intValue());
            paperNum.addAndGet(list3.get(0).getPaperNumber());
        });
        return new int[]{partNum.get(), paperNum.get()};
    }

    public int[] statReceiveByMaterial(List<StatDataDaily> list) {
        Map<Long, List<StatDataDaily>> groupByMaterial = list.stream().collect(Collectors.groupingBy(StatDataDaily::getMaterialId));
        AtomicInteger partNum = new AtomicInteger();
        AtomicInteger paperNum = new AtomicInteger();
        groupByMaterial.forEach((materialId, list3) -> {
            int receiveTotal = list3.stream().mapToInt(a -> a.getReceiveNumber() != null ? a.getReceiveNumber() : 0).sum();
            partNum.addAndGet(receiveTotal);
            paperNum.addAndGet(receiveTotal > 0 ? list3.get(0).getPaperNumber() : 0);
        });
        return new int[]{partNum.get(), paperNum.get()};
    }

    public Date getStatisticDate(StatQuery query) {
        Date statisticDate = new Date();
        if (query != null && query.getStatisticDate() != null) {
            statisticDate = query.getStatisticDate();
        }
        return statisticDate;
    }

    public StatDailyQuery getStatDailyQuery(StatQuery query) {
        // 查询加工纳期在4个月之前到之后的
        return StatDailyQuery.builder().endTime(null).startTime(StatUtil.fourMonthAgo(getStatisticDate(query))).build();
    }

    public StatDailyQuery getTodayQuery(StatQuery query) {
        Date todayOffWork = StatUtil.todayOffWork(getStatisticDate(query));
        return StatDailyQuery.builder().startTime(StatUtil.dateToCompleteStr(StatUtil.reduceOneDay(todayOffWork))).endTime(StatUtil.dateToCompleteStr(todayOffWork)).build();
    }
}
