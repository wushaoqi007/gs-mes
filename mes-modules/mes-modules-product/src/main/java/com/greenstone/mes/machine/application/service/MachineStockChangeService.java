package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockChangeAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.event.MachineStockChangeE;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeRecord;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeResult;

import java.util.List;

public interface MachineStockChangeService {

    void saveDraft(MachineStockChangeAddCmd addCmd);

    void saveCommit(MachineStockChangeAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineStockChangeResult> selectList(MachineFuzzyQuery query);

    MachineStockChangeResult detail(String serialNo);

    void operationAfterStockChange(MachineStockChangeE source);

    MachinePartStockR scan(MachinePartStockScanQuery query);

    List<MachinePartStockR> partChoose(MachinePartStockQuery query);

    List<MachineStockChangeRecord> listRecord(MachineRecordQuery query);

    List<MachineStockChangeRecordExportR> exportRecord(MachineRecordQuery query);
}
