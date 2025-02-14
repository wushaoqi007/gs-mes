package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.application.dto.StatWeekQuery;
import com.greenstone.mes.material.application.dto.result.StatWeekReworkR;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-23-8:47
 */
public interface StatResultWeekService {

    void weekStatistics();

    List<StatWeekReworkR> selectWeekReworkRate(StatWeekQuery query);
}
