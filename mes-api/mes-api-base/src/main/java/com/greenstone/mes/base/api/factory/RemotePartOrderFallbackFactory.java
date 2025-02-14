package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemotePartOrderService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.material.request.PartOrderAddReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemotePartOrderFallbackFactory implements FallbackFactory<RemotePartOrderService> {
    private static final Logger log = LoggerFactory.getLogger(RemotePartOrderFallbackFactory.class);

    @Override
    public RemotePartOrderService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemotePartOrderService() {

            @Override
            public R<Long> addPartOrder(PartOrderAddReq partOrderAddReq) {
                return R.fail("添加机加工单失败:" + throwable.getMessage());
            }

        };
    }
}
