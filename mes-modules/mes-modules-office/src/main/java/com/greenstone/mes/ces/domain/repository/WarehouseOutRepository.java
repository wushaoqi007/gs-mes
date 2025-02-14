package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.WarehouseIOError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.ces.application.dto.query.WarehouseIOFuzzyQuery;
import com.greenstone.mes.ces.domain.converter.WarehouseOutConverter;
import com.greenstone.mes.ces.domain.entity.Warehouse;
import com.greenstone.mes.ces.domain.entity.WarehouseOut;
import com.greenstone.mes.ces.dto.cmd.WarehouseIOStatusChangeCmd;
import com.greenstone.mes.ces.infrastructure.mapper.WarehouseOutDetailMapper;
import com.greenstone.mes.ces.infrastructure.mapper.WarehouseOutMapper;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseOutDO;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseOutDetailDO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-06-5-10:04
 */
@Service
@AllArgsConstructor
public class WarehouseOutRepository {
    private final WarehouseOutMapper warehouseOutMapper;
    private final WarehouseOutDetailMapper itemMapper;
    private final WarehouseOutConverter converter;
    private final WarehouseRepository warehouseRepository;


    public WarehouseOut get(String serialNo) {
        return converter.toWarehouseOut(warehouseOutMapper.getOneOnly(WarehouseOutDO.builder().serialNo(serialNo).build()));
    }

    public void statusChange(WarehouseIOStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<WarehouseOutDO> updateWrapper = Wrappers.lambdaUpdate(WarehouseOutDO.class).set(WarehouseOutDO::getStatus, statusChangeCmd.getStatus())
                .in(WarehouseOutDO::getSerialNo, statusChangeCmd.getSerialNos());
        warehouseOutMapper.update(updateWrapper);
    }

    public void changeStatus(WarehouseOut warehouseOut) {
        LambdaUpdateWrapper<WarehouseOutDO> updateWrapper = Wrappers.lambdaUpdate(WarehouseOutDO.class)
                .eq(WarehouseOutDO::getSerialNo, warehouseOut.getSerialNo())
                .set(WarehouseOutDO::getStatus, warehouseOut.getStatus());
        warehouseOutMapper.update(updateWrapper);
    }

    public WarehouseOut detail(String serialNo) {
        WarehouseOutDO select = WarehouseOutDO.builder().serialNo(serialNo).build();
        WarehouseOutDO warehouseOutDO = warehouseOutMapper.getOneOnly(select);
        if (warehouseOutDO == null) {
            throw new ServiceException("选择的出库单不存在，请重新选择");
        }
        List<WarehouseOutDetailDO> itemDOS = itemMapper.list(WarehouseOutDetailDO.builder().serialNo(serialNo).build());
        WarehouseOut warehouseOut = converter.toWarehouseOut(warehouseOutDO, itemDOS);
        Warehouse warehouse = warehouseRepository.selectByWarehouseCode(warehouseOut.getWarehouseCode());
        warehouseOut.setWarehouseName(warehouse.getWarehouseName());
        return warehouseOut;
    }

    public List<WarehouseOut> list(WarehouseIOFuzzyQuery fuzzyQuery) {
        QueryWrapper<WarehouseOutDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        if (Objects.nonNull(fuzzyQuery.getState())) {
            fuzzyQueryWrapper.eq("state", fuzzyQuery.getState());
        }
        List<WarehouseOut> warehouseOuts = new ArrayList<>();
        List<WarehouseOutDO> warehouseOutDOS = warehouseOutMapper.selectList(fuzzyQueryWrapper);
        for (WarehouseOutDO warehouseOutDO : warehouseOutDOS) {
            List<WarehouseOutDetailDO> itemDOS = itemMapper.list(WarehouseOutDetailDO.builder().serialNo(warehouseOutDO.getSerialNo()).build());
            WarehouseOut warehouseOut = converter.toWarehouseOut(warehouseOutDO, itemDOS);
            Warehouse warehouse = warehouseRepository.selectByWarehouseCode(warehouseOut.getWarehouseCode());
            warehouseOut.setWarehouseName(warehouse.getWarehouseName());
            warehouseOuts.add(warehouseOut);
        }
        return warehouseOuts;
    }

    public void add(WarehouseOut warehouseOut) {
        WarehouseOutDO warehouseOutDO = converter.toWarehouseOutDO(warehouseOut);
        List<WarehouseOutDetailDO> itemDOS = converter.toWarehouseOutDetailDOs(warehouseOut.getItems());
        warehouseOutMapper.insert(warehouseOutDO);
        for (WarehouseOutDetailDO itemDO : itemDOS) {
            itemDO.setSerialNo(warehouseOutDO.getSerialNo());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void edit(WarehouseOut warehouseOut) {
        WarehouseOutDO warehouseOutDO = converter.toWarehouseOutDO(warehouseOut);
        List<WarehouseOutDetailDO> itemDOS = converter.toWarehouseOutDetailDOs(warehouseOut.getItems());

        warehouseOutMapper.update(warehouseOutDO, Wrappers.lambdaQuery(WarehouseOutDO.class).eq(WarehouseOutDO::getSerialNo, warehouseOutDO.getSerialNo()));
        itemMapper.delete(WarehouseOutDetailDO.builder().serialNo(warehouseOutDO.getSerialNo()).build());
        for (WarehouseOutDetailDO itemDO : itemDOS) {
            itemDO.setSerialNo(warehouseOutDO.getSerialNo());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            WarehouseOutDO appFound = warehouseOutMapper.getOneOnly(WarehouseOutDO.builder().serialNo(serialNo).build());
            if (appFound == null) {
                throw new ServiceException(WarehouseIOError.E120101);
            }
            if (appFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(WarehouseIOError.E120102);
            }
        }

        LambdaQueryWrapper<WarehouseOutDO> appWrapper = Wrappers.lambdaQuery(WarehouseOutDO.class).in(WarehouseOutDO::getSerialNo, serialNos);
        warehouseOutMapper.delete(appWrapper);
        LambdaQueryWrapper<WarehouseOutDetailDO> itemWrapper = Wrappers.lambdaQuery(WarehouseOutDetailDO.class).in(WarehouseOutDetailDO::getSerialNo,
                serialNos);
        itemMapper.delete(itemWrapper);
    }

    public void moveTo(String fromWarehouseCode, String toWarehouseCode) {
        LambdaUpdateWrapper<WarehouseOutDO> updateWrapper =
                Wrappers.lambdaUpdate(WarehouseOutDO.class).eq(WarehouseOutDO::getWarehouseCode, fromWarehouseCode).
                        set(WarehouseOutDO::getWarehouseCode, toWarehouseCode);
        warehouseOutMapper.update(updateWrapper);
    }
}
