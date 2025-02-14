package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineTransferAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineStockAllQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineStockScanQuery;
import com.greenstone.mes.machine.application.dto.event.MachineTransferE;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineTransferResult;

import java.util.List;

public interface MachineTransferService {

    void saveDraft(MachineTransferAddCmd addCmd);

    void saveCommit(MachineTransferAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineTransferResult> selectList(MachineFuzzyQuery query);

    MachineTransferResult detail(String serialNo);

    void operationAfterTransfer(MachineTransferE source);

    MachinePartStockR scan(MachineStockScanQuery query);

    List<MachinePartStockR> stockAll(MachineStockAllQuery query);

}
