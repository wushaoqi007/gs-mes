package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.domain.MaterialStockRecord;
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
public interface IMaterialStockRecordService extends IServiceWrapper<MaterialStockRecord> {
    /**
     * 查询物料出入库记录
     *
     * @param id 物料出入库记录主键
     * @return 物料出入库记录
     */
    MaterialStockRecord selectMaterialStockRecordById(Long id);

    List<StockRecordSearchResp> listStockRecord(StockRecordSearchReq searchDto);

    List<StockRecordDetailListResp> listStockRecordDetail(StockRecordDetailListReq req);

    List<StockRecordMaterialSearchResp> listStockRecordMaterial(StockRecordMaterialSearchReq searchReq);

}