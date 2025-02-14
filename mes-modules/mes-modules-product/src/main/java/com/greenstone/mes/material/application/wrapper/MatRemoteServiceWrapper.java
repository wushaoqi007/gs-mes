package com.greenstone.mes.material.application.wrapper;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteMaterialService;
import com.greenstone.mes.base.api.RemoteWarehouseService;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gu_renkai
 * @date 2022/8/8 16:01
 */

@Slf4j
@Service
public class MatRemoteServiceWrapper {

    private final RemoteWarehouseService warehouseService;

    private final RemoteMaterialService materialService;

    @Autowired
    public MatRemoteServiceWrapper(RemoteWarehouseService warehouseService, RemoteMaterialService materialService) {
        this.warehouseService = warehouseService;
        this.materialService = materialService;
    }

    public BaseWarehouse getWarehouse(Long warehouseId) {
        R<BaseWarehouse> warehouseR = warehouseService.getWarehouse(warehouseId);
        warehouseR.okCheck();
        if (warehouseR.isNotPresent()) {
            log.error("Warehouse id {} is not exist", warehouseId);
            throw new ServiceException(StrUtil.format("仓库ID:'{}'不存在", warehouseId));
        }
        return warehouseR.getData();
    }

    public BaseMaterial getMaterial(String materialCode, String materialVersion) {
        R<BaseMaterial> baseMaterialR = materialService.queryMaterial(materialCode, materialVersion);
        baseMaterialR.okCheck();
        return baseMaterialR.getData();
    }

    public BaseMaterial getMaterial(Long materialId) {
        R<BaseMaterial> baseMaterialR = materialService.getMaterial(materialId);
        baseMaterialR.okCheck();
        return baseMaterialR.getData();
    }

}
