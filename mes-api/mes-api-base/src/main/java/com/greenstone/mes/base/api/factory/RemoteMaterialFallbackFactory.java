package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemoteMaterialService;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.request.MaterialAddReq;
import com.greenstone.mes.common.core.domain.R;
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
public class RemoteMaterialFallbackFactory implements FallbackFactory<RemoteMaterialService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteMaterialFallbackFactory.class);

    @Override
    public RemoteMaterialService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemoteMaterialService() {
            @Override
            public R<BaseMaterial> getMaterial(Long id) {
                return R.fail("获取物料信息失败:" + throwable.getMessage());
            }

            @Override
            public BaseMaterial findMaterial(Long id) {
                return null;
            }

            @Override
            public R<BaseMaterial> queryMaterial(String code, String version) {
                return R.fail("获取物料信息失败:" + throwable.getMessage());
            }

            @Override
            public R<BaseMaterial> add(MaterialAddReq materialAddReq) {
                return R.fail("添加物料失败:" + throwable.getMessage());
            }

            @Override
            public R<Integer> remove(Long[] ids) {
                return R.fail("删除物料失败:" + throwable.getMessage());
            }
        };
    }
}
