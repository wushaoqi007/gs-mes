package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineWarehouseInAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInRecord;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseInResult;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseIn;

import java.util.List;

public interface MachineWarehouseInService {

    void saveDraft(MachineWarehouseInAddCmd addCmd);

    void saveCommit(MachineWarehouseInAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineWarehouseInResult> selectList(MachineFuzzyQuery query);

    MachineWarehouseInResult detail(String serialNo);

    void doStockWhenStockInGoodsCommit(MachineWarehouseIn warehouseIn);

    List<MachineWarehouseInRecord> listRecord(MachineRecordQuery query);

    List<MachineWarehouseInRecordExportR> exportRecord(MachineRecordQuery query);
}
