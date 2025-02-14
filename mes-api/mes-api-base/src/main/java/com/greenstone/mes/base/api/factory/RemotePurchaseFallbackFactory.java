package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemotePurchaseService;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.ces.dto.cmd.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author gu_renkai
 * @date 2023/3/6 11:07
 */
@Slf4j
@Component
public class RemotePurchaseFallbackFactory implements FallbackFactory<RemotePurchaseService> {
    @Override
    public RemotePurchaseService create(Throwable throwable) {
        log.error("流程服务调用失败:{}", throwable.getMessage());
        return new RemotePurchaseService() {

            @Override
            public void consumableChangeState(@RequestBody StateChangeCmd changeCmd) {
                throw new ServiceException("开始流程失败");
            }

            @Override
            public void marketChangeState(AppStatusChangeCmd changeCmd) {

            }

            @Override
            public void orderChangeState(OrderStatusChangeCmd changeCmd) {
            }

            @Override
            public void receiptChangeState(ReceiptStatusChangeCmd changeCmd) {
            }

            @Override
            public void warehouseInChangeState(WarehouseIOStatusChangeCmd changeCmd) {

            }

            @Override
            public void warehouseOutChangeState(WarehouseIOStatusChangeCmd changeCmd) {

            }

            @Override
            public void checkStock() {

            }
        };
    }
}
