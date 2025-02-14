package com.greenstone.mes.job.task;

import com.greenstone.mes.base.api.RemotePurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 采购任务
 *
 * @author wushaoqi
 * @date 2023-06-08-12:46
 */
@Slf4j
@Component("purchaseTask")
public class PurchaseTask {
    @Autowired
    private RemotePurchaseService purchaseService;

    /**
     * 检查采购仓库库存并提醒
     */
    public void checkStock() {
        log.info("check warehouse stock start");
        purchaseService.checkStock();
    }
}
