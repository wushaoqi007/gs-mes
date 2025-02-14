package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderProgressQuery;
import com.greenstone.mes.machine.application.dto.result.MachineOrderExportR;
import com.greenstone.mes.machine.application.dto.result.MachineOrderProgressResult;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.domain.converter.MachineOrderConverter;
import com.greenstone.mes.machine.domain.entity.MachineOrder;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.infrastructure.mapper.MachineOrderAttachmentMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineOrderDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineOrderMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderAttachmentDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineOrderDetailDO;
import com.greenstone.mes.table.core.AbstractTableRepository;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2024-11-25-10:43
 */
@Slf4j
@Service
public class MachineOrderRepository extends AbstractTableRepository<MachineOrder, MachineOrderDO, MachineOrderMapper> {
    private final MachineOrderConverter converter;
    private final MachineOrderDetailMapper detailMapper;
    private final MachineOrderAttachmentMapper attachmentMapper;
    private final MachineHelper machineHelper;

    public MachineOrderRepository(MachineOrderMapper mapper, MachineOrderConverter converter,
                                  MachineOrderDetailMapper detailMapper, MachineOrderAttachmentMapper attachmentMapper, MachineHelper machineHelper) {
        super(mapper);
        this.converter = converter;
        this.detailMapper = detailMapper;
        this.attachmentMapper = attachmentMapper;
        this.machineHelper = machineHelper;
    }

    public MachineOrder getBySerialNo(String serialNo) {
        QueryWrapper<MachineOrderDO> query = Wrappers.query(MachineOrderDO.builder().serialNo(serialNo).build());
        MachineOrder entity = converter.do2Entity(mapper.getOneOnly(query));
        setDetail(entity);
        calTotal(entity);
        return entity;
    }

    @Override
    public MachineOrder getEntity(Long id) {
        MachineOrderDO machineOrderDO = mapper.selectById(id);
        MachineOrder machineOrder = converter.do2Entity(machineOrderDO);
        setDetail(machineOrder);
        calTotal(machineOrder);
        return machineOrder;
    }

    private void calTotal(MachineOrder machineOrder) {
        Long totalProcess = machineOrder.getParts().stream().mapToLong(n -> n.getProcessNumber() == null ? 0 : n.getProcessNumber()).sum();
        machineOrder.setTotalProcess(totalProcess);
        double totalPrice = machineOrder.getParts().stream().mapToDouble(n -> n.getTotalPrice() == null ? 0 : n.getTotalPrice().doubleValue()).sum();
        BigDecimal tp = BigDecimal.valueOf(totalPrice);
        machineOrder.setTotalPrice(tp.setScale(2, RoundingMode.HALF_UP));
        Long totalReceived = machineOrder.getParts().stream().mapToLong(n -> n.getReceivedNumber() == null ? 0 : n.getReceivedNumber()).sum();
        machineOrder.setTotalReceived(totalReceived);
    }

    @Override
    public List<MachineOrder> getEntities(MachineOrder machineOrder) {
        LambdaQueryWrapper<MachineOrderDO> lambdaQuery = Wrappers.lambdaQuery(MachineOrderDO.class);
        lambdaQuery.eq(StrUtil.isNotBlank(machineOrder.getSerialNo()), MachineOrderDO::getSerialNo, machineOrder.getSerialNo());
        lambdaQuery.eq(machineOrder.getSpecial() != null, MachineOrderDO::getSpecial, machineOrder.getSpecial());
        lambdaQuery.like(StrUtil.isNotEmpty(machineOrder.getProvider()), MachineOrderDO::getProvider, machineOrder.getProvider());
        lambdaQuery.ne(MachineOrderDO::getDataStatus, TableConst.DataStatus.DRAFT);
        if (CollUtil.isNotEmpty(machineOrder.getParams())) {
            log.info("getEntities params:{}", machineOrder.getParams());
            Map<String, Object> params = machineOrder.getParams();
            List<Integer> statusList = MachineHelper.ObjectToList(params.get("processStatus"));
            lambdaQuery.in(CollUtil.isNotEmpty(statusList), MachineOrderDO::getProcessStatus, statusList);

            List<String> ids = MachineHelper.ObjectToList(params.get("ids"));
            lambdaQuery.in(CollUtil.isNotEmpty(ids), MachineOrderDO::getId, ids);

            if (params.get("partCode") != null && StrUtil.isNotBlank(params.get("partCode").toString())) {
                lambdaQuery.inSql(MachineOrderDO::getSerialNo,
                        "select serial_no from machine_order_detail d where d.part_code like '%" + params.get("partCode") + "%'");
            }
        }
        lambdaQuery.orderByDesc(MachineOrderDO::getProcessStartTime);
        List<MachineOrder> machineOrders = converter.dos2Entities(mapper.selectByDataScopeLambda(lambdaQuery));
        setDetails(machineOrders);
        for (MachineOrder order : machineOrders) {
            calTotal(order);
        }
        return machineOrders;
    }

