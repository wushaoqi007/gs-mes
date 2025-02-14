package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCalculateDetailEditCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCalculateImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStatusChangeCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCalculateHistoryQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCalculateResult;
import com.greenstone.mes.machine.domain.entity.MachineCalculateHistory;

import java.util.List;

public interface MachineCalculateService {

    void importCalculate(MachineCalculateImportCmd importCommand);

    List<MachineCalculateResult> selectList(MachineFuzzyQuery query);

    MachineCalculateResult detail(String serialNo);

    void calculate(MachineCalculateDetailEditCmd editCmd);

    void statusChange(MachineStatusChangeCmd statusChangeCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineCalculateHistory> selectHistory(MachineCalculateHistoryQuery query);
}
