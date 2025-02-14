package com.greenstone.mes.material.application.service.impl;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.dto.StockRecordSaveCommand;
import com.greenstone.mes.material.application.service.MaterialStockRecordManager;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.MaterialStockRecord;
import com.greenstone.mes.material.domain.MaterialStockRecordDetail;
import com.greenstone.mes.material.enums.WarehouseType;
import com.greenstone.mes.material.response.MaterialInfoResp;
import com.greenstone.mes.material.response.StockRecordDetailResp;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.domain.service.IMaterialStockRecordDetailService;
import com.greenstone.mes.material.domain.service.IMaterialStockRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MaterialStockRecordManagerImpl implements MaterialStockRecordManager {

    @Autowired
    private IMaterialStockRecordService stockRecordService;

    @Autowired
    private IMaterialStockRecordDetailService stockRecordDetailService;

    @Autowired
    private IBaseWarehouseService warehouseService;

    @Override
    public void saveStockRecord(StockRecordSaveCommand stockRecordSaveCommand) {
        // 全部出库，保存其绑定的库位的出入库记录
        BaseWarehouse warehouse = stockRecordSaveCommand.getWarehouse();
        Long warehouseId = WarehouseType.BOARD.match(warehouse.getType()) ? warehouse.getParentId() : warehouse.getId();
        // 保存出入库记录
        MaterialStockRecord stockRecord = MaterialStockRecord.builder().warehouseId(warehouseId).
                sponsor(stockRecordSaveCommand.getSponsor()).
                remark(stockRecordSaveCommand.getRemark()).
                operation(stockRecordSaveCommand.getAction().getId()).build();
        stockRecordService.save(stockRecord);

        // 保存存取记录详情
        List<MaterialStockRecordDetail> stockRecordDetailList = new ArrayList<>();
        List<StockRecordSaveCommand.StockDetail> stockDetailList = stockRecordSaveCommand.getMaterialList();
        for (StockRecordSaveCommand.StockDetail stockDetail : stockDetailList) {
            MaterialStockRecordDetail stockRecordDetail = MaterialStockRecordDetail.builder().recordId(stockRecord.getId()).
                    warehouseId(warehouseId).
                    sponsor(stockRecord.getSponsor()).
                    operation(stockRecord.getOperation()).
                    stageOperation(stockRecordSaveCommand.getOperation() == null ? null : stockRecordSaveCommand.getOperation().getId()).
                    materialId(stockDetail.getMaterial().getId()).
                    numberAfterOperation(stockDetail.getNumberAfterOperation()).
                    number(stockDetail.getNumber())
                    .worksheetCode(stockDetail.getWorksheetCode()).componentCode(stockDetail.getComponentCode()).build();
            stockRecordDetailList.add(stockRecordDetail);
        }
        stockRecordDetailService.saveBatch(stockRecordDetailList, 100);
    }

    @Override
    public StockRecordDetailResp getRecordDetail(Long recordId) {
        MaterialStockRecord stockRecord = stockRecordService.getById(recordId);
        if (Objects.isNull(stockRecord)) {
            throw new ServiceException("stock.record.does.not.exist");
        }
        BaseWarehouse warehouse = warehouseService.selectBaseWarehouseById(stockRecord.getWarehouseId());
        if (Objects.isNull(warehouse)) {
            throw new ServiceException("the.warehouse.does.not.exist");
        }

        StockRecordDetailResp recordDetailResp = new StockRecordDetailResp();
        recordDetailResp.setSponsor(stockRecord.getSponsor());
        recordDetailResp.setOperator(stockRecord.getCreateBy());
        recordDetailResp.setWarehouseName(warehouse.getName());
        recordDetailResp.setOperation(stockRecord.getOperation());
        recordDetailResp.setOperationTime(stockRecord.getCreateTime());
        recordDetailResp.setRemark(stockRecord.getRemark());

        List<MaterialInfoResp> materialList = stockRecordDetailService.listStockRecordDetail(stockRecord.getId());
        recordDetailResp.setMaterialList(materialList);

        return recordDetailResp;
    }

}