    @Override
    public List<MachineOrder> getDrafts() {
        QueryWrapper<MachineOrderDO> query = Wrappers.query();
        query.eq("data_status", TableConst.DataStatus.DRAFT);
        query.eq("create_by", SecurityUtils.getUserId());
        query.orderByDesc("create_time");
        List<MachineOrder> machineOrders = converter.dos2Entities(mapper.selectList(query));
        setDetails(machineOrders);
        for (MachineOrder machineOrder : machineOrders) {
            calTotal(machineOrder);
        }
        return machineOrders;
    }

    @Override
    public MachineOrder insert(MachineOrder machineOrder) {
        MachineOrderDO machineOrderDO = converter.entity2Do(machineOrder);
        mapper.insert(machineOrderDO);
        if (CollUtil.isNotEmpty(machineOrder.getParts())) {
            List<MachineOrderDetailDO> machineOrderDetailDOS = converter.detailEntities2Dos(machineOrder.getParts());
            detailMapper.insertBatchSomeColumn(machineOrderDetailDOS);
        }
        if (CollUtil.isNotEmpty(machineOrder.getAttachments())) {
            List<MachineOrderAttachmentDO> machineOrderAttachmentDOS = converter.attachEntities2Dos(machineOrder.getAttachments());
            attachmentMapper.insertBatchSomeColumn(machineOrderAttachmentDOS);
        }
        MachineOrder entity = getEntity(machineOrderDO.getId());
        entity.setFunctionId(machineOrder.getFunctionId());
        return entity;
    }

    @Override
    public MachineOrder update(MachineOrder machineOrder) {
        MachineOrder oldEntity = getEntity(machineOrder.getId());
        MachineOrderDO machineOrderDO = converter.entity2Do(machineOrder);
        mapper.updateById(machineOrderDO);
        if (CollUtil.isNotEmpty(machineOrder.getParts())) {
            List<MachineOrderDetailDO> insertDetails = new ArrayList<>();
            List<MachineOrderDetailDO> updateDetailS = converter.detailEntities2Dos(machineOrder.getParts());
            for (MachineOrderDetailDO updateDo : updateDetailS) {
                if (updateDo.getId() != null && CollUtil.isNotEmpty(oldEntity.getParts())) {
                    // 除了更新的都要删掉
                    oldEntity.getParts().removeIf(d -> Objects.equals(d.getId(), updateDo.getId()));
                    updateDo.setUpdateBy(SecurityUtils.getUserId());
                    updateDo.setUpdateTime(LocalDateTime.now());
                    detailMapper.updateById(updateDo);
                } else {
                    updateDo.setCreateBy(SecurityUtils.getUserId());
                    updateDo.setCreateTime(LocalDateTime.now());
                    insertDetails.add(updateDo);
                }
            }
            if (CollUtil.isNotEmpty(insertDetails)) {
                detailMapper.insertBatchSomeColumn(insertDetails);
            }
        }
        if (CollUtil.isNotEmpty(machineOrder.getAttachments())) {
            List<MachineOrderAttachmentDO> insertAttachS = new ArrayList<>();
            List<MachineOrderAttachmentDO> updateAttachS = converter.attachEntities2Dos(machineOrder.getAttachments());
            for (MachineOrderAttachmentDO updateDo : updateAttachS) {
                if (updateDo.getId() != null && CollUtil.isNotEmpty(oldEntity.getAttachments())) {
                    // 除了更新的都要删掉
                    oldEntity.getAttachments().removeIf(d -> Objects.equals(d.getId(), updateDo.getId()));
                    updateDo.setUpdateBy(SecurityUtils.getUserId());
                    updateDo.setUpdateTime(LocalDateTime.now());
                    attachmentMapper.updateById(updateDo);
                } else {
                    updateDo.setCreateBy(SecurityUtils.getUserId());
                    updateDo.setCreateTime(LocalDateTime.now());
                    insertAttachS.add(updateDo);
                }
            }
            if (CollUtil.isNotEmpty(insertAttachS)) {
                attachmentMapper.insertBatchSomeColumn(insertAttachS);
            }
        }
        // 删除不需要的
        if (CollUtil.isNotEmpty(oldEntity.getParts())) {
            detailMapper.deleteBatchIds(oldEntity.getParts());
            //  删除零件时，需要删除该零件的待收件库存
            if (oldEntity.getDataStatus() == TableConst.DataStatus.EFFECTIVE) {
                // 撤回库存
                machineHelper.revokeOrderStock(oldEntity.getParts());
            }
        }
        if (CollUtil.isNotEmpty(oldEntity.getAttachments())) {
            attachmentMapper.deleteBatchIds(oldEntity.getAttachments());
        }
        MachineOrder entity = getEntity(machineOrderDO.getId());
        entity.setFunctionId(machineOrder.getFunctionId());
        entity.setChangeReason(machineOrder.getChangeReason());
        return entity;
    }

