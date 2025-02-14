package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemoteMaterialStockFallbackFactory;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.material.response.StockListResp;
import com.greenstone.mes.material.response.StockTimeOutListResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteMaterialStockService", value = ServiceNameConstants.PRODUCT_SERVICE, fallbackFactory = RemoteMaterialStockFallbackFactory.class)
public interface RemoteMaterialStockService {

    @GetMapping("/stock/list/all/timeout")
    R<List<StockTimeOutListResp>> searchTimeout(@RequestParam("warehouseId") Long warehouseId, @RequestParam("duration") Integer duration, @RequestParam("containsChildren") Boolean containsChildren);

    @GetMapping("/stock/list/all")
    R<List<StockListResp>> listAllStock(@RequestParam("materialId") Long materialId, @RequestParam("warehouseCode") String warehouseCode);

}
