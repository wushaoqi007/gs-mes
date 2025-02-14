package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemoteWarehouseService;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.request.WarehouseBindReq;
import com.greenstone.mes.material.request.WarehouseUnbindReq;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
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
public class RemoteWarehouseFallbackFactory implements FallbackFactory<RemoteWarehouseService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteWarehouseFallbackFactory.class);

    @Override
    public RemoteWarehouseService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemoteWarehouseService() {
            @Override
            public R<BaseWarehouse> getWarehouse(Long id) {
                return R.fail("获取仓库信息失败:" + throwable.getMessage());
            }

            @Override
            public BaseWarehouse getWarehouse2(Long id) {
                return null;
            }

            @Override
            public R<BaseWarehouse> query(String code) {
                return R.fail("获取仓库信息失败:" + throwable.getMessage());
            }

            @Override
            public R<List<BaseWarehouse>> queryAll(Integer stage) {
                return R.fail("获取仓库列表信息失败:" + throwable.getMessage());
            }

            @Override
            public R<Integer> deleteWarehouse(Long[] ids) {
                return R.fail("删除仓库列表信息失败:" + throwable.getMessage());
            }

            @Override
            public BaseWarehouse bind(WarehouseBindReq bindReq) {
                throw new ServiceException("绑定仓库失败");
            }

            @Override
            public void unbindWarehouse(WarehouseUnbindReq unbindReq) {

            }

            @Override
            public BaseWarehouse getWithStage(Integer code) {
                return null;
            }
        };
    }
}
