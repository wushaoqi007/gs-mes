package com.greenstone.mes.system.api.factory;

import com.greenstone.mes.system.api.RemoteDeptService;
import com.greenstone.mes.system.api.domain.SysDept;
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
public class RemoteDeptFallbackFactory implements FallbackFactory<RemoteDeptService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteDeptFallbackFactory.class);

    @Override
    public RemoteDeptService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteDeptService() {
            @Override
            public SysDept getSysDept(SysDept sysDept) {
                return null;
            }

            @Override
            public void addDept(SysDept sysDept) {

            }

            @Override
            public void updateDept(SysDept sysDept) {

            }

            @Override
            public void deleteDept(Long deptId) {

            }
        };
    }
}
