package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.application.dto.result.*;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-23-8:47
 */
public interface StatResultDesignerService {

    void monthStatistics();

    List<StatMonthPlanR> selectMonthImport(StatMonthQuery query);

    List<StatMonthDesignerOverdueR> selectMonthDesignerOverdue(StatMonthQuery query);

    List<StatMonthSpecialR> selectMonthSpecial(StatMonthQuery query);

    StatChartBarR selectMonthImportChart(StatMonthQuery query);

    StatChartBarAndLineR selectMonthImportOverdueChart(StatMonthQuery query);

    StatChartBarAndLineR selectMonthSpecialChart(StatMonthQuery query);
}
