package com.greenstone.mes.material.application.helper;

import com.greenstone.mes.material.application.dto.StockOperationCommand;
import com.greenstone.mes.material.application.dto.StockTransferVo;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.infrastructure.enums.StorePlaceAction;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2023/1/13 15:46
 */
@Component
public class StockHelper {

    private IBaseMaterialService materialService;

    public StockHelper(IBaseMaterialService materialService) {
        this.materialService = materialService;
    }

    public StockOperationCommand toOperationCommand(StockTransferVo transferVo, StockAction action, BaseWarehouse warehouse) {
        List<StockOperationCommand.TransferMaterial> materialList = new ArrayList<>();
        for (StockTransferVo.MaterialInfo materialInfo : transferVo.getMaterialInfoList()) {
            StockOperationCommand.TransferMaterial transferMaterial =
                    StockOperationCommand.TransferMaterial.builder().material(materialService.getById(materialInfo.getMaterialId()))
                            .number(materialInfo.getNumber())
                            .componentCode(materialInfo.getComponentCode())
                            .projectCode(materialInfo.getProjectCode())
                            .worksheetCode(materialInfo.getWorksheetCode()).build();
            materialList.add(transferMaterial);
        }

        StockOperationCommand.NgData ngData = null;
        if (!Objects.isNull(transferVo.getNgData())) {
            ngData = StockOperationCommand.NgData.builder().ngType(transferVo.getNgData().getNgType()).subNgType(transferVo.getNgData().getSubNgType()).files(transferVo.getNgData().getFiles()).build();
        }

        return StockOperationCommand.builder().action(action).operation(transferVo.getOperation()).partsGroupId(transferVo.getPartsGroupId())
                .warehouse(warehouse)
                .sponsor(transferVo.getSponsor())
                .remark(transferVo.getRemark())
                .outboundAll(transferVo.isOperateAll())
                .ngData(ngData)
                .storePlaceAction(StorePlaceAction.UNBIND)
                .materialList(materialList).build();
    }

    public StockOperationCommand toTransferOperationCommand(StockTransferVo transferVo, StockAction action, BaseWarehouse warehouseOut,
                                                            BaseWarehouse warehouseIn) {
        List<StockOperationCommand.TransferMaterial> materialList = new ArrayList<>();
        for (StockTransferVo.MaterialInfo materialInfo : transferVo.getMaterialInfoList()) {
            StockOperationCommand.TransferMaterial transferMaterial =
                    StockOperationCommand.TransferMaterial.builder().material(materialService.getById(materialInfo.getMaterialId()))
                            .number(materialInfo.getNumber())
                            .componentCode(materialInfo.getComponentCode())
                            .projectCode(materialInfo.getProjectCode())
                            .worksheetCode(materialInfo.getWorksheetCode()).build();
            materialList.add(transferMaterial);
        }

        StockOperationCommand.NgData ngData = null;
        if (!Objects.isNull(transferVo.getNgData())) {
            ngData = StockOperationCommand.NgData.builder().ngType(transferVo.getNgData().getNgType()).subNgType(transferVo.getNgData().getSubNgType()).files(transferVo.getNgData().getFiles()).build();
        }

        return StockOperationCommand.builder().action(action).operation(transferVo.getOperation()).partsGroupId(transferVo.getPartsGroupId())
                .warehouse(warehouseOut)
                .toWarehouse(warehouseIn)
                .sponsor(transferVo.getSponsor())
                .remark(transferVo.getRemark())
                .outboundAll(transferVo.isOperateAll())
                .ngData(ngData)
                .storePlaceAction(StorePlaceAction.TRANSFER)
                .materialList(materialList).build();
    }


}
