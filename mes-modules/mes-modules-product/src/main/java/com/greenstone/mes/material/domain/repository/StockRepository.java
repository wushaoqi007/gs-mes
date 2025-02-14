package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.dto.InStockCommand;
import com.greenstone.mes.material.application.dto.OutStockCommand;
import com.greenstone.mes.material.domain.MaterialStock;
import com.greenstone.mes.material.infrastructure.mapper.StockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gu_renkai
 * @date 2022/12/19 10:28
 */
@Slf4j
@Component
public class StockRepository {

    private StockMapper stockMapper;

    public StockRepository(StockMapper stockMapper) {
        this.stockMapper = stockMapper;
    }

    public void saveInStock(InStockCommand inStockCmd) {
        // 更新物料数量，无则保存，有则更新
        List<InStockCommand.InStockMaterial> inStockMaterialList = inStockCmd.getMaterialList();
        for (InStockCommand.InStockMaterial inStockMaterial : inStockMaterialList) {
            MaterialStock stockSelectEntity = MaterialStock.builder().warehouseId(inStockCmd.getWarehouse().getId())
                    .materialId(inStockMaterial.getMaterial().getId())
                    .worksheetCode(inStockMaterial.getWorksheetCode())
                    .componentCode(inStockMaterial.getComponentCode()).build();

            MaterialStock existStock = stockMapper.getOneOnly(stockSelectEntity);
            if (Objects.isNull(existStock)) {
                // 关联加工单的库存查不到，去查历史数据
                stockSelectEntity = MaterialStock.builder().warehouseId(inStockCmd.getWarehouse().getId()).
                        materialId(inStockMaterial.getMaterial().getId()).build();
                existStock = stockMapper.getOneOnly(stockSelectEntity);
            }
            if (Objects.isNull(existStock)) {
                MaterialStock stockAddEntity = MaterialStock.builder().warehouseId(inStockCmd.getWarehouse().getId()).
                        materialId(inStockMaterial.getMaterial().getId()).number(inStockMaterial.getNumber())
                        .worksheetCode(inStockMaterial.getWorksheetCode()).componentCode(inStockMaterial.getComponentCode()).build();
                stockMapper.insert(stockAddEntity);
            } else {
                Long numberAfterOperation = inStockMaterial.getNumber() + existStock.getNumber();
                MaterialStock stockUpdateEntity = MaterialStock.builder().id(existStock.getId()).
                        number(numberAfterOperation).build();
                stockMapper.updateById(stockUpdateEntity);
            }
        }
    }

    public void saveOutStock(OutStockCommand outStockCmd) {
        // 检查库存不足提示
        StringBuilder notEnoughStockMsg = new StringBuilder("库存不足：");
        boolean isEnoughStock = true;

        // 更新物料数量，取出后还有则更新，没有就删除
        List<OutStockCommand.OutStockMaterial> outStockMaterialList = outStockCmd.getMaterialList();
        for (OutStockCommand.OutStockMaterial outStockMaterial : outStockMaterialList) {
            List<MaterialStock> stockList = new ArrayList<>();
            // 校验库存余量是否足够，若不足则抛出错误
            MaterialStock stockSelectEntity = MaterialStock.builder().warehouseId(outStockCmd.getWarehouse().getId())
                    .materialId(outStockMaterial.getMaterial().getId())
                    .worksheetCode(outStockMaterial.getWorksheetCode()).componentCode(outStockMaterial.getComponentCode()).build();
            MaterialStock existStock = stockMapper.getOneOnly(Wrappers.query(stockSelectEntity));
            if (!Objects.isNull(existStock)) {
                stockList.add(existStock);
            }
            // 去查历史数据:不关联加工单的库存
            stockSelectEntity = MaterialStock.builder().warehouseId(outStockCmd.getWarehouse().getId()).
                    materialId(outStockMaterial.getMaterial().getId()).build();
            QueryWrapper<MaterialStock> queryBlank = Wrappers.query(stockSelectEntity);
            queryBlank.lambda().isNull(MaterialStock::getWorksheetCode);
            existStock = stockMapper.getOneOnly(queryBlank);
            if (!Objects.isNull(existStock)) {
                stockList.add(existStock);
            }
            Long stockNum = 0L;
            if (CollUtil.isNotEmpty(stockList)) {
                stockNum = stockList.stream().collect(Collectors.summarizingLong(MaterialStock::getNumber)).getSum();
            }
            if (CollUtil.isEmpty(stockList) || stockNum < outStockMaterial.getNumber()) {
                isEnoughStock = false;
                log.warn("Stock not enough, id {}", outStockMaterial.getMaterial().getCode());
                notEnoughStockMsg.append(outStockMaterial.getMaterial().getName()).append(",").append(outStockMaterial.getMaterial().getCode()).append("/").append(outStockMaterial.getMaterial().getVersion()).append(",库存数量：").append(stockNum).append(";");
            }
            // 若取出后库存为0，则删除库存；若取出后库存有剩余，则更新库存
            long numberAfterOperation = stockNum - outStockMaterial.getNumber();
            if (!CollUtil.isEmpty(stockList) && numberAfterOperation <= 0) {
                for (MaterialStock materialStock : stockList) {
                    stockMapper.deleteById(materialStock);
                }
            }
            if (!CollUtil.isEmpty(stockList) && numberAfterOperation > 0) {
                long needOut = outStockMaterial.getNumber();
                for (MaterialStock materialStock : stockList) {
                    numberAfterOperation = materialStock.getNumber() - needOut;
                    if (numberAfterOperation > 0) {
                        MaterialStock stockUpdateEntity = MaterialStock.builder().id(materialStock.getId()).number(numberAfterOperation).build();
                        stockMapper.updateById(stockUpdateEntity);
                        break;
                    } else {
                        // 使用的这个库存不足，清空
                        stockMapper.deleteById(materialStock);
                        needOut -= materialStock.getNumber();
                    }

                }
            }
        }
        // 不忽略库存不足且库存确实不足，抛出库存异常
        if (!outStockCmd.isForceOut() && !isEnoughStock) {
            throw new ServiceException(BizError.E23002, notEnoughStockMsg.toString());
        }
    }

    public MaterialStock getLatestStock(Long warehouseId, Long materialId) {
        LambdaQueryWrapper<MaterialStock> wrapper = Wrappers.lambdaQuery(MaterialStock.class).eq(MaterialStock::getWarehouseId, warehouseId)
                .eq(MaterialStock::getMaterialId, materialId).orderByDesc(MaterialStock::getCreateTime);
        List<MaterialStock> stockList = stockMapper.selectList(wrapper);
        if (CollUtil.isNotEmpty(stockList)) {
            return stockList.get(0);
        } else {
            return null;
        }
    }

    public Long updateByMaterial(String worksheetCode, String componentCode, Long materialId, Long warehouseId, Long number) {
        long offset = 0;
        if (warehouseId != null) {
            MaterialStock selectDO = MaterialStock.builder().
                    warehouseId(warehouseId).materialId(materialId).
                    worksheetCode(worksheetCode).componentCode(componentCode).build();
            MaterialStock find = stockMapper.getOneOnly(selectDO);
            if (find != null) {
                offset = number - find.getNumber();
                find.setNumber(number);
                stockMapper.updateById(find);
            } else {
                // 没找到库存（需要无中生有）
                offset = number;
                selectDO.setNumber(number);
                stockMapper.insert(selectDO);
            }
        }
        return offset;
    }
}
