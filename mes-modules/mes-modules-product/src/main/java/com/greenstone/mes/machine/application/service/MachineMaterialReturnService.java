package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialReturnAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineMaterialReturnResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;

import java.util.List;

public interface MachineMaterialReturnService {

    void saveDraft(MachineMaterialReturnAddCmd addCmd);

    void saveCommit(MachineMaterialReturnAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineMaterialReturnResult> selectList(MachineFuzzyQuery query);

    MachineMaterialReturnResult detail(String serialNo);

    MachineOrderPartR scan(MachineOrderPartScanQuery query);

    List<MachineOrderPartR> partChoose(MachineOrderPartListQuery query);
}
