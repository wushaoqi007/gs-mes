package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.MaterialStockRecord;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.domain.service.IMaterialStockRecordService;
import com.greenstone.mes.material.enums.WarehouseType;
import com.greenstone.mes.material.infrastructure.mapper.MaterialStockRecordMapper;
import com.greenstone.mes.material.request.StockRecordDetailListReq;
import com.greenstone.mes.material.request.StockRecordMaterialSearchReq;
import com.greenstone.mes.material.request.StockRecordSearchReq;
import com.greenstone.mes.material.response.StockRecordDetailListResp;
import com.greenstone.mes.material.response.StockRecordMaterialSearchResp;
import com.greenstone.mes.material.response.StockRecordSearchResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class MaterialStockRecordServiceImpl extends ServiceImpl<MaterialStockRecordMapper, MaterialStockRecord> implements IMaterialStockRecordService {

    @Autowired
    private MaterialStockRecordMapper materialStockRecordMapper;

    @Autowired
    private IBaseWarehouseService warehouseService;

    /**
     * 查询物料出入库记录
     *
     * @param id 物料出入库记录主键
     * @return 物料出入库记录
     */
    @Override
    public MaterialStockRecord selectMaterialStockRecordById(Long id) {
        return materialStockRecordMapper.selectMaterialStockRecordById(id);
    }


    @Override
    public List<StockRecordSearchResp> listStockRecord(StockRecordSearchReq searchReq) {
        return materialStockRecordMapper.listStockRecord(searchReq);
    }

    @Override
    public List<StockRecordDetailListResp> listStockRecordDetail(StockRecordDetailListReq req) {
        return materialStockRecordMapper.listStockRecordDetail(req);
    }

    @Override
    public List<StockRecordMaterialSearchResp> listStockRecordMaterial(StockRecordMaterialSearchReq searchReq) {
        if (StrUtil.isNotEmpty(searchReq.getWarehouseId())) {
            BaseWarehouse warehouse = warehouseService.selectBaseWarehouseById(Long.parseLong(searchReq.getWarehouseId()));
            if (Objects.isNull(warehouse)) {
                log.error("warehouse not found by id: {}", searchReq.getWarehouseId());
                throw new ServiceException(BizError.E23001, searchReq.getWarehouseId());
            }
            // 砧板库存：查询其绑定仓库的库存记录
            if (Objects.equals(warehouse.getType(), WarehouseType.BOARD.getType())) {
                searchReq.setWarehouseId(warehouse.getParentId().toString());
            }
        }


        return materialStockRecordMapper.listStockRecordMaterial(searchReq);
    }

}