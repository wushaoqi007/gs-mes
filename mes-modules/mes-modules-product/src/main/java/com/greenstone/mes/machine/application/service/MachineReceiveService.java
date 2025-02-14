package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.*;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.domain.entity.MachineReceive;
import com.greenstone.mes.system.api.domain.SysFile;

import java.util.List;

public interface MachineReceiveService {

    void saveDraft(MachineReceiveAddCmd addCmd);

    void saveCommit(MachineReceiveAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineReceiveResult> selectList(MachineFuzzyQuery query);

    MachineReceiveResult detail(String serialNo);

    void doStockWhenReceiveCommit(MachineReceive receive);

    MachineOrderPartR scan(MachineReceivePartScanQuery query);

    List<MachineOrderPartR> partChoose(MachineOrderPartListQuery query);

    void importOrder(MachineReceiveImportCmd importCommand);

    List<MachineReceiveExportR> selectExportDataList(MachineOrderExportQuery query);

    SysFile print(String serialNo);

    List<MachineReceiveRecord> listRecord(MachineRecordQuery query);

    List<MachineReceiveRecordExportR> exportRecord(MachineRecordQuery query);
}
