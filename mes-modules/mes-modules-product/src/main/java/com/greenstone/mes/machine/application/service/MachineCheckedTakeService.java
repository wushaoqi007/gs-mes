package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckedTakeAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.event.MachineCheckedTakeE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckedTakeRecord;
import com.greenstone.mes.machine.application.dto.result.MachineCheckedTakeResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import com.greenstone.mes.system.api.domain.SysFile;

import java.util.List;

public interface MachineCheckedTakeService {

    void saveDraft(MachineCheckedTakeAddCmd addCmd);

    void saveCommit(MachineCheckedTakeAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineCheckedTakeResult> selectList(MachineFuzzyQuery query);

    List<MachineCheckedTakeRecord> listRecord(MachineRecordFuzzyQuery query);

    MachineCheckedTakeResult detail(String serialNo);

    void operationAfterCheckedTake(MachineCheckedTakeE source);

    String sign(MachineSignCmd signCmd);

    void signFinish(MachineSignFinishCmd finishCmd);

    MachineOrderPartR scan(MachineOrderPartScanQuery query);

    SysFile print(String serialNo);
}
