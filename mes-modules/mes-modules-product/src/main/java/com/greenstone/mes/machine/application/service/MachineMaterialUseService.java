package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialUseAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialUseFinishCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.event.MachineMaterialUseE;
import com.greenstone.mes.machine.application.dto.result.MachineMaterialUseResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;

import java.util.List;

public interface MachineMaterialUseService {

    void saveDraft(MachineMaterialUseAddCmd addCmd);

    void saveCommit(MachineMaterialUseAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineMaterialUseResult> selectList(MachineFuzzyQuery query);

    MachineMaterialUseResult detail(String serialNo);

    void operationAfterMaterialUse(MachineMaterialUseE source);

    MachineOrderPartR scan(MachineOrderPartScanQuery query);

    List<MachineOrderPartR> partChoose(MachineOrderPartListQuery query);

    void finish(MachineMaterialUseFinishCmd finishCmd);
}
