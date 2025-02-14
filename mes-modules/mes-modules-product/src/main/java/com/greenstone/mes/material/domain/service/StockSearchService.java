package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.request.StockSearchReq;
import com.greenstone.mes.material.response.StockSearchResp;

import java.util.List;

/**
 * 物料库存Service接口
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
public interface StockSearchService {

    /**
     * 查询仓库中的库存
     *
     * @param searchRequest 查询信息
     * @return 库存信息
     */
    List<StockSearchResp> searchMaterialInStock(StockSearchReq searchRequest);
}
