package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.application.assembler.StatMonthAssembler;
import com.greenstone.mes.material.application.assembler.StatMonthChartAssembler;
import com.greenstone.mes.material.domain.entity.StatResultDaily;
import com.greenstone.mes.material.domain.entity.StatResultMonth;
import com.greenstone.mes.material.domain.entity.StatResultRework;
import com.greenstone.mes.material.domain.service.StatResultMonthService;
import com.greenstone.mes.material.infrastructure.util.StatUtil;
import com.greenstone.mes.material.domain.repository.StatResultDailyRepository;
import com.greenstone.mes.material.domain.repository.StatResultMonthRepository;
import com.greenstone.mes.material.domain.repository.StatResultReworkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-02-23-8:49
 */
@Slf4j
@Service
public class StatResultMonthServiceImpl implements StatResultMonthService {

    private final StatResultMonthRepository statResultMonthRepository;
    private final StatResultDailyRepository statResultDailyRepository;
    private final StatResultReworkRepository statResultReworkRepository;
    private final StatMonthAssembler statMonthAssembler;
    private final StatMonthChartAssembler statMonthChartAssembler;

    public StatResultMonthServiceImpl(StatResultMonthRepository statResultMonthRepository, StatResultDailyRepository statResultDailyRepository,
                                      StatMonthAssembler statMonthAssembler, StatResultReworkRepository statResultReworkRepository,
                                      StatMonthChartAssembler statMonthChartAssembler) {
        this.statResultMonthRepository = statResultMonthRepository;
        this.statResultDailyRepository = statResultDailyRepository;
        this.statMonthAssembler = statMonthAssembler;
        this.statResultReworkRepository = statResultReworkRepository;
        this.statMonthChartAssembler = statMonthChartAssembler;
    }

    @Override
    public void monthStatistics() {
        // 查询本月的日数据统计
        List<StatResultDaily> statResultDailyList = statResultDailyRepository.selectStatResultList(getMonthQuery());
        // 统计月数据
        List<StatResultMonth> statResultMonthList = statMonth(statResultDailyList);
        // 保存月数据
        statResultMonthRepository.save(statResultMonthList);
    }

    @Override
    public List<StatMonthR> selectMonthOwe(StatMonthQuery query) {
        // 查询
        StatMonthQuery monthQuery = statMonthAssembler.toStatMonthQuery(query);
        List<StatResultMonth> statResultMonthList = statResultMonthRepository.selectStatResultList(monthQuery);
        return statMonthAssembler.toMonthOweR(statResultMonthList, monthQuery);
    }

    @Override
    public StatChartBarR selectMonthOweChart(StatMonthQuery query) {
        if (StrUtil.isEmpty(query.getMonth())) {
            // 默认搜索本月数据
            query.setMonth(StatUtil.monthToStr(new Date()));
        }
        List<StatMonthR> statMonthRList = selectMonthOwe(query);
        return statMonthChartAssembler.toMonthOweChartR(statMonthRList, query);
    }

    @Override
    public StatChartBarR selectYearOweChart(StatMonthQuery query) {
        // 默认搜索全年数据
        StatMonthQuery monthQuery = statMonthAssembler.toStatYearOweQuery(query);
        List<StatResultMonth> statResultMonthList = statResultMonthRepository.selectStatResultList(monthQuery);
        List<StatMonthR> statMonthRList = statMonthAssembler.toMonthOweR(statResultMonthList, monthQuery);
        return statMonthChartAssembler.toYearOweChartR(statMonthRList, query);
    }

    @Override
    public List<StatReworkTypeR> selectReworkType(StatMonthQuery query) {
        // 查询月统计结果
        StatMonthQuery monthQuery = statMonthAssembler.toStatReworkQuery(query);
        List<StatResultMonth> statResultMonthList = statResultMonthRepository.selectStatResultList(monthQuery);
        // 查询返工统计结果
        StatDailyQuery reworkTypeQuery = statMonthAssembler.toStatReworkTypeQuery(query);
        List<StatResultRework> statResultReworkList = statResultReworkRepository.selectStatResultReworkList(reworkTypeQuery);
        return statMonthAssembler.toReworkTypeR(statResultMonthList, statResultReworkList, monthQuery);
    }

