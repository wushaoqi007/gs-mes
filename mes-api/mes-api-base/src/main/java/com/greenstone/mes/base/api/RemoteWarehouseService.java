package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemoteWarehouseFallbackFactory;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.request.WarehouseBindReq;
import com.greenstone.mes.material.request.WarehouseUnbindReq;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日志服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteWarehouseService", value = ServiceNameConstants.PRODUCT_SERVICE, fallbackFactory = RemoteWarehouseFallbackFactory.class)
public interface RemoteWarehouseService {
    /**
     * 获取仓库信息
     *
     * @param id 仓库ID
     * @return 仓库信息
     */
    @GetMapping("/warehouse/{id}")
    R<BaseWarehouse> getWarehouse(@PathVariable("id") Long id);

    @GetMapping("/warehouse/{id}")
    BaseWarehouse getWarehouse2(@PathVariable("id") Long id);

    @GetMapping("/warehouse/query")
    R<BaseWarehouse> query(@RequestParam("code") String code);

    @GetMapping("/warehouse/query/all")
    R<List<BaseWarehouse>> queryAll(@RequestParam("stage") Integer stage);

    @DeleteMapping("/warehouse/{ids}")
    R<Integer> deleteWarehouse(@PathVariable("ids") Long[] ids);

    @PostMapping("/warehouse/bind")
    BaseWarehouse bind(@RequestBody WarehouseBindReq bindReq);

    @PutMapping("/warehouse/unbind")
    void unbindWarehouse(@RequestBody WarehouseUnbindReq unbindReq);

    @GetMapping("/warehouse/stage/{stage}")
    BaseWarehouse getWithStage(@PathVariable("stage") Integer stage);
}
