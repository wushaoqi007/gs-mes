package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.ces.application.dto.query.RequisitionFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.RequisitionItemResult;
import com.greenstone.mes.ces.domain.converter.RequisitionConverter;
import com.greenstone.mes.ces.domain.entity.Requisition;
import com.greenstone.mes.ces.domain.entity.RequisitionItem;
import com.greenstone.mes.ces.dto.cmd.RequisitionStatusChangeCmd;
import com.greenstone.mes.ces.infrastructure.mapper.RequisitionItemMapper;
import com.greenstone.mes.ces.infrastructure.mapper.RequisitionMapper;
import com.greenstone.mes.ces.infrastructure.persistence.RequisitionDO;
import com.greenstone.mes.ces.infrastructure.persistence.RequisitionItemDO;
import com.greenstone.mes.common.core.enums.RequisitionError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RequisitionRepository {
    private final RequisitionMapper requisitionMapper;
    private final RequisitionItemMapper itemMapper;
    private final RequisitionConverter converter;


    public Requisition get(String serialNo) {
        return converter.toRequisition(requisitionMapper.getOneOnly(RequisitionDO.builder().serialNo(serialNo).build()));
    }

    public RequisitionItem getItemById(String id) {
        return converter.toRequisitionItem(itemMapper.getOneOnly(RequisitionItemDO.builder().id(id).build()));
    }

    public void statusChange(RequisitionStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<RequisitionDO> updateWrapper = Wrappers.lambdaUpdate(RequisitionDO.class).set(RequisitionDO::getStatus, statusChangeCmd.getStatus())
                .in(RequisitionDO::getSerialNo, statusChangeCmd.getSerialNos());
        requisitionMapper.update(updateWrapper);
    }

    public void changeStatus(Requisition requisition) {
        LambdaUpdateWrapper<RequisitionDO> updateWrapper = Wrappers.lambdaUpdate(RequisitionDO.class)
                .eq(RequisitionDO::getSerialNo, requisition.getSerialNo())
                .set(RequisitionDO::getStatus, requisition.getStatus());
        requisitionMapper.update(updateWrapper);
    }

    public Requisition detail(String serialNo) {
        RequisitionDO select = RequisitionDO.builder().serialNo(serialNo).build();
        RequisitionDO requisitionDO = requisitionMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(RequisitionError.E140101);
        }
        List<RequisitionItemDO> itemDOS = itemMapper.list(RequisitionItemDO.builder().serialNo(serialNo).build());
        return converter.toRequisition(requisitionDO, itemDOS);
    }

    public List<Requisition> list(RequisitionFuzzyQuery fuzzyQuery) {
        QueryWrapper<RequisitionDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        if (Objects.nonNull(fuzzyQuery.getStatus())) {
            fuzzyQueryWrapper.eq("status", fuzzyQuery.getStatus());
        }
        List<Requisition> requisitions = new ArrayList<>();
        List<RequisitionDO> requisitionDOS = requisitionMapper.selectList(fuzzyQueryWrapper);
        for (RequisitionDO requisitionDO : requisitionDOS) {
            List<RequisitionItemDO> itemDOS = itemMapper.list(RequisitionItemDO.builder().serialNo(requisitionDO.getSerialNo()).build());
            requisitions.add(converter.toRequisition(requisitionDO, itemDOS));
        }
        return requisitions;
    }

    public void add(Requisition requisition) {
        RequisitionDO requisitionDO = converter.toRequisitionDO(requisition);
        List<RequisitionItemDO> itemDOS = converter.toRequisitionItemDOs(requisition.getItems());
        requisitionMapper.insert(requisitionDO);
        for (RequisitionItemDO itemDO : itemDOS) {
            itemDO.setSerialNo(requisitionDO.getSerialNo());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void edit(Requisition requisition) {
        RequisitionDO requisitionDO = converter.toRequisitionDO(requisition);
        List<RequisitionItemDO> itemDOS = converter.toRequisitionItemDOs(requisition.getItems());

        requisitionMapper.update(requisitionDO, Wrappers.lambdaQuery(RequisitionDO.class).eq(RequisitionDO::getSerialNo, requisitionDO.getSerialNo()));
        itemMapper.delete(RequisitionItemDO.builder().serialNo(requisitionDO.getSerialNo()).build());
        for (RequisitionItemDO itemDO : itemDOS) {
            itemDO.setSerialNo(requisitionDO.getSerialNo());
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            RequisitionDO appFound = requisitionMapper.getOneOnly(RequisitionDO.builder().serialNo(serialNo).build());
            if (appFound == null) {
                throw new ServiceException(RequisitionError.E140101);
            }
            if (appFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(RequisitionError.E140102);
            }
        }

        LambdaQueryWrapper<RequisitionDO> appWrapper = Wrappers.lambdaQuery(RequisitionDO.class).in(RequisitionDO::getSerialNo, serialNos);
        requisitionMapper.delete(appWrapper);
        LambdaQueryWrapper<RequisitionItemDO> itemWrapper = Wrappers.lambdaQuery(RequisitionItemDO.class).in(RequisitionItemDO::getSerialNo,
                serialNos);
        itemMapper.delete(itemWrapper);
    }

    public List<RequisitionItemResult> listItem(RequisitionFuzzyQuery query) {
        return requisitionMapper.listItem(query);
    }

    public void updateByCesReturn(RequisitionItem item) {
        itemMapper.updateById(converter.toRequisitionItemDO(item));
    }
}
