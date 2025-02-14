package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckTakeAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.event.MachineCheckTakeE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckTakeRecord;
import com.greenstone.mes.machine.application.dto.result.MachineCheckTakeResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import com.greenstone.mes.system.api.domain.SysFile;

import java.util.List;

public interface MachineCheckTakeService {

    void saveDraft(MachineCheckTakeAddCmd addCmd);

    void saveCommit(MachineCheckTakeAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineCheckTakeResult> selectList(MachineFuzzyQuery query);

    List<MachineCheckTakeRecord> listRecord(MachineRecordFuzzyQuery query);

    MachineCheckTakeResult detail(String serialNo);

    void operationAfterCheckTake(MachineCheckTakeE source);

    String sign(MachineSignCmd signCmd);

    void signFinish(MachineSignFinishCmd finishCmd);

    MachineOrderPartR scan(MachineOrderPartScanQuery query);

    SysFile print(String serialNo);
}
