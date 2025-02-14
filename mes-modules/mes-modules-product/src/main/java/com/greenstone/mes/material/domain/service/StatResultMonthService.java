package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.application.dto.result.*;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-23-8:47
 */
public interface StatResultMonthService {
    void monthStatistics();

    List<StatMonthR> selectMonthOwe(StatMonthQuery query);

    List<StatReworkTypeR> selectReworkType(StatMonthQuery query);

    List<StatMonthReworkR> selectReworkRate(StatMonthQuery query);

    List<StatMonthCheckR> selectMonthCheck(StatMonthQuery query);

    List<StatMonthPlanR> selectMonthPlan(StatMonthQuery query);

    StatChartBarR selectMonthOweChart(StatMonthQuery query);

    StatChartBarR selectYearOweChart(StatMonthQuery query);

    StatChartBarR selectReworkRateChart(StatMonthQuery query);

    StatChartBarR selectMonthCheckChart(StatMonthQuery query);

    StatChartBarAndLineR selectMonthPlanChart(StatMonthQuery query);

    StatChartBarAndLineR selectReworkTypeChart(StatMonthQuery query);
}
