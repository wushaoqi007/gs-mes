package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.machine.application.dto.result.MachineStockRecordExportR;
import com.greenstone.mes.machine.application.service.MachineStockRecordService;
import com.greenstone.mes.machine.infrastructure.mapper.MachineStockRecordMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockRecord;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.infrastructure.enums.StockBehavior;
import com.greenstone.mes.material.request.StockRecordDetailListReq;
import com.greenstone.mes.material.request.StockRecordMaterialSearchReq;
import com.greenstone.mes.material.request.StockRecordSearchReq;
import com.greenstone.mes.material.response.StockRecordDetailListResp;
import com.greenstone.mes.material.response.StockRecordMaterialSearchResp;
import com.greenstone.mes.material.response.StockRecordSearchResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 物料出入库记录Service业务层处理
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@Slf4j
@Service
public class MachineStockRecordServiceImpl extends ServiceImpl<MachineStockRecordMapper, MachineStockRecord> implements MachineStockRecordService {

    @Autowired
    private MachineStockRecordMapper machineStockRecordMapper;

    @Autowired
    private IBaseWarehouseService warehouseService;


    @Override
    public List<StockRecordSearchResp> listStockRecord(StockRecordSearchReq searchReq) {
        return machineStockRecordMapper.listStockRecord(searchReq);
    }

    @Override
    public List<StockRecordDetailListResp> listStockRecordDetail(StockRecordDetailListReq req) {
        return machineStockRecordMapper.listStockRecordDetail(req);
    }

    @Override
    public List<StockRecordMaterialSearchResp> listStockRecordMaterial(StockRecordMaterialSearchReq searchReq) {
        if (StrUtil.isNotEmpty(searchReq.getWarehouseId())) {
            BaseWarehouse warehouse = warehouseService.selectBaseWarehouseById(Long.parseLong(searchReq.getWarehouseId()));
            if (Objects.isNull(warehouse)) {
                log.error("warehouse not found by id: {}", searchReq.getWarehouseId());
                throw new ServiceException(BizError.E23001, searchReq.getWarehouseId());
            }
        }
        if (searchReq.getOperationTimeTo() != null) {
            searchReq.setOperationTimeTo(DateUtil.endOfDay(searchReq.getOperationTimeTo()));
        }
        return machineStockRecordMapper.listStockRecordMaterial(searchReq);
    }

    @Override
    public List<MachineStockRecordExportR> exportStockRecord(StockRecordMaterialSearchReq searchReq) {
        List<StockRecordMaterialSearchResp> stockRecords = listStockRecordMaterial(searchReq);
        List<MachineStockRecordExportR> exportRS = new ArrayList<>();
        for (StockRecordMaterialSearchResp stockRecord : stockRecords) {
            MachineStockRecordExportR recordExportR = MachineStockRecordExportR.builder().projectCode(stockRecord.getProjectCode()).partCode(stockRecord.getMaterialCode())
                    .partVersion(stockRecord.getMaterialVersion()).partName(stockRecord.getMaterialName())
                    .number(stockRecord.getNumber()).sponsor(stockRecord.getSponsor())
                    .createTime(stockRecord.getOperationTime()).warehouseCode(stockRecord.getWarehouseCode()).build();
            exportRS.add(recordExportR);
            StockBehavior stockBehavior = StockBehavior.getById(stockRecord.getBehavior());
            recordExportR.setType(stockBehavior == null ? "其他" : stockBehavior.getName());
        }
        return exportRS;
    }

}