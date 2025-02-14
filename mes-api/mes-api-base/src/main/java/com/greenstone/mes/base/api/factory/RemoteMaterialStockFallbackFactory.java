package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemoteMaterialStockService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.material.response.StockListResp;
import com.greenstone.mes.material.response.StockTimeOutListResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemoteMaterialStockFallbackFactory implements FallbackFactory<RemoteMaterialStockService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteMaterialStockFallbackFactory.class);

    @Override
    public RemoteMaterialStockService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemoteMaterialStockService() {
            @Override
            public R<List<StockTimeOutListResp>> searchTimeout(Long warehouseId, Integer duration, Boolean containsChildren) {
                return R.fail("获取滞留库存信息失败:" + throwable.getMessage());
            }

            @Override
            public R<List<StockListResp>> listAllStock(Long materialId, String warehouseCode) {
                return R.fail("获取库存失败:" + throwable.getMessage());
            }

        };
    }
}