    @Override
    public StatChartBarAndLineR selectReworkTypeChart(StatMonthQuery query) {
        if (StrUtil.isEmpty(query.getMonth())) {
            // 默认搜索本月数据
            query.setMonth(StatUtil.monthToStr(new Date()));
        }
        List<StatReworkTypeR> statReworkTypeRList = selectReworkType(query);
        return statMonthChartAssembler.toReworkTypeChartR(statReworkTypeRList, query);
    }

    @Override
    public List<StatMonthReworkR> selectReworkRate(StatMonthQuery query) {
        // 查询
        StatMonthQuery monthQuery = statMonthAssembler.toStatYearMonthQuery(query);
        List<StatResultMonth> statResultMonthList = statResultMonthRepository.selectStatResultList(monthQuery);
        return statMonthAssembler.toMonthReworkR(statResultMonthList, monthQuery);
    }

    @Override
    public StatChartBarR selectReworkRateChart(StatMonthQuery query) {
        // 默认查询本年
        if (StrUtil.isEmpty(query.getYear())) {
            query.setYear(StatUtil.toYearStr(new Date()));
        }
        List<StatMonthReworkR> statMonthReworkRList = selectReworkRate(query);
        return statMonthChartAssembler.toReworkRateChartR(statMonthReworkRList, query);
    }

    @Override
    public List<StatMonthCheckR> selectMonthCheck(StatMonthQuery query) {
        // 查询
        StatMonthQuery monthQuery = statMonthAssembler.toStatYearMonthQuery(query);
        List<StatResultMonth> statResultMonthList = statResultMonthRepository.selectStatResultList(monthQuery);
        return statMonthAssembler.toMonthCheckR(statResultMonthList, monthQuery);
    }

    @Override
    public StatChartBarR selectMonthCheckChart(StatMonthQuery query) {
        // 默认查询本年
        if (StrUtil.isEmpty(query.getYear())) {
            query.setYear(StatUtil.toYearStr(new Date()));
        }
        List<StatMonthCheckR> statMonthCheckRList = selectMonthCheck(query);
        return statMonthChartAssembler.toCheckChartR(statMonthCheckRList, query);
    }

    @Override
    public List<StatMonthPlanR> selectMonthPlan(StatMonthQuery query) {
        // 查询
        StatMonthQuery monthQuery = statMonthAssembler.toStatYearMonthQuery(query);
        List<StatResultMonth> statResultMonthList = statResultMonthRepository.selectStatResultList(monthQuery);
        return statMonthAssembler.toMonthPlanR(statResultMonthList, monthQuery);
    }

    @Override
    public StatChartBarAndLineR selectMonthPlanChart(StatMonthQuery query) {
        // 默认查询本年
        if (StrUtil.isEmpty(query.getYear())) {
            query.setYear(StatUtil.toYearStr(new Date()));
        }
        List<StatMonthPlanR> statMonthPlanRList = selectMonthPlan(query);
        return statMonthChartAssembler.toPlanChartR(statMonthPlanRList, query);
    }

