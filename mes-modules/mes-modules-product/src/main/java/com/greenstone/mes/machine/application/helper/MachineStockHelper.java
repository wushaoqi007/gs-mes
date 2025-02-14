package com.greenstone.mes.machine.application.helper;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockOperationCommand;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockTransferVo;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2023/1/13 15:46
 */
@Component
public class MachineStockHelper {

    private IBaseMaterialService materialService;

    public MachineStockHelper(IBaseMaterialService materialService) {
        this.materialService = materialService;
    }

    public MachineStockOperationCommand toOperationCommand(MachineStockTransferVo transferVo, StockAction action, BaseWarehouse warehouse) {
        List<MachineStockOperationCommand.TransferMaterial> materialList = new ArrayList<>();
        for (MachineStockTransferVo.MaterialInfo materialInfo : transferVo.getMaterialInfoList()) {
            MachineStockOperationCommand.TransferMaterial transferMaterial =
                    MachineStockOperationCommand.TransferMaterial.builder().material(materialService.getById(materialInfo.getMaterialId()))
                            .number(materialInfo.getNumber())
                            .projectCode(materialInfo.getProjectCode()).orderDetailId(materialInfo.getOrderDetailId())
                            .orderSerialNo(materialInfo.getOrderSerialNo()).build();
            materialList.add(transferMaterial);
        }

        MachineStockOperationCommand.NgData ngData = null;
        if (!Objects.isNull(transferVo.getNgData())) {
            ngData = MachineStockOperationCommand.NgData.builder().ngType(transferVo.getNgData().getNgType()).subNgType(transferVo.getNgData().getSubNgType()).build();
        }

        return MachineStockOperationCommand.builder().action(action).operation(transferVo.getOperation())
                .warehouse(warehouse)
                .sponsor(transferVo.getSponsor())
                .applicantNo(transferVo.getApplicantNo())
                .applicant(transferVo.getApplicant())
                .remark(transferVo.getRemark())
                .ngData(ngData)
                .materialList(materialList).build();
    }

    public MachineStockOperationCommand toTransferOperationCommand(MachineStockTransferVo transferVo, StockAction action, BaseWarehouse warehouseOut,
                                                                   BaseWarehouse warehouseIn) {
        List<MachineStockOperationCommand.TransferMaterial> materialList = new ArrayList<>();
        for (MachineStockTransferVo.MaterialInfo materialInfo : transferVo.getMaterialInfoList()) {
            MachineStockOperationCommand.TransferMaterial transferMaterial =
                    MachineStockOperationCommand.TransferMaterial.builder().material(materialService.getById(materialInfo.getMaterialId()))
                            .number(materialInfo.getNumber())
                            .projectCode(materialInfo.getProjectCode()).orderDetailId(materialInfo.getOrderDetailId())
                            .orderSerialNo(materialInfo.getOrderSerialNo()).build();
            materialList.add(transferMaterial);
        }

        MachineStockOperationCommand.NgData ngData = null;
        if (!Objects.isNull(transferVo.getNgData())) {
            ngData = MachineStockOperationCommand.NgData.builder().ngType(transferVo.getNgData().getNgType()).subNgType(transferVo.getNgData().getSubNgType()).build();
        }

        return MachineStockOperationCommand.builder().action(action).operation(transferVo.getOperation())
                .warehouse(warehouseOut)
                .toWarehouse(warehouseIn)
                .sponsor(transferVo.getSponsor())
                .applicant(transferVo.getApplicant())
                .applicantNo(transferVo.getApplicantNo())
                .remark(transferVo.getRemark())
                .ngData(ngData)
                .materialList(materialList).build();
    }


}
