package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.dto.*;
import com.greenstone.mes.material.application.assembler.StockAssembler;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.repository.StockRepository;
import com.greenstone.mes.material.domain.service.StockService;
import com.greenstone.mes.material.enums.WarehouseType;
import com.greenstone.mes.material.event.StockEvent;
import com.greenstone.mes.material.event.StockOperationEvent;
import com.greenstone.mes.material.event.StockUpdateEvent;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.event.data.StockUpdateEventData;
import com.greenstone.mes.material.infrastructure.enums.StorePlaceAction;
import com.greenstone.mes.material.infrastructure.util.StockTransferUtil;
import com.greenstone.mes.material.request.StockListReq;
import com.greenstone.mes.material.request.WarehouseBindReq;
import com.greenstone.mes.material.request.WarehouseUnbindReq;
import com.greenstone.mes.material.response.StockListResp;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.domain.service.MaterialStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2023/1/13 14:54
 */
@Slf4j
@Service
public class StockServiceImpl implements StockService {

    private ApplicationEventPublisher eventPublisher;

    private StockAssembler stockAssembler;

    private StockRepository stockRepository;

    private MaterialStockService stockService;

    private IBaseMaterialService materialService;

    private IBaseWarehouseService warehouseService;


    public StockServiceImpl(ApplicationEventPublisher eventPublisher, StockAssembler stockAssembler,
                            StockRepository stockRepository, MaterialStockService stockService,
                            IBaseMaterialService materialService, IBaseWarehouseService warehouseService) {
        this.eventPublisher = eventPublisher;
        this.stockAssembler = stockAssembler;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
        this.materialService = materialService;
        this.warehouseService = warehouseService;
    }

    /**
     * 所有出入库操作的入口
     */
    @Override
    public void operation(StockOperationCommand operationCommand) {
        switch (operationCommand.getAction()) {
            case IN -> {
                inStock(stockAssembler.toInStockCommand(operationCommand));
                // 发布入库操作事件
                StockOperationEventData stockOperationEventData = stockAssembler.toStockOperationEventData(operationCommand);
                eventPublisher.publishEvent(new StockOperationEvent(stockOperationEventData));
            }
            case OUT -> {
                outStock(stockAssembler.toOutStockCommand(operationCommand));
                // 发布出库操作事件
                StockOperationEventData stockOperationEventData = stockAssembler.toStockOperationEventData(operationCommand);
                // 全部出库时，没有零件信息
                if (operationCommand.isOutboundAll()) {
                    stockOperationEventData = stockAssembler.toStockOperationEventData2(StockTransferUtil.getOutStockCommand());
                }
                eventPublisher.publishEvent(new StockOperationEvent(stockOperationEventData));
            }
            case TRANSFER -> {
                try {
                    transfer(stockAssembler.toTransferStockCommand(operationCommand));
                } finally {
                    // 全部出库的零件暂存在 ThreadLocal 中，用于在入库时使用，在业务结束时，需要将 ThreadLocal 中的数据清除，否则会造成内存溢出
                    StockTransferUtil.removeOutStockCommand();
                }
            }
        }

    }

    @Override
    public void inStock(InStockCommand inStockCmd) {
        log.info("In stock start");
        if (CollUtil.isEmpty(inStockCmd.getMaterialList())) {
            throw new ServiceException("入库清单不能为空");
        }
        // 保存入库数据
        stockRepository.saveInStock(inStockCmd);
        log.info("In stock succeed");
        // 发布入库事件
        eventPublisher.publishEvent(new StockEvent(stockAssembler.toEventData(inStockCmd)));
    }

    @Override
    public void outStock(OutStockCommand outStockCmd) {
        outStockCmd.setForceOut(true);
        if (outStockCmd.isOutboundAll()) {
            // 全部出库的物料列表处理
            setAllOutboundMaterialList(outStockCmd);
        }
        log.info("Out stock start");
        if (CollUtil.isEmpty(outStockCmd.getMaterialList())) {
            log.warn("No material in out stock list");
            throw new ServiceException(BizError.E23006);
        }
        stockRepository.saveOutStock(outStockCmd);
        if (outStockCmd.getStorePlaceAction() == StorePlaceAction.UNBIND) {
            // 解绑
            WarehouseUnbindReq unbindReq = WarehouseUnbindReq.builder().id(outStockCmd.getWarehouse().getId()).build();
            warehouseService.unBindWarehouse(unbindReq);
        }
        log.info("Out stock succeed");
        // 发布出库事件
        eventPublisher.publishEvent(new StockEvent(stockAssembler.toEventData(outStockCmd)));
    }