    private List<StatResultMonth> statMonth(List<StatResultDaily> statResultDailyList) {
        List<StatResultMonth> result = new ArrayList<>();
        Map<String, List<StatResultDaily>> groupByProvider = statResultDailyList.stream().collect(Collectors.groupingBy(StatResultDaily::getProvider));
        groupByProvider.forEach((provider, list1) -> {
            Map<String, List<StatResultDaily>> groupByProjectCode = list1.stream().collect(Collectors.groupingBy(StatResultDaily::getProjectCode));
            groupByProjectCode.forEach((projectCode, list2) -> {
                int partOweNum = 0;
                int paperOweNum = 0;
                int partDeliveryNum = 0;
                int paperDeliveryNum = 0;
                int partPlanNum = 0;
                int paperPlanNum = 0;
                int partOverdueNum = 0;
                int paperOverdueNum = 0;
                int partOverdueThreeDaysNum = 0;
                int paperOverdueThreeDaysNum = 0;
                int partReworkNum = 0;
                int paperReworkNum = 0;
                int partCheckNum = 0;
                int paperCheckNum = 0;
                for (StatResultDaily statResultDaily : list2) {
                    partOweNum += statResultDaily.getPartOweNum() == null ? 0 : statResultDaily.getPartOweNum();
                    paperOweNum += statResultDaily.getPaperOweNum() == null ? 0 : statResultDaily.getPaperOweNum();
                    partDeliveryNum += statResultDaily.getPartDeliveryNum() == null ? 0 : statResultDaily.getPartDeliveryNum();
                    paperDeliveryNum += statResultDaily.getPaperDeliveryNum() == null ? 0 : statResultDaily.getPaperDeliveryNum();
                    partPlanNum += statResultDaily.getPartPlanNum() == null ? 0 : statResultDaily.getPartPlanNum();
                    paperPlanNum += statResultDaily.getPaperPlanNum() == null ? 0 : statResultDaily.getPaperPlanNum();
                    partOverdueNum += statResultDaily.getPartOverdueNum() == null ? 0 : statResultDaily.getPartOverdueNum();
                    paperOverdueNum += statResultDaily.getPaperOverdueNum() == null ? 0 : statResultDaily.getPaperOverdueNum();
                    partOverdueThreeDaysNum += statResultDaily.getPartOverdueThreeDaysNum() == null ? 0 : statResultDaily.getPartOverdueThreeDaysNum();
                    paperOverdueThreeDaysNum += statResultDaily.getPaperOverdueThreeDaysNum() == null ? 0 : statResultDaily.getPaperOverdueThreeDaysNum();
                    partReworkNum += statResultDaily.getPartReworkNum() == null ? 0 : statResultDaily.getPartReworkNum();
                    paperReworkNum += statResultDaily.getPaperReworkNum() == null ? 0 : statResultDaily.getPaperReworkNum();
                    partCheckNum += statResultDaily.getPartCheckNum() == null ? 0 : statResultDaily.getPartCheckNum();
                    paperCheckNum += statResultDaily.getPaperCheckNum() == null ? 0 : statResultDaily.getPaperCheckNum();
                }
                StatResultMonth statResultMonth = StatResultMonth.builder().provider(provider).projectCode(projectCode)
                        .partOweNum(partOweNum).paperOweNum(paperOweNum)
                        .partDeliveryNum(partDeliveryNum).paperDeliveryNum(paperDeliveryNum)
                        .partPlanNum(partPlanNum).paperPlanNum(paperPlanNum)
                        .partOverdueNum(partOverdueNum).paperOverdueNum(paperOverdueNum)
                        .partOverdueThreeDaysNum(partOverdueThreeDaysNum).paperOverdueThreeDaysNum(paperOverdueThreeDaysNum)
                        .partReworkNum(partReworkNum).paperReworkNum(paperReworkNum)
                        .partCheckNum(partCheckNum).paperCheckNum(paperCheckNum)
                        .statisticDate(StatUtil.dateToSimpleStr(new Date())).statisticMonth(StatUtil.monthToStr(new Date()))
                        .build();
                result.add(statResultMonth);
            });
        });
        return result;
    }

    public StatDailyQuery getMonthQuery() {
        return StatDailyQuery.builder().startTime(StatUtil.dateToSimpleStr(StatUtil.monthStart(new Date()))).endTime(StatUtil.dateToSimpleStr(StatUtil.monthEnd(new Date()))).build();
    }
}
