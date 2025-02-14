package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckResultCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCheckPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCheckPartScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.system.api.domain.SysFile;

import java.util.List;

public interface MachineCheckService {

    void saveDraft(MachineCheckAddCmd addCmd);

    void saveCommit(MachineCheckAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineCheckResult> selectList(MachineFuzzyQuery query);

    MachineCheckResult detail(String serialNo);

    List<MachineCheckPartR> selectPartList(MachineCheckPartListQuery query);

    void resultEntry(MachineCheckResultCmd resultCmd);

    MachineCheckPartR resultScan(MachineCheckPartScanQuery query);

    List<MachineCheckCountR> checkCount(MachineCheckPartListQuery query);

    List<MachineCheckRecord> listRecord(MachineCheckPartListQuery query);

    SysFile print(String serialNo);

    List<MachineCheckRecord> reworkRecord(MachineRecordQuery query);

    List<MachineReworkRecordExportR> exportRecord(MachineRecordQuery query);
}
