package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartScanQuery2;
import com.greenstone.mes.machine.application.dto.result.MachinePartScanResp;
import com.greenstone.mes.machine.application.service.MachinePartService;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.domain.entity.MachineCheckDetail;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatmentDetail;
import com.greenstone.mes.machine.domain.repository.MachineCheckRepository;
import com.greenstone.mes.machine.domain.repository.MachineOrderRepository;
import com.greenstone.mes.machine.domain.repository.MachineSurfaceTreatmentRepository;
import com.greenstone.mes.machine.interfaces.resp.MachineStageStockResp;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class MachinePartServiceImpl implements MachinePartService {

    private final IBaseMaterialService materialService;
    private final MachineStockService stockService;
    private final MachineOrderRepository orderRepository;
    private final MachineCheckRepository checkRepository;
    private final MachineSurfaceTreatmentRepository surfaceTreatmentRepository;

    @Override
    public MachinePartScanResp partScan(MachinePartScanQuery2 scanQuery) {
        BaseMaterial material = materialService.getOneOnly(BaseMaterial.builder().code(scanQuery.getPartCode()).version(scanQuery.getPartVersion()).build());
        if (material == null) {
            String mgs = StrUtil.format("系统中不存在【{}/{}】的零件信息，请补录零件信息后再尝试。", scanQuery.getPartCode(), scanQuery.getPartVersion());
            throw new RuntimeException(mgs);
        }
        MachinePartScanResp scanResp = MachinePartScanResp.builder()
                .partCode(material.getCode())
                .partVersion(material.getVersion())
                .partName(material.getName())
                .materialId(material.getId())
                .requirementSerialNo(scanQuery.getRequirementSerialNo())
                .projectCode(scanQuery.getProjectCode()).build();
        scanResp.setStocks(getScanDefaultNumber(scanQuery, material));
        return scanResp;
    }

    /**
     * 获取扫码时的默认数量
     */
    private List<MachinePartScanResp.Stock> getScanDefaultNumber(MachinePartScanQuery2 scanQuery, BaseMaterial material) {
        List<MachinePartScanResp.Stock> stocks = new ArrayList<>();
        // 查询订单，除了出库其他操作都要有订单
        MachineOrderDetail orderDetail = orderRepository.selectEffectivePart(MachineOrderDetail.builder().requirementSerialNo(scanQuery.getRequirementSerialNo()).projectCode(scanQuery.getProjectCode())
                .partCode(scanQuery.getPartCode()).partVersion(scanQuery.getPartVersion()).build());
        if (orderDetail == null && scanQuery.getPartOperation() != 11) {
            throw new ServiceException(StrUtil.format("零件没有对应订单，申请单号：{}，项目号：{}，零件号/版本：{}/{}",
                    scanQuery.getRequirementSerialNo(), scanQuery.getProjectCode(), scanQuery.getPartCode(), scanQuery.getPartVersion()));
        }
        // 查询对应的库存数量
        WarehouseStage[] stages = null;
        switch (scanQuery.getPartOperation()) {
            case 1 ->
                    stages = new WarehouseStage[]{WarehouseStage.WAIT_RECEIVE, WarehouseStage.REWORKING, WarehouseStage.TREATING};
            case 3, 4, 5 -> stages = new WarehouseStage[]{WarehouseStage.WAIT_CHECK};
            case 6 -> stages = new WarehouseStage[]{WarehouseStage.WAIT_TREAT_SURFACE};
            case 10 -> stages = new WarehouseStage[]{WarehouseStage.CHECKED_OK};
            case 11 -> stages = new WarehouseStage[]{WarehouseStage.GOOD};
        }
        if (stages == null) {
            throw new RuntimeException("不支持此操作，请联系系统管理员");
        }
        List<MachineStageStockResp> stageStocks = stockService.listStagesStock(material.getId(), stages, scanQuery.getProjectCode());
        if (CollUtil.isEmpty(stageStocks)) {
            throw new ServiceException("零件没有库存，请先调整库存再扫码:" + material.getCode() + "/" + material.getVersion());
        }
        for (MachineStageStockResp stageStock : stageStocks) {
            MachinePartScanResp.Stock stock = MachinePartScanResp.Stock.builder().orderSerialNo(orderDetail == null ? null : orderDetail.getSerialNo())
                    .number(stageStock.getNumber())
                    .orderNumber(orderDetail == null ? null : orderDetail.getProcessNumber())
                    .stage(stageStock.getStage())
                    .warehouseId(stageStock.getWarehouseId())
                    .warehouseCode(stageStock.getWarehouseCode())
                    .stockId(stageStock.getStockId()).build();
            switch (scanQuery.getPartOperation()) {
                case 1 -> {
                    // 收货时返回具体的操作（正常收货、返工收货、表处收货）
                    stock.setOperation(getOperationByStage(stageStock.getStage()));
                    if (stock.getOperation() == 1 && orderDetail != null) {
                        stock.setNumber(orderDetail.getReceivedNumber() == null ? orderDetail.getProcessNumber() : (orderDetail.getProcessNumber() - orderDetail.getReceivedNumber()));
                    }
                    if (stock.getOperation() == 7) {
                        surfaceTreatmentReceive(scanQuery, stock, stocks);
                        continue;
                    }
                    if (stock.getOperation() == 9) {
                        stock = reworkReceive(scanQuery, stock);
                        if (stock == null) {
                            continue;
                        }
                    }
                }
                case 3, 4, 5, 6, 10, 11 -> {
                    //使用订单中购买数量作为默认数量
                    if (orderDetail != null) {
                        stock.setNumber(orderDetail.getProcessNumber());
                    }
                }
            }
            stocks.add(stock);
        }

        return stocks;
    }

    public MachinePartScanResp.Stock reworkReceive(MachinePartScanQuery2 scanQuery, MachinePartScanResp.Stock stock) {
        // 返工收货，没有返工单无法收货，默认数量取返工单未收货数量，扫码后返回返工单号，如果有多个返工单则取总数，且不返回返工单号；
        List<MachineCheckDetail> details = checkRepository.selectReworkDetails(scanQuery);
        details = details.stream().filter(d -> d.getReceivedNumber() == null || d.getCheckedNumber() > d.getReceivedNumber()).collect(Collectors.toList());
        if (CollUtil.isEmpty(details)) {
            return null;
        }
        if (details.size() == 1) {
            stock.setNumber(details.get(0).getReceivedNumber() == null ? details.get(0).getCheckedNumber() : (details.get(0).getCheckedNumber() - details.get(0).getReceivedNumber()));
            stock.setReworkSerialNo(details.get(0).getSerialNo());
        } else {
            // 返工库如果有多个库位，会有问题：每个库位都是总数
            long total = 0;
            for (MachineCheckDetail detail : details) {
                long waitReceive = detail.getReceivedNumber() == null ? detail.getCheckedNumber() : (detail.getCheckedNumber() - detail.getReceivedNumber());
                total += waitReceive;
            }
            stock.setNumber(total);
        }
        return stock;
    }

    public void surfaceTreatmentReceive(MachinePartScanQuery2 scanQuery, MachinePartScanResp.Stock stock, List<MachinePartScanResp.Stock> stocks) {
        // 表处收货，没有表处单无法收货，默认数量取表处单未收货数量，扫码后返回表处单号，如果有多个表处单则需要提示：请先扫描表处单二维码；
        List<MachineSurfaceTreatmentDetail> details = surfaceTreatmentRepository.selectDetailsByPart(scanQuery.getRequirementSerialNo(), scanQuery.getProjectCode(), scanQuery.getPartCode(), scanQuery.getPartVersion());
        details = details.stream().filter(d -> d.getReceivedNumber() == null || d.getHandleNumber() > d.getReceivedNumber()).collect(Collectors.toList());
        if (CollUtil.isEmpty(details)) {
            return;
        }
        for (MachineSurfaceTreatmentDetail detail : details) {
            MachinePartScanResp.Stock stock1 = MachinePartScanResp.Stock.builder()
                    .operation(stock.getOperation())
                    .orderSerialNo(stock.getOrderSerialNo())
                    .surfaceTreatmentSerialNo(detail.getSerialNo())
                    .orderNumber(stock.getOrderNumber())
                    .number(detail.getReceivedNumber() == null ? detail.getHandleNumber() : (detail.getHandleNumber() - detail.getReceivedNumber()))
                    .stage(stock.getStage())
                    .warehouseId(stock.getWarehouseId())
                    .warehouseCode(stock.getWarehouseCode())
                    .stockId(stock.getStockId()).build();
            stocks.add(stock1);
        }
    }


    private int getOperationByStage(int partStage) {
        int operation;
        switch (partStage) {
            case 1 -> operation = 1;
            case 7 -> operation = 7;
            case 8 -> operation = 9;
            default -> operation = 1;
        }
        return operation;
    }

}