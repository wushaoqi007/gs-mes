package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.application.dto.StockUpdateQuery;
import com.greenstone.mes.material.application.dto.result.StockUpdateR;
import com.greenstone.mes.material.domain.MaterialStock;
import com.greenstone.mes.material.request.StockListReq;
import com.greenstone.mes.material.request.StockSearchListReq;
import com.greenstone.mes.material.request.StockTimeoutSearchReq;
import com.greenstone.mes.material.request.StockTotalListReq;
import com.greenstone.mes.material.response.StockListResp;
import com.greenstone.mes.material.response.StockTimeOutListResp;
import com.greenstone.mes.material.response.StockTotalListResp;

import java.util.List;

/**
 * 物料库存Service接口
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
public interface MaterialStockService extends IServiceWrapper<MaterialStock> {

    List<StockTotalListResp> listStockTotal(StockTotalListReq searchRequest);

    List<StockListResp> listStock(StockListReq searchRequest);

    /**
     * 查询指定物料指定仓库的
     */
    List<StockListResp> listSearchStock(StockSearchListReq searchRequest);

    /**
     * 查询物料滞留库存
     */
    List<StockTimeOutListResp> listStockTimeout(StockTimeoutSearchReq searchReq);

    List<StockUpdateR> listStockForUpdate(StockUpdateQuery query);
}
