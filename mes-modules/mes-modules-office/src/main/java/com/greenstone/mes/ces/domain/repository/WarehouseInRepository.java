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
import com.greenstone.mes.ces.domain.converter.WarehouseInConverter;
import com.greenstone.mes.ces.domain.entity.Warehouse;
import com.greenstone.mes.ces.domain.entity.WarehouseIn;
import com.greenstone.mes.ces.dto.cmd.WarehouseIOStatusChangeCmd;
import com.greenstone.mes.ces.infrastructure.mapper.WarehouseInDetailMapper;
import com.greenstone.mes.ces.infrastructure.mapper.WarehouseInMapper;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseInDO;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseInDetailDO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-06-2-10:04
 */
@Service
@AllArgsConstructor
public class WarehouseInRepository {
    private final WarehouseInMapper warehouseInMapper;
    private final WarehouseInDetailMapper itemMapper;
    private final WarehouseInConverter converter;
    private final WarehouseRepository warehouseRepository;


    public WarehouseIn get(String serialNo) {
        return converter.toWarehouseIn(warehouseInMapper.getOneOnly(WarehouseInDO.builder().serialNo(serialNo).build()));
    }

    public void statusChange(WarehouseIOStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<WarehouseInDO> updateWrapper = Wrappers.lambdaUpdate(WarehouseInDO.class).set(WarehouseInDO::getStatus, statusChangeCmd.getStatus())
                .in(WarehouseInDO::getSerialNo, statusChangeCmd.getSerialNos());
        warehouseInMapper.update(updateWrapper);
    }

    public void changeStatus(WarehouseIn warehouseIn) {
        LambdaUpdateWrapper<WarehouseInDO> updateWrapper = Wrappers.lambdaUpdate(WarehouseInDO.class)
                .eq(WarehouseInDO::getSerialNo, warehouseIn.getSerialNo())
                .set(WarehouseInDO::getStatus, warehouseIn.getStatus());
        warehouseInMapper.update(updateWrapper);
    }

    public WarehouseIn detail(String serialNo) {
        WarehouseInDO select = WarehouseInDO.builder().serialNo(serialNo).build();
        WarehouseInDO warehouseInDO = warehouseInMapper.getOneOnly(select);
        if (warehouseInDO == null) {
            throw new ServiceException("选择的入库单不存在，请重新选择");
        }
        List<WarehouseInDetailDO> itemDOS = itemMapper.list(WarehouseInDetailDO.builder().serialNo(serialNo).build());
        WarehouseIn warehouseIn = converter.toWarehouseIn(warehouseInDO, itemDOS);
        Warehouse warehouse = warehouseRepository.selectByWarehouseCode(warehouseIn.getWarehouseCode());
        warehouseIn.setWarehouseName(warehouse.getWarehouseName());
        return warehouseIn;
    }

    public List<WarehouseIn> list(WarehouseIOFuzzyQuery fuzzyQuery) {
        QueryWrapper<WarehouseInDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<WarehouseIn> warehouseIns = new ArrayList<>();
        List<WarehouseInDO> warehouseInDOS = warehouseInMapper.selectList(fuzzyQueryWrapper);
        for (WarehouseInDO warehouseInDO : warehouseInDOS) {
            List<WarehouseInDetailDO> itemDOS = itemMapper.list(WarehouseInDetailDO.builder().serialNo(warehouseInDO.getSerialNo()).build());
            WarehouseIn warehouseIn = converter.toWarehouseIn(warehouseInDO, itemDOS);
            Warehouse warehouse = warehouseRepository.selectByWarehouseCode(warehouseIn.getWarehouseCode());
            warehouseIn.setWarehouseName(warehouse.getWarehouseName());
            warehouseIns.add(warehouseIn);
        }
        return warehouseIns;
    }

    public void add(WarehouseIn warehouseIn) {
        WarehouseInDO warehouseInDO = converter.toWarehouseInDO(warehouseIn);
        List<WarehouseInDetailDO> itemDOS = converter.toWarehouseInDetailDOs(warehouseIn.getItems());
        warehouseInMapper.insert(warehouseInDO);
        for (WarehouseInDetailDO itemDO : itemDOS) {
            itemDO.setSerialNo(warehouseInDO.getSerialNo());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void edit(WarehouseIn warehouseIn) {
        WarehouseInDO warehouseInDO = converter.toWarehouseInDO(warehouseIn);
        List<WarehouseInDetailDO> itemDOS = converter.toWarehouseInDetailDOs(warehouseIn.getItems());

        warehouseInMapper.update(warehouseInDO, Wrappers.lambdaQuery(WarehouseInDO.class).eq(WarehouseInDO::getSerialNo, warehouseInDO.getSerialNo()));
        itemMapper.delete(WarehouseInDetailDO.builder().serialNo(warehouseInDO.getSerialNo()).build());
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            WarehouseInDO appFound = warehouseInMapper.getOneOnly(WarehouseInDO.builder().serialNo(serialNo).build());
            if (appFound == null) {
                throw new ServiceException(WarehouseIOError.E120101);
            }
            if (appFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(WarehouseIOError.E120102);
            }
        }

        LambdaQueryWrapper<WarehouseInDO> appWrapper = Wrappers.lambdaQuery(WarehouseInDO.class).in(WarehouseInDO::getSerialNo, serialNos);
        warehouseInMapper.delete(appWrapper);
        LambdaQueryWrapper<WarehouseInDetailDO> itemWrapper = Wrappers.lambdaQuery(WarehouseInDetailDO.class).in(WarehouseInDetailDO::getSerialNo,
                serialNos);
        itemMapper.delete(itemWrapper);
    }

    public void moveTo(String fromWarehouseCode, String toWarehouseCode) {
        LambdaUpdateWrapper<WarehouseInDO> updateWrapper =
                Wrappers.lambdaUpdate(WarehouseInDO.class).eq(WarehouseInDO::getWarehouseCode, fromWarehouseCode).
                        set(WarehouseInDO::getWarehouseCode, toWarehouseCode);
        warehouseInMapper.update(updateWrapper);
    }
}
