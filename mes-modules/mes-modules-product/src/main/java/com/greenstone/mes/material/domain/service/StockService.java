package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.application.dto.InStockCommand;
import com.greenstone.mes.material.application.dto.OutStockCommand;
import com.greenstone.mes.material.application.dto.PartStockNumberEditCmd;
import com.greenstone.mes.material.application.dto.StockOperationCommand;

/**
 * @author gu_renkai
 * @date 2023/1/13 14:52
 */

public interface StockService {

    void operation(StockOperationCommand transferCommand);

    void inStock(InStockCommand inStockCmd);

    void outStock(OutStockCommand outStockCmd);

    void updateStockNumber(PartStockNumberEditCmd editCmd);
}
