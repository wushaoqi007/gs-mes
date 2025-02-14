package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSurfaceTreatmentAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCheckPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineSurfaceTreatmentPartScanQuery;
import com.greenstone.mes.machine.application.dto.event.MachineSurfaceTreatmentE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentRecord;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentResult;

import java.util.List;

public interface MachineSurfaceTreatmentService {

    void saveDraft(MachineSurfaceTreatmentAddCmd addCmd);

    void saveCommit(MachineSurfaceTreatmentAddCmd editCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineSurfaceTreatmentResult> selectList(MachineFuzzyQuery query);

    MachineSurfaceTreatmentResult detail(String serialNo);

    List<MachineSurfaceTreatmentRecord> listRecord(MachineRecordQuery query);

    void doStockWhenTreatCommit(MachineSurfaceTreatmentE source);

    MachineCheckPartStockR scan(MachineSurfaceTreatmentPartScanQuery query);

    List<MachineCheckPartStockR> partChoose(MachineCheckPartListQuery query);

    List<MachineSurfaceTreatmentRecordExportR> exportRecord(MachineRecordQuery query);
}
