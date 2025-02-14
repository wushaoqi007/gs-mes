package com.greenstone.mes.machine.domain.service;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockRecordSaveCommand;
import com.greenstone.mes.material.response.StockRecordDetailResp;
import com.greenstone.mes.warehouse.domain.StockCmd;

public interface MachineStockRecordManager {

    void saveStockRecord(StockCmd stockCmd);

    /**
     * 保存出入库记录
     *
     * @param stockRecordSaveCommand materialStockDto
     */
    void saveStockRecord(MachineStockRecordSaveCommand stockRecordSaveCommand);

    StockRecordDetailResp getRecordDetail(Long recordId);

}
