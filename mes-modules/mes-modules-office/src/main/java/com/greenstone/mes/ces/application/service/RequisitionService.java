package com.greenstone.mes.ces.application.service;

import com.greenstone.mes.ces.application.dto.cmd.RequisitionAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.RequisitionEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.RequisitionRemoveCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.event.CesReturnAddE;
import com.greenstone.mes.ces.application.dto.query.RequisitionFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.RequisitionItemResult;
import com.greenstone.mes.ces.application.dto.result.RequisitionResult;
import com.greenstone.mes.ces.dto.cmd.RequisitionStatusChangeCmd;
import com.greenstone.mes.form.dto.cmd.ProcessResult;

import java.util.List;

public interface RequisitionService {
    void add(RequisitionAddCmd addCmd);

    void edit(RequisitionEditCmd editCmd);

    void statusChange(RequisitionStatusChangeCmd statusChangeCmd);

    void remove(RequisitionRemoveCmd removeCmd);

    List<RequisitionResult> list(RequisitionFuzzyQuery query);

    RequisitionResult detail(String serialNo);

    List<WarehouseOutAddCmd> createWarehouseOut(String serialNo);

    void approved(ProcessResult processResult);

    List<RequisitionItemResult> itemList(RequisitionFuzzyQuery query);

    void returnAddEvent(CesReturnAddE eventData);
}
