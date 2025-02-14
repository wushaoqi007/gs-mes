package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.WarehouseError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.ces.application.dto.query.WarehouseFuzzyQuery;
import com.greenstone.mes.ces.domain.converter.WarehouseConverter;
import com.greenstone.mes.ces.domain.entity.Warehouse;
import com.greenstone.mes.ces.enums.WarehouseStatus;
import com.greenstone.mes.ces.infrastructure.mapper.WarehouseMapper;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-06-01-10:33
 */
@Slf4j
@Service
public class WarehouseRepository {
    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private WarehouseConverter warehouseConverter;

    public List<Warehouse> listByParentCode(String parentWarehouseCode) {
        List<WarehouseDO> doList;
        WarehouseDO selectDO = WarehouseDO.builder().build();
        if (StrUtil.isNotEmpty(parentWarehouseCode)) {
            selectDO.setParentWarehouseCode(parentWarehouseCode);
        }
        doList = warehouseMapper.list(selectDO);
        return warehouseConverter.toWarehouseList(doList);
    }

    public void save(Warehouse warehouse) {
        boolean isNewOne = warehouse.getId() == null;
        WarehouseDO warehouseDO = warehouseConverter.toWarehouseDO(warehouse);
        // 新增仓库
        if (isNewOne) {
            warehouse.setStatus(WarehouseStatus.NORMAL);
            warehouseMapper.insert(warehouseDO);
            warehouse.setId(warehouseDO.getId());
        }
        // 更新仓库
        else {
            // 校验：修改的数据是否存在
            if (Objects.isNull(warehouseMapper.selectById(warehouse.getId()))) {
                throw new ServiceException(WarehouseError.E110101);
            }
            warehouseMapper.updateById(warehouseDO);
        }
    }

    public void remove(Long id) {
        warehouseMapper.deleteById(id);
    }

    public void moveTo(String fromParentCode, String toParentCode) {
        LambdaUpdateWrapper<WarehouseDO> updateWrapper =
                Wrappers.lambdaUpdate(WarehouseDO.class).eq(WarehouseDO::getParentWarehouseCode, fromParentCode)
                        .set(WarehouseDO::getParentWarehouseCode, toParentCode);
        warehouseMapper.update(updateWrapper);
    }

    public boolean existByWarehouseCode(String warehouseCode) {
        return warehouseMapper.exists(WarehouseDO.builder().warehouseCode(warehouseCode).build());
    }

    public Warehouse selectByWarehouseCode(String warehouseCode) {
        return warehouseConverter.toWarehouse(warehouseMapper.getOneOnly(WarehouseDO.builder().warehouseCode(warehouseCode).build()));
    }

    public List<Warehouse> list(WarehouseFuzzyQuery fuzzyQuery) {
        QueryWrapper<WarehouseDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        if (StrUtil.isNotEmpty(fuzzyQuery.getParentWarehouseCode())) {
            fuzzyQueryWrapper.eq("parent_warehouse_code", fuzzyQuery.getParentWarehouseCode());
        }
        List<WarehouseDO> doList = warehouseMapper.selectList(fuzzyQueryWrapper);
        List<Warehouse> warehouseList = warehouseConverter.toWarehouseList(doList);
        for (Warehouse warehouse : warehouseList) {
            if (warehouse.getParentWarehouseCode() != null) {
                Warehouse parentWarehouse = selectByWarehouseCode(warehouse.getParentWarehouseCode());
                if (parentWarehouse != null) {
                    warehouse.setParentWarehouseName(parentWarehouse.getWarehouseName());
                }
            }
        }
        return warehouseList;
    }

    public void updateChildren(List<Warehouse> children) {
        for (Warehouse child : children) {
            warehouseMapper.updateById(warehouseConverter.toWarehouseDO(child));
        }
    }

    public void removeById(Long id) {
        warehouseMapper.deleteById(id);
    }

    public Warehouse getById(Long id) {
        return warehouseConverter.toWarehouse(warehouseMapper.selectById(id));
    }

    public boolean haveChildren(String warehouseCode) {
        return null != warehouseMapper.getOneOnly(WarehouseDO.builder().parentWarehouseCode(warehouseCode).build());
    }

    public Long countChildren(String warehouseCode) {
        return warehouseMapper.selectCount(WarehouseDO.builder().parentWarehouseCode(warehouseCode).build());
    }

    public List<Warehouse> listChildren(String warehouseCode) {
        List<WarehouseDO> children = warehouseMapper.list(WarehouseDO.builder().parentWarehouseCode(warehouseCode).build());
        return warehouseConverter.toWarehouseList(children);
    }
}
