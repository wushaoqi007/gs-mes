package com.greenstone.mes.material.domain.service.impl;

import com.greenstone.mes.material.domain.service.StockSearchService;
import com.greenstone.mes.material.infrastructure.mapper.StockSearchMapper;
import com.greenstone.mes.material.request.StockSearchReq;
import com.greenstone.mes.material.response.StockSearchResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物料库存Service业务层处理
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Service
public class StockSearchServiceImpl implements StockSearchService {
    @Autowired
    private StockSearchMapper stockSearchMapper;

    @Override
    public List<StockSearchResp> searchMaterialInStock(StockSearchReq searchRequest) {
        return stockSearchMapper.searchMaterialInStock(searchRequest);
    }
}
