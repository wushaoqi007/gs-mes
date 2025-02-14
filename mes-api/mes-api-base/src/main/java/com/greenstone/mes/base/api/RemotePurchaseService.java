package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemotePurchaseFallbackFactory;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.ces.dto.cmd.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author gu_renkai
 * @date 2023/3/6 11:06
 */
@FeignClient(contextId = "remotePurchaseService", value = ServiceNameConstants.OFFICE_SERVICE, fallbackFactory =
        RemotePurchaseFallbackFactory.class)
public interface RemotePurchaseService {

    @PostMapping("/consumable/application/changeState")
    void consumableChangeState(@RequestBody StateChangeCmd changeCmd);

    @PutMapping("/office/market/application/statusChange")
    void marketChangeState(@RequestBody AppStatusChangeCmd changeCmd);

    @PutMapping("/consumable/order/statusChange")
    void orderChangeState(@RequestBody OrderStatusChangeCmd changeCmd);

    @PutMapping("/consumable/receipt/statusChange")
    void receiptChangeState(@RequestBody ReceiptStatusChangeCmd changeCmd);

    @PutMapping("/consumable/warehouse/in/statusChange")
    void warehouseInChangeState(@RequestBody WarehouseIOStatusChangeCmd changeCmd);

    @PutMapping("/consumable/warehouse/out/statusChange")
    void warehouseOutChangeState(@RequestBody WarehouseIOStatusChangeCmd changeCmd);

    @PostMapping("/consumable/warehouse/stock/check")
    void checkStock();
}
