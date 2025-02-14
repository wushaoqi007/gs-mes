package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.ces.application.dto.cmd.CesReturnAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesReturnRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.query.CesReturnFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.CesReturnItemResult;
import com.greenstone.mes.ces.application.dto.result.CesReturnResult;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-25-14:19
 */
public interface CesReturnService {
    void add(CesReturnAddCmd addCmd);

    void remove(CesReturnRemoveCmd removeCmd);

    List<CesReturnResult> list(CesReturnFuzzyQuery query);

    CesReturnResult detail(String serialNo);

    List<WarehouseInAddCmd> createWarehouseIn(String serialNo);

    List<CesReturnItemResult> itemList(CesReturnFuzzyQuery query);

}