    @Override
    public void delete(Long id) {
        MachineOrderDO orderDO = mapper.selectById(id);
        if (orderDO != null) {
            mapper.deleteById(id);
            LambdaQueryWrapper<MachineOrderDetailDO> deleteWrapper = Wrappers.lambdaQuery(MachineOrderDetailDO.class)
                    .eq(MachineOrderDetailDO::getSerialNo, orderDO.getSerialNo());
            detailMapper.delete(deleteWrapper);
            LambdaQueryWrapper<MachineOrderAttachmentDO> deleteWrapper2 = Wrappers.lambdaQuery(MachineOrderAttachmentDO.class)
                    .eq(MachineOrderAttachmentDO::getSerialNo, orderDO.getSerialNo());
            attachmentMapper.delete(deleteWrapper2);
        }
    }

    public List<MachineOrderExportR> selectExportDataList(MachineOrderExportQuery query) {
        DateTime parse;
        try {
            parse = DateUtil.parse(query.getMonth(), "yyyy-MM");
        } catch (Exception e) {
            throw new ServiceException("导出月份格式为：yyyy-MM");
        }
        LocalDate timeStart = DateUtil.beginOfMonth(parse).toLocalDateTime().toLocalDate();
        LocalDate timeEnd = DateUtil.endOfMonth(parse).toLocalDateTime().toLocalDate();
        query.setStart(timeStart);
        query.setEnd(timeEnd);

        return mapper.selectExportDataList(query);
    }

    public List<MachineOrderProgressResult> selectOrderDetailList(MachineOrderProgressQuery query) {
        log.info("selectOrderDetailList params:{}", query);
        return detailMapper.selectOrderDetailList(query);
    }

    public List<MachineOrderDetail> selectEffectiveParts(String requirementSerialNo) {
        return detailMapper.selectEffectiveParts(requirementSerialNo);
    }

    public MachineOrderDetail selectEffectivePart(MachineOrderDetail orderDetail) {
        return detailMapper.selectEffectivePart(orderDetail);
    }

    public MachineOrderDO isExist(String serialNo) {
        return mapper.getOneOnly(MachineOrderDO.builder().serialNo(serialNo).build());
    }

    private void setDetails(List<MachineOrder> machineOrders) {
        for (MachineOrder machineOrder : machineOrders) {
            setDetail(machineOrder);
        }
    }

    public void setDetail(MachineOrder machineOrder) {
        if (machineOrder != null) {
            LambdaQueryWrapper<MachineOrderDetailDO> queryWrapper = Wrappers.lambdaQuery(MachineOrderDetailDO.class)
                    .eq(MachineOrderDetailDO::getSerialNo, machineOrder.getSerialNo());
            queryWrapper.orderByDesc(MachineOrderDetailDO::getCreateTime);
            List<MachineOrderDetailDO> machineOrderDetailDOS = detailMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(machineOrderDetailDOS)) {
                machineOrder.setParts(converter.detailDos2Entities(machineOrderDetailDOS));
            }
            LambdaQueryWrapper<MachineOrderAttachmentDO> queryWrapper2 = Wrappers.lambdaQuery(MachineOrderAttachmentDO.class)
                    .eq(MachineOrderAttachmentDO::getSerialNo, machineOrder.getSerialNo());
            queryWrapper2.orderByDesc(MachineOrderAttachmentDO::getCreateTime);
            List<MachineOrderAttachmentDO> machineOrderAttachmentDOS = attachmentMapper.selectList(queryWrapper2);
            if (CollUtil.isNotEmpty(machineOrderAttachmentDOS)) {
                machineOrder.setAttachments(converter.attachDos2Entities(machineOrderAttachmentDOS));
            }
        }
    }
}
