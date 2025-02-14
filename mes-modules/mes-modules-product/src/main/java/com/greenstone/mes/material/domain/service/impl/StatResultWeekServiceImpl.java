package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.date.DateUtil;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.application.dto.StatWeekQuery;
import com.greenstone.mes.material.application.dto.result.StatWeekReworkR;
import com.greenstone.mes.material.application.assembler.StatBoardAssembler;
import com.greenstone.mes.material.domain.entity.StatResultDaily;
import com.greenstone.mes.material.domain.entity.StatResultWeek;
import com.greenstone.mes.material.infrastructure.util.StatUtil;
import com.greenstone.mes.material.domain.repository.StatResultDailyRepository;
import com.greenstone.mes.material.domain.repository.StatResultWeekRepository;
import com.greenstone.mes.material.domain.service.StatResultWeekService;
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
public class StatResultWeekServiceImpl implements StatResultWeekService {

    private final StatResultWeekRepository statResultWeekRepository;
    private final StatResultDailyRepository statResultDailyRepository;
    private final StatBoardAssembler statBoardAssembler;

    public StatResultWeekServiceImpl(StatResultWeekRepository statResultWeekRepository, StatResultDailyRepository statResultDailyRepository, StatBoardAssembler statBoardAssembler) {
        this.statResultWeekRepository = statResultWeekRepository;
        this.statResultDailyRepository = statResultDailyRepository;
        this.statBoardAssembler = statBoardAssembler;
    }

    @Override
    public void weekStatistics() {
        // 查询本周的日数据统计
        List<StatResultDaily> statResultDailyList = statResultDailyRepository.selectStatResultList(getWeekQuery());
        // 统计周数据
        List<StatResultWeek> statResultWeekList = statWeek(statResultDailyList);
        // 保存周数据
        statResultWeekRepository.save(statResultWeekList);
    }

    @Override
    public List<StatWeekReworkR> selectWeekReworkRate(StatWeekQuery query) {
        StatWeekQuery statWeekQuery = statBoardAssembler.toStatReworkQuery(query);
        List<StatResultWeek> statResultWeekList = statResultWeekRepository.selectStatResultList(statWeekQuery);
        return statBoardAssembler.toStatWeekReworkR(statResultWeekList, statWeekQuery);
    }

    private List<StatResultWeek> statWeek(List<StatResultDaily> statResultDailyList) {
        List<StatResultWeek> result = new ArrayList<>();
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
                StatResultWeek statResultMonth = StatResultWeek.builder().provider(provider).projectCode(projectCode)
                        .partOweNum(partOweNum).paperOweNum(paperOweNum)
                        .partDeliveryNum(partDeliveryNum).paperDeliveryNum(paperDeliveryNum)
                        .partPlanNum(partPlanNum).paperPlanNum(paperPlanNum)
                        .partOverdueNum(partOverdueNum).paperOverdueNum(paperOverdueNum)
                        .partOverdueThreeDaysNum(partOverdueThreeDaysNum).paperOverdueThreeDaysNum(paperOverdueThreeDaysNum)
                        .partReworkNum(partReworkNum).paperReworkNum(paperReworkNum)
                        .partCheckNum(partCheckNum).paperCheckNum(paperCheckNum)
                        .statisticDate(StatUtil.dateToSimpleStr(new Date())).statisticMonth(StatUtil.monthToStr(new Date())).monthWeek(DateUtil.weekOfMonth(new Date()))
                        .build();
                result.add(statResultMonth);
            });
        });
        return result;
    }

    public StatDailyQuery getWeekQuery() {
        return StatDailyQuery.builder().startTime(StatUtil.dateToSimpleStr(StatUtil.getCurrentWeek()[0])).endTime(StatUtil.dateToSimpleStr(StatUtil.getCurrentWeek()[1])).build();
    }
}
