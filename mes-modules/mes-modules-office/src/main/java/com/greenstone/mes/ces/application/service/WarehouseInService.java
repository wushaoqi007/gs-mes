package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseIORemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInEditCmd;
import com.greenstone.mes.ces.application.dto.event.WarehouseUpdateE;
import com.greenstone.mes.ces.application.dto.query.WarehouseIOFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseInResult;
import com.greenstone.mes.ces.dto.cmd.WarehouseIOStatusChangeCmd;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-2-14:19
 */
public interface WarehouseInService {
    void add(WarehouseInAddCmd addCmd);

    void edit(WarehouseInEditCmd editCmd);

    void statusChange(WarehouseIOStatusChangeCmd statusChangeCmd);

    void remove(WarehouseIORemoveCmd removeCmd);

    List<WarehouseInResult> list(WarehouseIOFuzzyQuery query);

    WarehouseInResult detail(String serialNo);

    void warehouseUpdateEvent(WarehouseUpdateE eventData);

    void approved(ProcessResult processResult);
}
