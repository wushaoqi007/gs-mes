package com.greenstone.mes.material.application.service.impl;

import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.MaterialMachinedPart;
import com.greenstone.mes.material.dto.MachinedPartImportDto;
import com.greenstone.mes.material.application.service.MachinedPartManager;
import com.greenstone.mes.material.request.MaterialInfo;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.domain.service.IMaterialMachinedPartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class MachinedPartManagerImpl implements MachinedPartManager {

    @Autowired
    private IBaseMaterialService materialService;

    @Autowired
    private IBaseWarehouseService warehouseService;

    @Autowired
    private IMaterialMachinedPartService machinedPartService;

    @Override
    @Transactional
    public void importData(List<MachinedPartImportDto> importList) {
        log.info("start save machinedPart, size {}", importList.size());
        List<MaterialMachinedPart> assembledImportList = new ArrayList<>();

        // 组件缓存，避免重复查询
        Map<String, BaseMaterial> componentCache = new HashMap<>();

        for (MachinedPartImportDto machinedPart : importList) {
            // 无组件版本时默认为V0
            if (Objects.isNull(machinedPart.getComponentVersion())) {
                machinedPart.setComponentVersion("V0");
            }
            // 获取组件信息
            BaseMaterial component = componentCache.get(machinedPart.getMaterialCode());
            if (Objects.isNull(component)) {
                BaseMaterial findMaterial = materialService.queryBaseMaterial(BaseMaterial.builder().code(machinedPart.getComponentCode()).version(machinedPart.getComponentVersion()).build());
                if (Objects.nonNull(findMaterial)) {
                    component = findMaterial;
                    componentCache.put(component.getCode(), component);
                } else {
                    throw new ServiceException("系统中不存在编码为" + machinedPart.getComponentCode() + "的组件");
                }
            }

            // 无零件版本时默认为V0
            if (Objects.isNull(machinedPart.getMaterialVersion())) {
                machinedPart.setMaterialVersion("V0");
            }
            // 获取零件信息
            BaseMaterial part = materialService.queryBaseMaterial(BaseMaterial.builder().code(machinedPart.getMaterialCode()).version(machinedPart.getMaterialVersion()).build());
            if (Objects.isNull(part)) {
                throw new ServiceException("系统中不存在编码为" + machinedPart.getMaterialCode() + "版本为" + machinedPart.getMaterialVersion() + "的零件");
            }

            // 组装需要保存的加工件信息
            MaterialMachinedPart materialMachinedPart = MaterialMachinedPart.builder().materialId(part.getId()).
                    materialCode(part.getCode()).
                    materialName(part.getName()).
                    materialVersion(part.getVersion()).
                    number(machinedPart.getNumber()).
                    provider(machinedPart.getProvider()).
                    designer(part.getDesigner()).
                    componentId(component.getId()).
                    componentCode(component.getCode()).
                    componentName(component.getName()).
                    componentVersion(component.getVersion()).
                    purchaseTime(machinedPart.getPurchaseTime()).
                    rawMaterial(part.getRawMaterial()).
                    deliveryTime(machinedPart.getDeliveryTime()).build();

            assembledImportList.add(materialMachinedPart);
        }

        // 保存加工件信息
        machinedPartService.saveBatch(assembledImportList);

        // 导入加工件时的入库信息
        List<MaterialInfo> materialInfoList = new ArrayList<>();

        log.info("start save op update machinedPart, size {}", assembledImportList.size());
        for (MaterialMachinedPart machinedPart : assembledImportList) {
            // 组装加工件的入库信息
            MaterialInfo materialInfo = MaterialInfo.builder().materialId(machinedPart.getMaterialId()).number(machinedPart.getNumber()).build();
            materialInfoList.add(materialInfo);
        }

        // 加工件入库
        saveMachinedPartToWarehouse(materialInfoList);

        log.info("machinedPart save completed");
    }

    /**
     * 机加工件导入的时候直接存入外协库
     */
    private void saveMachinedPartToWarehouse(List<MaterialInfo> materialInfoList) {
        log.info("start save machinedPart to warehouse, size {}", materialInfoList.size());
        BaseWarehouse warehouse = warehouseService.queryWarehouseByCode(BaseWarehouse.builder().code("WX0000").build());
        if (Objects.isNull(warehouse)) {
            log.error("获取WX0000仓库信息失败");
            throw new ServiceException(BizError.E23001);
        }

//        MaterialInStockReq inStockReq = MaterialInStockReq.builder().warehouseId(r.getData().getId()).
//                sponsor(SecurityUtils.getLoginUser().getSysUser().getNickName()).
//                materialList(materialInfoList).build();
//        stockManager.inStock(inStockReq);

    }

}
