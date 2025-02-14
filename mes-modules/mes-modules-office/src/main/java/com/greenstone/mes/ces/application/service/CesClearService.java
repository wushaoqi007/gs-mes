package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.ces.application.dto.cmd.CesClearAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesClearEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesClearRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.query.CesClearFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.CesClearResult;
import com.greenstone.mes.ces.dto.cmd.CesClearStatusChangeCmd;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-05-25-14:19
 */
public interface CesClearService {
    void add(CesClearAddCmd addCmd);

    void edit(CesClearEditCmd editCmd);

    void statusChange(CesClearStatusChangeCmd statusChangeCmd);

    void remove(CesClearRemoveCmd removeCmd);

    List<CesClearResult> list(CesClearFuzzyQuery query);

    CesClearResult detail(String serialNo);

    List<WarehouseOutAddCmd> createWarehouseOut(String serialNo);

}
