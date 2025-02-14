package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.ces.application.dto.query.WarehouseStockFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseStockResult;
import com.greenstone.mes.ces.domain.converter.WarehouseStockConverter;
import com.greenstone.mes.ces.domain.entity.WarehouseStock;
import com.greenstone.mes.ces.domain.entity.WarehouseStockDetail;
import com.greenstone.mes.ces.infrastructure.mapper.WarehouseInDetailMapper;
import com.greenstone.mes.ces.infrastructure.mapper.WarehouseStockMapper;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseInDetailDO;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseStockDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:22
 */
@Slf4j
@Service
public class WarehouseStockRepository {
    @Autowired
    private WarehouseStockMapper stockMapper;
    @Autowired
    private WarehouseInDetailMapper warehouseInDetailMapper;
    @Autowired
    private WarehouseStockConverter stockConverter;

    public WarehouseStock getByWarehouseAndItem(String warehouseCode, String itemCode) {
        return stockConverter.toWarehouseStock(stockMapper.getOneOnly(WarehouseStockDO.builder().warehouseCode(warehouseCode).itemCode(itemCode).build()));
    }

    public List<WarehouseStock> getStockByItemCode(String itemCode) {
        List<WarehouseStockDO> list = stockMapper.list(WarehouseStockDO.builder().itemCode(itemCode).build());
        return stockConverter.toWarehouseStockS(list);

    }

    public void add(WarehouseStock warehouseStock) {
        WarehouseStockDO warehouseStockDO = stockConverter.toWarehouseStockDO(warehouseStock);
        stockMapper.insert(warehouseStockDO);
    }

    public void edit(WarehouseStock warehouseStock) {
        WarehouseStockDO warehouseStockDO = stockConverter.toWarehouseStockDO(warehouseStock);
        stockMapper.updateById(warehouseStockDO);
    }

    public void remove(Long id) {
        stockMapper.deleteById(id);
    }

    public void moveTo(String fromWarehouseCode, String toWarehouseCode) {
        LambdaUpdateWrapper<WarehouseStockDO> updateWrapper =
                Wrappers.lambdaUpdate(WarehouseStockDO.class).eq(WarehouseStockDO::getWarehouseCode, fromWarehouseCode).
                        set(WarehouseStockDO::getWarehouseCode, toWarehouseCode);
        stockMapper.update(updateWrapper);
    }

    public List<WarehouseStockDetail> checkStock() {
        return stockMapper.checkStock();
    }

    public List<WarehouseStockResult> list(WarehouseStockFuzzyQuery query) {
        List<WarehouseStockResult> search = stockMapper.search(query);
        if (CollUtil.isNotEmpty(search)) {
            for (WarehouseStockResult warehouseStockResult : search) {
                // 自定义物品：从入库单中取图片
                WarehouseInDetailDO warehouseInDetailDO = warehouseInDetailMapper.getOneOnly(WarehouseInDetailDO.builder().itemCode(warehouseStockResult.getItemCode()).build());
                warehouseStockResult.setPicturePath(warehouseInDetailDO.getPicturePath());
                if (StrUtil.isEmpty(warehouseStockResult.getSpecification())) {
                    warehouseStockResult.setSpecification(warehouseInDetailDO.getSpecification());
                }
                if (StrUtil.isEmpty(warehouseStockResult.getItemName())) {
                    warehouseStockResult.setItemName(warehouseInDetailDO.getItemName());
                }
                if (StrUtil.isEmpty(warehouseStockResult.getUnit())) {
                    warehouseStockResult.setUnit(warehouseInDetailDO.getUnit());
                }
            }
        }
        return search;
    }
}
