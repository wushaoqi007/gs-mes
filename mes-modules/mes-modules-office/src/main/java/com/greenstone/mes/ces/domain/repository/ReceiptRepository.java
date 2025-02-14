package com.greenstone.mes.ces.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.ces.application.dto.query.ReceiptFuzzyQuery;
import com.greenstone.mes.ces.domain.converter.ReceiptConverter;
import com.greenstone.mes.ces.domain.entity.ItemSpec;
import com.greenstone.mes.ces.domain.entity.Receipt;
import com.greenstone.mes.ces.domain.entity.ReceiptItem;
import com.greenstone.mes.ces.domain.entity.Warehouse;
import com.greenstone.mes.ces.dto.cmd.ReceiptStatusChangeCmd;
import com.greenstone.mes.ces.infrastructure.mapper.OrderItemMapper;
import com.greenstone.mes.ces.infrastructure.mapper.ReceiptItemMapper;
import com.greenstone.mes.ces.infrastructure.mapper.ReceiptMapper;
import com.greenstone.mes.ces.infrastructure.persistence.OrderItemDO;
import com.greenstone.mes.ces.infrastructure.persistence.ReceiptDO;
import com.greenstone.mes.ces.infrastructure.persistence.ReceiptItemDO;
import com.greenstone.mes.common.core.enums.ReceiptError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-05-25-15:04
 */
@Service
@AllArgsConstructor
public class ReceiptRepository {
    private final ReceiptMapper receiptMapper;
    private final ReceiptItemMapper itemMapper;
    private final ItemSpecRepository itemSpecRepository;
    private final ReceiptConverter converter;
    private final OrderItemMapper orderItemMapper;
    private final WarehouseRepository warehouseRepository;
    private final RemoteSystemService systemService;


    public Receipt get(String serialNo) {
        return converter.toReceipt(receiptMapper.getOneOnly(ReceiptDO.builder().serialNo(serialNo).build()));
    }

    public void statusChange(ReceiptStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<ReceiptDO> updateWrapper = Wrappers.lambdaUpdate(ReceiptDO.class).set(ReceiptDO::getStatus, statusChangeCmd.getStatus())
                .in(ReceiptDO::getSerialNo, statusChangeCmd.getSerialNos());
        receiptMapper.update(updateWrapper);
    }

    public void changeStatus(Receipt receipt) {
        LambdaUpdateWrapper<ReceiptDO> updateWrapper = Wrappers.lambdaUpdate(ReceiptDO.class)
                .eq(ReceiptDO::getSerialNo, receipt.getSerialNo())
                .set(ReceiptDO::getStatus, receipt.getStatus());
        receiptMapper.update(updateWrapper);
    }

    public Receipt detail(String serialNo) {
        ReceiptDO select = ReceiptDO.builder().serialNo(serialNo).build();
        ReceiptDO receiptDO = receiptMapper.getOneOnly(select);
        if (receiptDO == null) {
            throw new ServiceException("选择的采购订单不存在，请重新选择");
        }
        List<ReceiptItemDO> itemDOS = itemMapper.list(ReceiptItemDO.builder().serialNo(serialNo).build());
        Receipt receipt = converter.toReceipt(receiptDO, itemDOS);
        setOtherAttr(receipt);
        return receipt;
    }

    public List<Receipt> list(ReceiptFuzzyQuery fuzzyQuery) {
        QueryWrapper<ReceiptDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<Receipt> receipts = new ArrayList<>();
        List<ReceiptDO> receiptDOS = receiptMapper.selectList(fuzzyQueryWrapper);
        for (ReceiptDO receiptDO : receiptDOS) {
            List<ReceiptItemDO> itemDOS = itemMapper.list(ReceiptItemDO.builder().serialNo(receiptDO.getSerialNo()).build());
            Receipt receipt = converter.toReceipt(receiptDO, itemDOS);
            setOtherAttr(receipt);
            receipts.add(receipt);
        }
        return receipts;
    }

    public void setOtherAttr(Receipt receipt) {
        for (ReceiptItem item : receipt.getItems()) {
            if (item.getItemCode() != null) {
                ItemSpec itemSpec = itemSpecRepository.detail(item.getItemCode());
                if (itemSpec != null) {
                    item.setTypeName(itemSpec.getTypeName());
                }
            }
            if (item.getWarehouseCode() != null) {
                Warehouse warehouse = warehouseRepository.selectByWarehouseCode(item.getWarehouseCode());
                if (warehouse != null) {
                    item.setWarehouseName(warehouse.getWarehouseName());
                }
            }
        }
    }

    public void add(Receipt receipt) {
        ReceiptDO receiptDO = converter.toReceiptDO(receipt);
        List<ReceiptItemDO> itemDOS = converter.toReceiptItemDOs(receipt.getItems());
        receiptMapper.insert(receiptDO);
        for (ReceiptItemDO itemDO : itemDOS) {
            itemDO.setSerialNo(receiptDO.getSerialNo());
            setCesApplicationId(itemDO);
        }
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void setCesApplicationId(ReceiptItemDO itemDO) {
        if (Objects.nonNull(itemDO.getOrderItemId())) {
            OrderItemDO orderItemDO = orderItemMapper.getOneOnly(OrderItemDO.builder().id(itemDO.getOrderItemId()).build());
            if (Objects.nonNull(orderItemDO.getApplicationItemId())) {
                itemDO.setApplicationItemId(orderItemDO.getApplicationItemId());
            }
        }
    }

    public void edit(Receipt receipt) {
        ReceiptDO receiptDO = converter.toReceiptDO(receipt);
        List<ReceiptItemDO> itemDOS = converter.toReceiptItemDOs(receipt.getItems());
        for (ReceiptItemDO itemDO : itemDOS) {
            setCesApplicationId(itemDO);
        }
        receiptMapper.update(receiptDO, Wrappers.lambdaQuery(ReceiptDO.class).eq(ReceiptDO::getSerialNo, receiptDO.getSerialNo()));
        itemMapper.delete(ReceiptItemDO.builder().serialNo(receiptDO.getSerialNo()).build());
        itemMapper.insertBatchSomeColumn(itemDOS);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            ReceiptDO appFound = receiptMapper.getOneOnly(ReceiptDO.builder().serialNo(serialNo).build());
            if (appFound == null) {
                throw new ServiceException(ReceiptError.E100101);
            }
            if (appFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(ReceiptError.E100102);
            }
        }

        LambdaQueryWrapper<ReceiptDO> appWrapper = Wrappers.lambdaQuery(ReceiptDO.class).in(ReceiptDO::getSerialNo, serialNos);
        receiptMapper.delete(appWrapper);
        LambdaQueryWrapper<ReceiptItemDO> itemWrapper = Wrappers.lambdaQuery(ReceiptItemDO.class).in(ReceiptItemDO::getSerialNo,
                serialNos);
        itemMapper.delete(itemWrapper);
    }
}
