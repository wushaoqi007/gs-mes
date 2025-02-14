
package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.ces.application.dto.query.CesClearFuzzyQuery;
import com.greenstone.mes.ces.domain.converter.CesClearConverter;
import com.greenstone.mes.ces.domain.entity.CesClear;
import com.greenstone.mes.ces.dto.cmd.CesClearStatusChangeCmd;
import com.greenstone.mes.ces.infrastructure.mapper.CesClearItemMapper;
import com.greenstone.mes.ces.infrastructure.mapper.CesClearMapper;
import com.greenstone.mes.ces.infrastructure.persistence.CesClearDO;
import com.greenstone.mes.ces.infrastructure.persistence.CesClearItemDO;
import com.greenstone.mes.common.core.enums.CesClearError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CesClearRepository {
    private final CesClearMapper cesClearMapper;
    private final CesClearItemMapper itemMapper;
    private final CesClearConverter converter;


    public CesClear get(String serialNo) {
        return converter.toCesClear(cesClearMapper.getOneOnly(CesClearDO.builder().serialNo(serialNo).build()));
    }

    public CesClear detail(String serialNo) {
        CesClearDO select = CesClearDO.builder().serialNo(serialNo).build();
        CesClearDO cesClearDO = cesClearMapper.getOneOnly(select);
        if (cesClearDO == null) {
            throw new ServiceException(CesClearError.E160101);
        }
        List<CesClearItemDO> itemDOS = itemMapper.list(CesClearItemDO.builder().serialNo(serialNo).build());
        return converter.toCesClear(cesClearDO, itemDOS);
    }

    public List<CesClear> list(CesClearFuzzyQuery fuzzyQuery) {
        QueryWrapper<CesClearDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        List<CesClear> cesClears = new ArrayList<>();
        List<CesClearDO> cesClearDOS = cesClearMapper.selectList(fuzzyQueryWrapper);
        for (CesClearDO cesClearDO : cesClearDOS) {
            List<CesClearItemDO> itemDOS = itemMapper.list(CesClearItemDO.builder().serialNo(cesClearDO.getSerialNo()).build());
            cesClears.add(converter.toCesClear(cesClearDO, itemDOS));
        }
        return cesClears;
    }

    public void add(CesClear cesClear) {
        CesClearDO cesClearDO = converter.toCesClearDO(cesClear);
        List<CesClearItemDO> itemDOS = converter.toCesClearItemDOs(cesClear.getItems());
        cesClearMapper.insert(cesClearDO);
        for (CesClearItemDO itemDO : itemDOS) {
            itemDO.setSerialNo(cesClearDO.getSerialNo());
            itemDO.setClearNum(itemDO.getClearNum() == null ? 0 : itemDO.getClearNum());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void edit(CesClear cesClear) {
        CesClearDO cesClearDO = converter.toCesClearDO(cesClear);
        List<CesClearItemDO> itemDOS = converter.toCesClearItemDOs(cesClear.getItems());

        cesClearMapper.update(cesClearDO, Wrappers.lambdaQuery(CesClearDO.class).eq(CesClearDO::getSerialNo, cesClearDO.getSerialNo()));
        itemMapper.delete(CesClearItemDO.builder().serialNo(cesClearDO.getSerialNo()).build());
        for (CesClearItemDO itemDO : itemDOS) {
            itemDO.setSerialNo(cesClearDO.getSerialNo());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void statusChange(CesClearStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<CesClearDO> updateWrapper = Wrappers.lambdaUpdate(CesClearDO.class).set(CesClearDO::getStatus, statusChangeCmd.getStatus())
                .in(CesClearDO::getSerialNo, statusChangeCmd.getSerialNos());
        cesClearMapper.update(updateWrapper);
    }

    public void changeStatus(CesClear cesClear) {
        LambdaUpdateWrapper<CesClearDO> updateWrapper = Wrappers.lambdaUpdate(CesClearDO.class)
                .eq(CesClearDO::getSerialNo, cesClear.getSerialNo())
                .set(CesClearDO::getStatus, cesClear.getStatus());
        cesClearMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            CesClearDO appFound = cesClearMapper.getOneOnly(CesClearDO.builder().serialNo(serialNo).build());
            if (appFound == null) {
                throw new ServiceException(CesClearError.E160101);
            }
        }

        LambdaQueryWrapper<CesClearDO> appWrapper = Wrappers.lambdaQuery(CesClearDO.class).in(CesClearDO::getSerialNo, serialNos);
        cesClearMapper.delete(appWrapper);
        LambdaQueryWrapper<CesClearItemDO> itemWrapper = Wrappers.lambdaQuery(CesClearItemDO.class).in(CesClearItemDO::getSerialNo,
                serialNos);
        itemMapper.delete(itemWrapper);
    }

}
