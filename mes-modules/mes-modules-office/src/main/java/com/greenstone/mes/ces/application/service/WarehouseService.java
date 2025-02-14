package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.ces.application.dto.cmd.WarehouseAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseEditCmd;
import com.greenstone.mes.ces.application.dto.query.WarehouseFuzzyQuery;
import com.greenstone.mes.ces.application.dto.query.WarehouseQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseResult;

import java.util.List;

public interface WarehouseService {

    List<WarehouseResult> list(WarehouseQuery query);

    void add(WarehouseAddCmd addCmd);

    void edit(WarehouseEditCmd editCmd);

    void remove(String warehouseCode);

    List<WarehouseResult> search(WarehouseFuzzyQuery query);
}
