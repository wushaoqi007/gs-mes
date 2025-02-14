package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.form.dto.cmd.ProcessResult;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseIORemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutEditCmd;
import com.greenstone.mes.ces.application.dto.event.WarehouseUpdateE;
import com.greenstone.mes.ces.application.dto.query.WarehouseIOFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseOutResult;
import com.greenstone.mes.ces.dto.cmd.WarehouseIOStatusChangeCmd;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-5-14:19
 */
public interface WarehouseOutService {
    void add(WarehouseOutAddCmd addCmd);

    void edit(WarehouseOutEditCmd editCmd);

    void statusChange(WarehouseIOStatusChangeCmd statusChangeCmd);

    void remove(WarehouseIORemoveCmd removeCmd);

    List<WarehouseOutResult> list(WarehouseIOFuzzyQuery query);

    WarehouseOutResult detail(String serialNo);

    void warehouseUpdateEvent(WarehouseUpdateE eventData);

    void approved(ProcessResult processResult);
}
