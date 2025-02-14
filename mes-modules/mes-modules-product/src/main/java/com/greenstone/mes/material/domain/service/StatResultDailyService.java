package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.application.dto.StatQuery;
import com.greenstone.mes.material.application.dto.result.StatChartBarR;
import com.greenstone.mes.material.application.dto.result.StatDailyFinishR;
import com.greenstone.mes.material.application.dto.result.StatDailyR;
import com.greenstone.mes.material.application.dto.result.StatPartsDataSourceR;
import com.greenstone.mes.material.dto.cmd.StatDailyCmd;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-23-8:47
 */
public interface StatResultDailyService {

    void dailyStatistics(StatDailyCmd statDailyCmd);

    List<StatDailyR> selectDailyOwe(StatQuery query);

    List<String> listOweProvider(StatQuery query);

    List<StatDailyR> selectDailyOweDistribution(StatQuery query);

    List<StatDailyR> selectDailyDelivery(StatQuery query);

    List<StatDailyFinishR> selectDailyFinish(StatQuery query);

    List<StatDailyR> selectDailyPlan(StatQuery query);

    List<StatPartsDataSourceR> selectDailyOweSource(StatQuery query);

    List<StatPartsDataSourceR> selectDailyDeliverySource(StatQuery query);

    List<StatPartsDataSourceR> selectDailyFinishSource(StatQuery query);

    List<StatPartsDataSourceR> selectDailyPlanSource(StatQuery query);

    StatChartBarR selectDailyOweChart(StatQuery query);

    StatChartBarR selectDailyOweDistributionChart(StatQuery query);

    StatChartBarR selectDailyDeliveryChart(StatQuery query);

    StatChartBarR selectDailyFinishChart(StatQuery query);

    StatChartBarR selectDailyPlanChart(StatQuery query);

    StatChartBarR selectDailyFinishDistributionChart(StatQuery query);
}
