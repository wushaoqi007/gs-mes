package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemoteMaterialFallbackFactory;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.request.MaterialAddReq;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 日志服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteMaterialService", value = ServiceNameConstants.PRODUCT_SERVICE, fallbackFactory = RemoteMaterialFallbackFactory.class)
public interface RemoteMaterialService {
    /**
     * 获取物料信息
     *
     * @param id 物料ID
     * @return 物料信息
     */
    @GetMapping("/material/{id}")
    R<BaseMaterial> getMaterial(@PathVariable("id") Long id);

    @GetMapping("/material/{id}")
    BaseMaterial findMaterial(@PathVariable("id") Long id);

    /**
     * 获取物料信息
     *
     * @param code    物料编码
     * @param version 物料版本
     * @return 物料信息
     */
    @GetMapping("/material/query")
    R<BaseMaterial> queryMaterial(@RequestParam("code") String code, @RequestParam("version") String version);

    @PostMapping("/material")
    R<BaseMaterial> add(@RequestBody MaterialAddReq materialAddReq);

    /**
     * 删除物料
     *
     * @param ids 物料id
     */
    @DeleteMapping("/material/{ids}")
    R<Integer> remove(@PathVariable(value = "ids") Long[] ids);

}