    @Override
    public void updateStockNumber(PartStockNumberEditCmd editCmd) {
        for (PartStockNumberEditCmd.Material material : editCmd.getMaterialList()) {
            // 更改库存
            Long offset = stockRepository.updateByMaterial(material.getWorksheetCode(), material.getComponentCode(), material.getMaterial().getId(), material.getWarehouseId(), material.getNumber());
            // 发布修改事件
            if (offset != 0) {
                eventPublisher.publishEvent(new StockUpdateEvent(toUpdateEventData(material, offset, editCmd.getSponsor())));
            }
        }

    }

    private StockUpdateEventData toUpdateEventData(PartStockNumberEditCmd.Material material, Long offset, String sponsor) {
        return StockUpdateEventData.builder().warehouse(material.getWarehouse()).number(material.getNumber())
                .worksheetCode(material.getWorksheetCode()).componentCode(material.getComponentCode())
                .material(material.getMaterial()).stockOffset(offset)
                .partCode(material.getPartCode()).partVersion(material.getPartVersion())
                .projectCode(material.getProjectCode()).sponsor(sponsor).build();
    }

    private void transfer(TransferStockCommand transferStockCmd) {
        // 将原仓库的零件出库
        StockOperationCommand outStockOperationCommand = stockAssembler.toOutStockOperationCommand(transferStockCmd);
        operation(outStockOperationCommand);
        // 存放点解绑就在出库后解绑；存放点转移也需要在出库后解绑(前置条件：出库仓库是存放点)
        BaseWarehouse warehouseOut = warehouseService.selectBaseWarehouseById(transferStockCmd.getWarehouseOut().getId());
        if ((warehouseOut.getType() == 1) && (transferStockCmd.getStorePlaceAction() == StorePlaceAction.UNBIND ||
                transferStockCmd.getStorePlaceAction() == StorePlaceAction.TRANSFER)) {
            // 解绑
            WarehouseUnbindReq unbindReq = WarehouseUnbindReq.builder().id(transferStockCmd.getWarehouseOut().getId()).build();
            warehouseService.unBindWarehouse(unbindReq);
        }
        // 将出库零件再入库
        StockOperationCommand inStockOperationCommand = stockAssembler.toInStockOperationCommand(transferStockCmd);
        // 存放点转移需要在解绑后再绑定到入库仓库(前置条件：原仓库是存放点)
        if (warehouseOut.getType() == 1 && transferStockCmd.getStorePlaceAction() == StorePlaceAction.TRANSFER) {
            WarehouseBindReq bindReq = WarehouseBindReq.builder().warehouseId(transferStockCmd.getWarehouseIn()
                            .getId()).code(warehouseOut.getCode())
                    .type(WarehouseType.BOARD.getType()).build();
            BaseWarehouse baseWarehouse = warehouseService.bindWarehouse(bindReq);
            OutStockCommand outStockCommand = StockTransferUtil.getOutStockCommand();
            outStockCommand.setWarehouse(baseWarehouse);
            inStockOperationCommand = stockAssembler.toInStockOperationCommand(outStockCommand);

        }
        // 执行入库
        operation(inStockOperationCommand);
    }

    private void setAllOutboundMaterialList(OutStockCommand outStockCommand) {
        // 在库存转移时，用于出库之后，入库时获取零件信息
        StockTransferUtil.setOutStockCommand(outStockCommand);

        if (Objects.isNull(outStockCommand.getWarehouse().getType()) || Objects.equals(outStockCommand.getWarehouse().getType(),
                WarehouseType.WAREHOUSE.getType())) {
            log.error("Out stock fail: can not outbound all, cause only stock place can do this");
            throw new ServiceException(BizError.E23004);
        }
        if (outStockCommand.getMaterialList() == null) {
            outStockCommand.setMaterialList(new ArrayList<>());
        }
        List<StockListResp> stockList =
                stockService.listStock(StockListReq.builder().type(WarehouseType.BOARD.getType()).warehouseCode(outStockCommand.getWarehouse().getCode()).build());
        for (StockListResp stockListResp : stockList) {
            BaseMaterial material = materialService.getById(stockListResp.getMaterialId());
            if (material == null) {
                log.error("Material id {} is not exist", stockListResp.getMaterialId());
                throw new ServiceException(BizError.E20001);
            }
            OutStockCommand.OutStockMaterial outStockMaterial = stockAssembler.toMaterialStockDetail(stockListResp, material);
            outStockCommand.getMaterialList().add(outStockMaterial);
        }

    }
}
