package com.greenstone.mes.office.api.factory;

import com.greenstone.mes.office.api.RemoteMeetingService;
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
public class RemoteMeetingFallbackFactory implements FallbackFactory<RemoteMeetingService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteMeetingFallbackFactory.class);

    @Override
    public RemoteMeetingService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteMeetingService() {
            @Override
            public void changeStatus() {

            }
        };
    }
}
