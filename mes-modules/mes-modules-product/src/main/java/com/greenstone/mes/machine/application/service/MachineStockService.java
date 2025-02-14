package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineInStockCommand;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineOutStockCommand;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockOperationCommand;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRealStockQuery;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.interfaces.resp.MachineStageStockResp;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.warehouse.domain.StockCmd;
import com.greenstone.mes.warehouse.domain.StockPrepareCmd;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/13 14:52
 */

public interface MachineStockService {

    void doStock(StockPrepareCmd stockPrepareCmd);

    void operation(MachineStockOperationCommand transferCommand);

    void inStock(MachineInStockCommand inStockCmd);

    void outStock(MachineOutStockCommand outStockCmd);

    List<MachinePartStockR> listRealStock(MachineRealStockQuery query);

    List<MachineStageStockResp> listStagesStock(Long partId, WarehouseStage[] Stages, String projectCode);

    List<MachineStockExportR> exportRealStock(MachineRealStockQuery query);

    List<MachineStockWaitReceiveExportR> exportWaitReceiveStock(MachineRealStockQuery query);

    List<MachineStockCheckedExportR> exportCheckedStock(MachineRealStockQuery query);

    List<MachineStockReceivingExportR> exportReceivingStock(MachineRealStockQuery query);

    List<MachineStockTreatingExportR> exportTreatingStock(MachineRealStockQuery query);

    List<MachineStockStageExportR> exportStockStage(MachineRealStockQuery query);
}
