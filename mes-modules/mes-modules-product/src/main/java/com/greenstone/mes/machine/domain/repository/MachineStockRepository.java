package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineInStockCommand;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineOutStockCommand;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRealStockQuery;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.infrastructure.mapper.MachineStockMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStock;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.warehouse.domain.StockCmd;
import com.greenstone.mes.warehouse.domain.StockMaterial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gu_renkai
 * @date 2022/12/19 10:28
 */
@Slf4j
@Component
public class MachineStockRepository {

    private final MachineStockMapper stockMapper;

    public MachineStockRepository(MachineStockMapper stockMapper) {
        this.stockMapper = stockMapper;
    }

    public MachineStock getStock(Long warehouseId, Long materialId, String projectCode) {
        return stockMapper.getOneOnly(MachineStock.builder().warehouseId(warehouseId).materialId(materialId).projectCode(projectCode).build());
    }

    public void doStock(StockCmd stockCmd) {
        // 合并相同 动作（出入库）、项目号、零件id 的数量
        List<StockMaterial> sumMaterialList = stockCmd.getMaterialList().stream()
                .collect(
                        Collectors.toMap(material -> material.getAction().getId() + "|" + material.getProjectCode() + "|" + material.getMaterial().getId(),
                                material -> StockMaterial.builder().material(material.getMaterial())
                                        .behavior(material.getBehavior())
                                        .action(material.getAction())
                                        .warehouse(material.getWarehouse())
                                        .projectCode(material.getProjectCode())
                                        .orderSerialNo(material.getOrderSerialNo())
                                        .number(material.getNumber()).build(),
                                (existing, replacement) -> {
                                    existing.setNumber(existing.getNumber() + replacement.getNumber());
                                    return existing;
                                }
                        )
                )
                .values().stream().toList();

        for (StockMaterial stockMaterial : sumMaterialList) {
            QueryWrapper<MachineStock> queryWrapper = Wrappers.query(MachineStock.builder()
                    .warehouseId(stockMaterial.getWarehouse().getId())
                    .materialId(stockMaterial.getMaterial().getId())
                    .projectCode(stockMaterial.getProjectCode()).build());
            MachineStock existStock = stockMapper.getOneOnly(queryWrapper);

            // 入库，无则插入，有则更新
            if (stockMaterial.getAction() == StockAction.IN) {
                if (Objects.isNull(existStock)) {
                    MachineStock stockAddEntity = MachineStock.builder()
                            .projectCode(stockMaterial.getProjectCode())
                            .warehouseId(stockMaterial.getWarehouse().getId())
                            .materialId(stockMaterial.getMaterial().getId())
                            .number(stockMaterial.getNumber()).build();
                    stockMapper.insert(stockAddEntity);
                } else {
                    Long numberAfterOperation = stockMaterial.getNumber() + existStock.getNumber();
                    MachineStock stockUpdateEntity = MachineStock.builder().id(existStock.getId()).
                            number(numberAfterOperation).build();
                    stockMapper.updateById(stockUpdateEntity);
                }
            }
            // 出库，无库存或数量不足则出库失败
            else if (stockMaterial.getAction() == StockAction.OUT) {
                // 库存不足且不强制出库抛出错误
                if (Objects.isNull(existStock) || existStock.getNumber() < stockMaterial.getNumber()) {
                    String msg = StrUtil.format("项目号：{}，零件号/版本：{}/{}，零件名称：{}，库存数量：{}，总出库数量：{};",
                            stockMaterial.getProjectCode(), stockMaterial.getMaterial().getCode(),
                            stockMaterial.getMaterial().getVersion(), stockMaterial.getMaterial().getName(),
                            existStock == null ? 0 : existStock.getNumber(), stockMaterial.getNumber());
                    throw new ServiceException(BizError.E23002, msg);
                }
                // 若取出后库存为0，则删除库存
                long numberAfterOperation = existStock.getNumber() - stockMaterial.getNumber();
                if (numberAfterOperation <= 0) {
                    stockMapper.deleteById(existStock);
                }
                // 若取出后库存有剩余，则更新库存
                if (numberAfterOperation > 0) {
                    MachineStock stockUpdateEntity = MachineStock.builder().id(existStock.getId()).number(numberAfterOperation).build();
                    stockMapper.updateById(stockUpdateEntity);
                }
            }
        }
    }

