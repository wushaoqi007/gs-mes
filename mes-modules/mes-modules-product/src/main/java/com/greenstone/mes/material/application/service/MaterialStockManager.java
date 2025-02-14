package com.greenstone.mes.material.application.service;

import com.greenstone.mes.material.application.dto.PartStockNumberEditCmd;
import com.greenstone.mes.material.application.dto.StockTransferVo;

import javax.validation.Valid;

public interface MaterialStockManager {

    void transfer(@Valid StockTransferVo command);

    void updateStockNumber(PartStockNumberEditCmd editCmd);
}
