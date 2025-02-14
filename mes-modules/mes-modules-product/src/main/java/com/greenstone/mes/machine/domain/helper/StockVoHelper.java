package com.greenstone.mes.machine.domain.helper;

import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.machine.application.dto.event.MachineStockChangeE;
import com.greenstone.mes.machine.application.dto.event.MachineSurfaceTreatmentE;
import com.greenstone.mes.machine.application.dto.event.MachineWarehouseOutE;
import com.greenstone.mes.machine.domain.entity.*;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.warehouse.domain.StockPrepareCmd;
import com.greenstone.mes.warehouse.domain.StockPrepareMaterial;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockVoHelper {

    public List<StockPrepareCmd> converterStockCmds(MachineReceive receive) {
        List<StockPrepareCmd> stockPrepareCmds = new ArrayList<>();
        // 收货可能包含 正常收件、表处收件、返工收件
        Map<Integer, List<MachineReceiveDetail>> groupByOperation = receive.getParts().stream().collect(Collectors.groupingBy(MachineReceiveDetail::getOperation));
        groupByOperation.forEach((operation, partList) -> {
            StockPrepareCmd stockPrepareCmd = new StockPrepareCmd();
            stockPrepareCmds.add(stockPrepareCmd);

            stockPrepareCmd.setOperation(BillOperation.getByIdOrThrow(operation));
            stockPrepareCmd.setSerialNo(receive.getSerialNo());
            stockPrepareCmd.setSponsor(receive.getReceiver());
            stockPrepareCmd.setApplicant(receive.getReceiver());
            stockPrepareCmd.setApplicantNo(receive.getReceiverNo());
            stockPrepareCmd.setProvider(receive.getProvider());
            stockPrepareCmd.setRemark(receive.getRemark());

            List<StockPrepareMaterial> stockPrepareMaterials = new ArrayList<>();
            stockPrepareCmd.setMaterialList(stockPrepareMaterials);

            for (MachineReceiveDetail detail : partList) {
                StockPrepareMaterial prepareMaterial = StockPrepareMaterial.builder()
                        .materialId(detail.getMaterialId())
                        .number(detail.getActualNumber())
                        .warehouseCode(detail.getWarehouseCode())
                        .orderSerialNo(detail.getOrderSerialNo())
                        .projectCode(detail.getProjectCode()).build();
                stockPrepareMaterials.add(prepareMaterial);
            }
        });
        return stockPrepareCmds;
    }

    public StockPrepareCmd converterStockCmd(MachineCheck check) {
        StockPrepareCmd stockPrepareCmd = new StockPrepareCmd();

        stockPrepareCmd.setOperation(getBillOperationByCheckResult(check.getCheckResultType()));
        stockPrepareCmd.setSerialNo(check.getSerialNo());
        stockPrepareCmd.setSponsor(check.getCheckBy());
        stockPrepareCmd.setApplicant(check.getCheckByNo());
        stockPrepareCmd.setApplicantNo(check.getCheckByNo());
        stockPrepareCmd.setRemark(check.getRemark());

        List<StockPrepareMaterial> stockPrepareMaterials = new ArrayList<>();
        stockPrepareCmd.setMaterialList(stockPrepareMaterials);

        for (MachineCheckDetail detail : check.getParts()) {
            StockPrepareMaterial prepareMaterial = StockPrepareMaterial.builder()
                    .materialId(detail.getMaterialId())
                    .number(detail.getCheckedNumber())
                    .warehouseCode(detail.getOutWarehouseCode())
                    .orderSerialNo(detail.getOrderSerialNo())
                    .projectCode(detail.getProjectCode()).build();
            stockPrepareMaterials.add(prepareMaterial);
        }

        return stockPrepareCmd;
    }

    private BillOperation getBillOperationByCheckResult(CheckResultType checkResultType) {
        return switch (checkResultType) {
            case QUALIFIED -> BillOperation.CHECKED_OK_CREATE;
            case REWORK -> BillOperation.CHECKED_NG_CREATE;
            case TREAT_SURFACE -> BillOperation.CHECKED_TREAT_CREATE;
        };
    }

    public StockPrepareCmd converterStockCmd(MachineOrder order) {
        StockPrepareCmd stockPrepareCmd = new StockPrepareCmd();

        stockPrepareCmd.setOperation(BillOperation.ORDER_CREATE);
        stockPrepareCmd.setSerialNo(order.getSerialNo());
        stockPrepareCmd.setSponsor(order.getCreateUser().getNickName());
        stockPrepareCmd.setApplicant(order.getCreateUser().getNickName());
        stockPrepareCmd.setApplicantNo(order.getCreateUser().getEmployeeNo());
        stockPrepareCmd.setProvider(order.getProvider());
        stockPrepareCmd.setRemark(order.getRemark());

        List<StockPrepareMaterial> stockPrepareMaterials = new ArrayList<>();
        stockPrepareCmd.setMaterialList(stockPrepareMaterials);

        for (MachineOrderDetail detail : order.getParts()) {
            StockPrepareMaterial prepareMaterial = StockPrepareMaterial.builder()
                    .materialId(detail.getMaterialId())
                    .number(detail.getProcessNumber())
                    .orderSerialNo(detail.getSerialNo())
                    .projectCode(detail.getProjectCode()).build();
            stockPrepareMaterials.add(prepareMaterial);
        }

        return stockPrepareCmd;
    }

    public StockPrepareCmd converterStockCmd(MachineSurfaceTreatmentE treatment) {
        StockPrepareCmd stockPrepareCmd = new StockPrepareCmd();

        stockPrepareCmd.setOperation(BillOperation.TREAT_SURFACE_CREATE);
        stockPrepareCmd.setSerialNo(treatment.getSerialNo());
        stockPrepareCmd.setSponsor(treatment.getSponsor());
        stockPrepareCmd.setApplicant(treatment.getSponsor());

        List<StockPrepareMaterial> stockPrepareMaterials = new ArrayList<>();
        stockPrepareCmd.setMaterialList(stockPrepareMaterials);

        for (MachineSurfaceTreatmentE.Part detail : treatment.getParts()) {
            StockPrepareMaterial prepareMaterial = StockPrepareMaterial.builder()
                    .materialId(detail.getMaterialId())
                    .number(detail.getHandleNumber())
                    .orderSerialNo(detail.getSerialNo())
                    .warehouseCode(detail.getWarehouseCode())
                    .projectCode(detail.getProjectCode()).build();
            stockPrepareMaterials.add(prepareMaterial);
        }

        return stockPrepareCmd;
    }

    public StockPrepareCmd converterStockCmd(MachineWarehouseIn warehouseIn) {
        StockPrepareCmd stockPrepareCmd = new StockPrepareCmd();

        stockPrepareCmd.setOperation(BillOperation.OK_IN_STOCK_CREATE);
        stockPrepareCmd.setSerialNo(warehouseIn.getSerialNo());
        stockPrepareCmd.setSponsor(warehouseIn.getSponsor());
        stockPrepareCmd.setApplicant(warehouseIn.getApplicant());
        stockPrepareCmd.setApplicantNo(warehouseIn.getApplicantNo());
        stockPrepareCmd.setRemark(warehouseIn.getRemark());

        List<StockPrepareMaterial> stockPrepareMaterials = new ArrayList<>();
        stockPrepareCmd.setMaterialList(stockPrepareMaterials);

        for (MachineWarehouseInDetail detail : warehouseIn.getParts()) {
            StockPrepareMaterial prepareMaterial = StockPrepareMaterial.builder()
                    .materialId(detail.getMaterialId())
                    .number(detail.getInStockNumber())
                    .orderSerialNo(detail.getSerialNo())
                    .warehouseCode(detail.getWarehouseCode())
                    .projectCode(detail.getProjectCode()).build();
            stockPrepareMaterials.add(prepareMaterial);
        }

        return stockPrepareCmd;
    }

    public StockPrepareCmd converterStockCmd(MachineStockChangeE change) {
        StockPrepareCmd stockPrepareCmd = new StockPrepareCmd();

        stockPrepareCmd.setOperation(BillOperation.STOCK_CHANGE_CREATE);
        stockPrepareCmd.setSerialNo(change.getSerialNo());
        stockPrepareCmd.setRemark(change.getRemark());
        stockPrepareCmd.setSponsor(SecurityUtils.getLoginUser().getUser().getNickName());
        stockPrepareCmd.setApplicant(SecurityUtils.getLoginUser().getUser().getNickName());
        stockPrepareCmd.setApplicantNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());

        List<StockPrepareMaterial> stockPrepareMaterials = new ArrayList<>();
        stockPrepareCmd.setMaterialList(stockPrepareMaterials);

        for (MachineStockChangeE.Part detail : change.getParts()) {
            StockPrepareMaterial prepareMaterial = StockPrepareMaterial.builder()
                    .warehouseCode(detail.getWarehouseCode())
                    .materialId(detail.getMaterialId())
                    .number(detail.getChangeNumber())
                    .orderSerialNo(detail.getSerialNo())
                    .projectCode(detail.getProjectCode()).build();
            stockPrepareMaterials.add(prepareMaterial);
        }

        return stockPrepareCmd;
    }

    public StockPrepareCmd converterStockCmd(MachineWarehouseOutE change) {
        StockPrepareCmd stockPrepareCmd = new StockPrepareCmd();

        stockPrepareCmd.setOperation(BillOperation.STOCK_OUT_CREATE);
        stockPrepareCmd.setSerialNo(change.getSerialNo());
        stockPrepareCmd.setRemark(change.getRemark());
        stockPrepareCmd.setSponsor(SecurityUtils.getLoginUser().getUser().getNickName());
        stockPrepareCmd.setApplicant(SecurityUtils.getLoginUser().getUser().getNickName());
        stockPrepareCmd.setApplicantNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());

        List<StockPrepareMaterial> stockPrepareMaterials = new ArrayList<>();
        stockPrepareCmd.setMaterialList(stockPrepareMaterials);

        for (MachineWarehouseOutE.Part detail : change.getParts()) {
            StockPrepareMaterial prepareMaterial = StockPrepareMaterial.builder()
                    .materialId(detail.getMaterialId())
                    .number(detail.getOutStockNumber())
                    .orderSerialNo(detail.getOrderSerialNo())
                    .warehouseCode(detail.getWarehouseCode())
                    .projectCode(detail.getProjectCode()).build();
            stockPrepareMaterials.add(prepareMaterial);
        }

        return stockPrepareCmd;
    }

}