    public void saveInStock(MachineInStockCommand inStockCmd) {
        // 物料入库，无则插入，有则更新
        List<MachineInStockCommand.InStockMaterial> inStockMaterialList = inStockCmd.getMaterialList();
        for (MachineInStockCommand.InStockMaterial inStockMaterial : inStockMaterialList) {
            QueryWrapper<MachineStock> queryWrapper = Wrappers.query(MachineStock.builder().warehouseId(inStockCmd.getWarehouse().getId())
                    .materialId(inStockMaterial.getMaterial().getId()).build());
            if (StrUtil.isEmpty(inStockMaterial.getProjectCode())) {
                queryWrapper.isNull("project_code");
            } else {
                queryWrapper.eq("project_code", inStockMaterial.getProjectCode());
            }
            MachineStock existStock = stockMapper.getOneOnly(queryWrapper);
            if (Objects.isNull(existStock)) {
                MachineStock stockAddEntity = MachineStock.builder().projectCode(inStockMaterial.getProjectCode()).warehouseId(inStockCmd.getWarehouse().getId()).
                        materialId(inStockMaterial.getMaterial().getId()).number(inStockMaterial.getNumber()).build();
                stockMapper.insert(stockAddEntity);
            } else {
                Long numberAfterOperation = inStockMaterial.getNumber() + existStock.getNumber();
                MachineStock stockUpdateEntity = MachineStock.builder().id(existStock.getId()).
                        number(numberAfterOperation).build();
                stockMapper.updateById(stockUpdateEntity);
            }
        }
    }

    public void saveOutStock(MachineOutStockCommand outStockCmd) {
        // 更新物料数量，取出后还有则更新，没有就删除
        List<MachineOutStockCommand.OutStockMaterial> outStockMaterialList = outStockCmd.getMaterialList();
        for (MachineOutStockCommand.OutStockMaterial outStockMaterial : outStockMaterialList) {
            QueryWrapper<MachineStock> queryWrapper = Wrappers.query(MachineStock.builder().warehouseId(outStockCmd.getWarehouse().getId())
                    .materialId(outStockMaterial.getMaterial().getId()).build());
            if (StrUtil.isEmpty(outStockMaterial.getProjectCode())) {
                queryWrapper.isNull("project_code");
            } else {
                queryWrapper.eq("project_code", outStockMaterial.getProjectCode());
            }
            MachineStock existStock = stockMapper.getOneOnly(queryWrapper);
            Long stockNum = existStock == null ? 0 : existStock.getNumber();
            // 库存不足且不强制出库抛出错误
            if (!outStockCmd.isForceOut() && (Objects.isNull(existStock) || existStock.getNumber() < outStockMaterial.getNumber())) {
                log.warn("Stock not enough, id {}", outStockMaterial.getMaterial().getCode());
                String msg = StrUtil.format("零件号/版本：{}/{}，零件名称：{}，库存数量：{}，出库数量：{};",
                        outStockMaterial.getMaterial().getCode(), outStockMaterial.getMaterial().getVersion(), outStockMaterial.getMaterial().getName(),
                        stockNum, outStockMaterial.getNumber());
                throw new ServiceException(BizError.E23002, msg);
            }
            // 若取出后库存为0，则删除库存；若取出后库存有剩余，则更新库存
            long numberAfterOperation = stockNum - outStockMaterial.getNumber();
            if (numberAfterOperation <= 0 && Objects.nonNull(existStock)) {
                stockMapper.deleteById(existStock);
            }
            if (numberAfterOperation > 0 && Objects.nonNull(existStock)) {
                MachineStock stockUpdateEntity = MachineStock.builder().id(existStock.getId()).number(numberAfterOperation).build();
                stockMapper.updateById(stockUpdateEntity);
            }
        }
    }

    public List<MachinePartStockR> listStock(MachinePartStockQuery query) {
        return stockMapper.listStock(query);
    }

    public List<MachinePartStockR> listRealStock(MachineRealStockQuery query) {
        return stockMapper.listRealStock(query);
    }

    public void updateStock(String projectCode, Long warehouseId, Long materialId, Long changeNumber) {
        MachineStock machineStock = stockMapper.getOneOnly(MachineStock.builder().projectCode(projectCode).materialId(materialId).warehouseId(warehouseId).build());
        if (Objects.isNull(machineStock) && changeNumber > 0) {
            // 新增库存
            MachineStock insertStock = MachineStock.builder().projectCode(projectCode).materialId(materialId).warehouseId(warehouseId).number(changeNumber).build();
            stockMapper.insert(insertStock);
        }
        if (Objects.nonNull(machineStock)) {
            if (changeNumber == null || changeNumber == 0) {
                stockMapper.deleteById(machineStock);
            } else {
                // 更新库存
                machineStock.setNumber(changeNumber);
                stockMapper.updateById(machineStock);
            }
        }

    }

    public void deleteStock(MachineOutStockCommand outStockCmd) {
        List<MachineOutStockCommand.OutStockMaterial> outStockMaterialList = outStockCmd.getMaterialList();
        for (MachineOutStockCommand.OutStockMaterial outStockMaterial : outStockMaterialList) {
            MachineStock stockSelectEntity = MachineStock.builder().warehouseId(outStockCmd.getWarehouse().getId())
                    .materialId(outStockMaterial.getMaterial().getId()).build();
            MachineStock existStock = stockMapper.getOneOnly(Wrappers.query(stockSelectEntity));
            if (Objects.nonNull(existStock)) {
                long numberAfter = existStock.getNumber() - outStockMaterial.getNumber();
                if (numberAfter > 0) {
                    existStock.setNumber(numberAfter);
                    stockMapper.updateById(existStock);
                } else {
                    stockMapper.deleteById(existStock);
                }
            }
        }
    }
}
