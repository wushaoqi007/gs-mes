package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.ces.application.dto.query.CesReturnFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.CesReturnItemResult;
import com.greenstone.mes.ces.domain.converter.CesReturnConverter;
import com.greenstone.mes.ces.domain.entity.CesReturn;
import com.greenstone.mes.ces.infrastructure.mapper.CesReturnItemMapper;
import com.greenstone.mes.ces.infrastructure.mapper.CesReturnMapper;
import com.greenstone.mes.ces.infrastructure.persistence.CesReturnDO;
import com.greenstone.mes.ces.infrastructure.persistence.CesReturnItemDO;
import com.greenstone.mes.common.core.enums.CesReturnError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CesReturnRepository {
    private final CesReturnMapper cesReturnMapper;
    private final CesReturnItemMapper itemMapper;
    private final CesReturnConverter converter;


    public CesReturn get(String serialNo) {
        return converter.toCesReturn(cesReturnMapper.getOneOnly(CesReturnDO.builder().serialNo(serialNo).build()));
    }

    public CesReturn detail(String serialNo) {
        CesReturnDO select = CesReturnDO.builder().serialNo(serialNo).build();
        CesReturnDO cesReturnDO = cesReturnMapper.getOneOnly(select);
        if (cesReturnDO == null) {
            throw new ServiceException(CesReturnError.E150101);
        }
        List<CesReturnItemDO> itemDOS = itemMapper.list(CesReturnItemDO.builder().serialNo(serialNo).build());
        return converter.toCesReturn(cesReturnDO, itemDOS);
    }

    public List<CesReturn> list(CesReturnFuzzyQuery fuzzyQuery) {
        QueryWrapper<CesReturnDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        List<CesReturn> cesReturns = new ArrayList<>();
        List<CesReturnDO> cesReturnDOS = cesReturnMapper.selectList(fuzzyQueryWrapper);
        for (CesReturnDO cesReturnDO : cesReturnDOS) {
            List<CesReturnItemDO> itemDOS = itemMapper.list(CesReturnItemDO.builder().serialNo(cesReturnDO.getSerialNo()).build());
            cesReturns.add(converter.toCesReturn(cesReturnDO, itemDOS));
        }
        return cesReturns;
    }

    public void add(CesReturn cesReturn) {
        CesReturnDO cesReturnDO = converter.toCesReturnDO(cesReturn);
        List<CesReturnItemDO> itemDOS = converter.toCesReturnItemDOs(cesReturn.getItems());
        cesReturnMapper.insert(cesReturnDO);
        for (CesReturnItemDO itemDO : itemDOS) {
            itemDO.setSerialNo(cesReturnDO.getSerialNo());
            itemDO.setReturnNum(itemDO.getReturnNum() == null ? 0 : itemDO.getReturnNum());
            itemDO.setLossNum(itemDO.getLossNum() == null ? 0 : itemDO.getLossNum());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void edit(CesReturn cesReturn) {
        CesReturnDO cesReturnDO = converter.toCesReturnDO(cesReturn);
        List<CesReturnItemDO> itemDOS = converter.toCesReturnItemDOs(cesReturn.getItems());

        cesReturnMapper.update(cesReturnDO, Wrappers.lambdaQuery(CesReturnDO.class).eq(CesReturnDO::getSerialNo, cesReturnDO.getSerialNo()));
        itemMapper.delete(CesReturnItemDO.builder().serialNo(cesReturnDO.getSerialNo()).build());
        for (CesReturnItemDO itemDO : itemDOS) {
            itemDO.setSerialNo(cesReturnDO.getSerialNo());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            CesReturnDO appFound = cesReturnMapper.getOneOnly(CesReturnDO.builder().serialNo(serialNo).build());
            if (appFound == null) {
                throw new ServiceException(CesReturnError.E150101);
            }
        }

        LambdaQueryWrapper<CesReturnDO> appWrapper = Wrappers.lambdaQuery(CesReturnDO.class).in(CesReturnDO::getSerialNo, serialNos);
        cesReturnMapper.delete(appWrapper);
        LambdaQueryWrapper<CesReturnItemDO> itemWrapper = Wrappers.lambdaQuery(CesReturnItemDO.class).in(CesReturnItemDO::getSerialNo,
                serialNos);
        itemMapper.delete(itemWrapper);
    }

    public List<CesReturnItemResult> listItem(CesReturnFuzzyQuery query) {
        return cesReturnMapper.listItem(query);
    }
}
