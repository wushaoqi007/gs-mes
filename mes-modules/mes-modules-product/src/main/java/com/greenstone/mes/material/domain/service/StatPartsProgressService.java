package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.application.dto.StatQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.dto.cmd.StatProgressCmd;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-23-8:47
 */
public interface StatPartsProgressService {

    void partsProgressStatistics(StatProgressCmd statProgressCmd);

    List<StatProjectAnalyseR> projectAnalyse(StatQuery query);

    StatProjectCountR projectCount(StatQuery query);

    List<StatPartsProgressR> ongoingList(StatQuery query);

    List<StatProjectProgressR> projectProgress(StatQuery query);

    StatChartBarR projectAnalyseChart(StatQuery query);
}
