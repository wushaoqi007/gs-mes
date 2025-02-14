package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReworkAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStatusChangeCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCheckPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineReworkPartScanQuery;
import com.greenstone.mes.machine.application.dto.event.MachineReworkE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartR;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineReworkRecord;
import com.greenstone.mes.machine.application.dto.result.MachineReworkResult;

import java.util.List;

public interface MachineReworkService {

    void saveDraft(MachineReworkAddCmd addCmd);

    void saveCommit(MachineReworkAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineReworkResult> selectList(MachineFuzzyQuery query);

    MachineReworkResult detail(String serialNo);

    List<MachineReworkRecord> listRecord(MachineRecordFuzzyQuery query);

    void operationAfterRework(MachineReworkE source);

    MachineCheckPartStockR scan(MachineReworkPartScanQuery query);

    List<MachineCheckPartStockR> partChoose(MachineCheckPartListQuery query);
}
