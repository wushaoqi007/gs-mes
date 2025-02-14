package com.greenstone.mes.material.application.service;

import com.greenstone.mes.material.application.dto.StockRecordSaveCommand;
import com.greenstone.mes.material.response.StockRecordDetailResp;

public interface MaterialStockRecordManager {

    /**
     * 保存出入库记录
     *
     * @param stockRecordSaveCommand materialStockDto
     */
    void saveStockRecord(StockRecordSaveCommand stockRecordSaveCommand);

    StockRecordDetailResp getRecordDetail(Long recordId);

}
