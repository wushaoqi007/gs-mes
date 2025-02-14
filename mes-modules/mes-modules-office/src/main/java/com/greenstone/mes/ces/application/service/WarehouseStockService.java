package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.ces.application.dto.event.WarehouseStockE;
import com.greenstone.mes.ces.application.dto.event.WarehouseUpdateE;
import com.greenstone.mes.ces.application.dto.query.WarehouseStockFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseStockResult;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:27
 */
public interface WarehouseStockService {

    void transfer(WarehouseStockE stockE);

    void checkStock();

    void warehouseUpdateEvent(WarehouseUpdateE eventData);

    List<WarehouseStockResult> list(WarehouseStockFuzzyQuery query);
}
