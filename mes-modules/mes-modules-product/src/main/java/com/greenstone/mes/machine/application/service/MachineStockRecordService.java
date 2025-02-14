package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.machine.application.dto.result.MachineStockRecordExportR;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockRecord;
import com.greenstone.mes.material.request.StockRecordDetailListReq;
import com.greenstone.mes.material.request.StockRecordMaterialSearchReq;
import com.greenstone.mes.material.request.StockRecordSearchReq;
import com.greenstone.mes.material.response.StockRecordDetailListResp;
import com.greenstone.mes.material.response.StockRecordMaterialSearchResp;
import com.greenstone.mes.material.response.StockRecordSearchResp;

import java.util.List;

/**
 * 物料出入库记录Service接口
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
public interface MachineStockRecordService extends IServiceWrapper<MachineStockRecord> {

    List<StockRecordSearchResp> listStockRecord(StockRecordSearchReq searchDto);

    List<StockRecordDetailListResp> listStockRecordDetail(StockRecordDetailListReq req);

    List<StockRecordMaterialSearchResp> listStockRecordMaterial(StockRecordMaterialSearchReq searchReq);

    List<MachineStockRecordExportR> exportStockRecord(StockRecordMaterialSearchReq searchReq);
}