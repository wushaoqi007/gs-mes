package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineWarehouseOutAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineStockAllQuery;
import com.greenstone.mes.machine.application.dto.event.MachineWarehouseOutE;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutRecord;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutResult;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import com.greenstone.mes.system.api.domain.SysFile;

import java.util.List;

public interface MachineWarehouseOutService {

    void saveDraft(MachineWarehouseOutAddCmd addCmd);

    void saveCommit(MachineWarehouseOutAddCmd addCmd);

    void remove(MachineRemoveCmd removeCmd);

    List<MachineWarehouseOutResult> selectList(MachineFuzzyQuery query);

    MachineWarehouseOutResult detail(String serialNo);

    void operationAfterWarehouseOut(MachineWarehouseOutE source);

    List<MachinePartStockR> stockAll(MachineStockAllQuery query);

    String sign(MachineSignCmd signCmd);

    void signFinish(MachineSignFinishCmd finishCmd);

    SysFile print(String serialNo);

    List<MachineWarehouseOutRecord> listRecord(MachineRecordQuery query);

    List<MachineWarehouseOutRecordExportR> exportRecord(MachineRecordQuery query);
}
