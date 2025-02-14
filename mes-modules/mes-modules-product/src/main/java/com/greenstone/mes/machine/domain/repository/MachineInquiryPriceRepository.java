package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartScanQuery;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.domain.converter.MachineInquiryPriceConverter;
import com.greenstone.mes.machine.domain.entity.MachineInquiryPrice;
import com.greenstone.mes.machine.infrastructure.enums.InquiryPriceStatus;
import com.greenstone.mes.machine.infrastructure.mapper.MachineInquiryPriceDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineInquiryPriceMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineInquiryPriceDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineInquiryPriceDetailDO;
import com.greenstone.mes.table.core.AbstractTableRepository;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wsqwork
 * @date 2024/12/31 10:27
 */
@Slf4j
@Service
public class MachineInquiryPriceRepository extends AbstractTableRepository<MachineInquiryPrice, MachineInquiryPriceDO, MachineInquiryPriceMapper> {

    private final MachineInquiryPriceDetailMapper detailMapper;
    private final MachineInquiryPriceConverter converter;

    public MachineInquiryPriceRepository(MachineInquiryPriceMapper mapper, MachineInquiryPriceDetailMapper detailMapper,
                                         MachineInquiryPriceConverter converter) {
        super(mapper);
        this.detailMapper = detailMapper;
        this.converter = converter;
    }

    @Override
    public MachineInquiryPrice getEntity(Long id) {
        MachineInquiryPriceDO machineInquiryPriceDO = mapper.selectById(id);
        MachineInquiryPrice machineInquiryPrice = converter.do2Entity(machineInquiryPriceDO);
        setDetail(machineInquiryPrice);
        return machineInquiryPrice;
    }

    @Override
    public List<MachineInquiryPrice> getEntities(MachineInquiryPrice machineInquiryPrice) {
        LambdaQueryWrapper<MachineInquiryPriceDO> lambdaQuery = Wrappers.lambdaQuery(MachineInquiryPriceDO.class);
        lambdaQuery.eq(StrUtil.isNotBlank(machineInquiryPrice.getSerialNo()), MachineInquiryPriceDO::getSerialNo, machineInquiryPrice.getSerialNo());
        lambdaQuery.ne(MachineInquiryPriceDO::getDataStatus, TableConst.DataStatus.DRAFT);
        if (CollUtil.isNotEmpty(machineInquiryPrice.getParams())) {
            log.info("getEntities params:{}", machineInquiryPrice.getParams());
            Map<String, Object> params = machineInquiryPrice.getParams();
            List<String> ids = MachineHelper.ObjectToList(params.get("ids"));
            lambdaQuery.in(CollUtil.isNotEmpty(ids), MachineInquiryPriceDO::getId, ids);
            if (params.get("partCode") != null && StrUtil.isNotBlank(params.get("partCode").toString())
                    && params.get("requirementSerialNo") != null && StrUtil.isNotBlank(params.get("requirementSerialNo").toString())
                    && params.get("projectCode") != null && StrUtil.isNotBlank(params.get("projectCode").toString())
                    && params.get("partVersion") != null && StrUtil.isNotBlank(params.get("partVersion").toString())) {
                // 扫码搜索
                lambdaQuery.inSql(MachineInquiryPriceDO::getSerialNo,
                        "select serial_no from machine_inquiry_price_detail d where " +
                                " d.requirement_serial_no = '" + params.get("requirementSerialNo") + "'" +
                                " AND d.project_code = '" + params.get("projectCode") + "'" +
                                " AND d.part_code = '" + params.get("partCode") + "'" +
                                " AND d.part_version = '" + params.get("partVersion") + "'");
            } else if (params.get("partCode") != null && StrUtil.isNotBlank(params.get("partCode").toString())) {
                lambdaQuery.inSql(MachineInquiryPriceDO::getSerialNo,
                        "select serial_no from machine_inquiry_price_detail d where d.part_code like '%" + params.get("partCode") + "%'");
            }else if(params.get("projectCode") != null && StrUtil.isNotBlank(params.get("projectCode").toString())){
                lambdaQuery.inSql(MachineInquiryPriceDO::getSerialNo,
                        "select serial_no from machine_inquiry_price_detail d where d.project_code = '" + params.get("projectCode") + "' GROUP BY d.serial_no");
            }
        }
        lambdaQuery.orderByDesc(MachineInquiryPriceDO::getSubmitTime);
        List<MachineInquiryPrice> machineInquiryPrices = converter.dos2Entities(mapper.selectByDataScopeLambda(lambdaQuery));
        setDetails(machineInquiryPrices);
        return machineInquiryPrices;
    }

    @Override
    public List<MachineInquiryPrice> getDrafts() {
        QueryWrapper<MachineInquiryPriceDO> query = Wrappers.query();
        query.eq("data_status", TableConst.DataStatus.DRAFT);
        query.eq("create_by", SecurityUtils.getUserId());
        query.orderByDesc("create_time");
        List<MachineInquiryPrice> inquiryPrices = converter.dos2Entities(mapper.selectList(query));
        setDetails(inquiryPrices);
        return inquiryPrices;
    }

    @Override
    public MachineInquiryPrice insert(MachineInquiryPrice machineInquiryPrice) {
        MachineInquiryPriceDO machineInquiryPriceDO = converter.entity2Do(machineInquiryPrice);
        mapper.insert(machineInquiryPriceDO);
        if (CollUtil.isNotEmpty(machineInquiryPrice.getParts())) {
            List<MachineInquiryPriceDetailDO> machineInquiryPriceDetailDOS = converter.detailEntities2Dos(machineInquiryPrice.getParts());
            detailMapper.insertBatchSomeColumn(machineInquiryPriceDetailDOS);
        }
        MachineInquiryPrice entity = getEntity(machineInquiryPriceDO.getId());
        entity.setFunctionId(machineInquiryPrice.getFunctionId());
        return entity;
    }

    @Override
    public MachineInquiryPrice update(MachineInquiryPrice machineInquiryPrice) {
        MachineInquiryPrice oldEntity = getEntity(machineInquiryPrice.getId());
        MachineInquiryPriceDO machineInquiryPriceDO = converter.entity2Do(machineInquiryPrice);
        mapper.updateById(machineInquiryPriceDO);
        if (CollUtil.isNotEmpty(machineInquiryPrice.getParts())) {
            List<MachineInquiryPriceDetailDO> insertDetails = new ArrayList<>();
            List<MachineInquiryPriceDetailDO> updateDetailS = converter.detailEntities2Dos(machineInquiryPrice.getParts());
            for (MachineInquiryPriceDetailDO updateDo : updateDetailS) {
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
        // 删除不需要的
        if (CollUtil.isNotEmpty(oldEntity.getParts())) {
            detailMapper.deleteBatchIds(oldEntity.getParts());
        }
        MachineInquiryPrice entity = getEntity(machineInquiryPriceDO.getId());
        entity.setFunctionId(machineInquiryPrice.getFunctionId());
        entity.setChangeReason(machineInquiryPrice.getChangeReason());
        return entity;
    }

    @Override
    public void delete(Long id) {
        MachineInquiryPriceDO inquiryPriceDO = mapper.selectById(id);
        if (inquiryPriceDO != null) {
            mapper.deleteById(id);
            LambdaQueryWrapper<MachineInquiryPriceDetailDO> deleteWrapper = Wrappers.lambdaQuery(MachineInquiryPriceDetailDO.class)
                    .eq(MachineInquiryPriceDetailDO::getSerialNo, inquiryPriceDO.getSerialNo());
            detailMapper.delete(deleteWrapper);
        }
    }

    private void setDetails(List<MachineInquiryPrice> machineInquiryPrices) {
        for (MachineInquiryPrice machineInquiryPrice : machineInquiryPrices) {
            setDetail(machineInquiryPrice);
        }
    }

    private void setDetail(MachineInquiryPrice machineInquiryPrice) {
        if (machineInquiryPrice != null) {
            LambdaQueryWrapper<MachineInquiryPriceDetailDO> queryWrapper = Wrappers.lambdaQuery(MachineInquiryPriceDetailDO.class)
                    .eq(MachineInquiryPriceDetailDO::getSerialNo, machineInquiryPrice.getSerialNo());
            queryWrapper.orderByDesc(MachineInquiryPriceDetailDO::getCreateTime);
            List<MachineInquiryPriceDetailDO> inquiryPriceDetailDOS = detailMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(inquiryPriceDetailDOS)) {
                machineInquiryPrice.setParts(converter.detailDos2Entities(inquiryPriceDetailDOS));
            }
        }
    }

    public List<MachineInquiryPrice> selectListByScan(MachinePartScanQuery query) {
        List<MachineInquiryPrice> inquiryPriceS = mapper.selectListByScan(query);
        setDetails(inquiryPriceS);
        return inquiryPriceS;
    }

    public void handleStatusChange(List<String> serialNos, InquiryPriceStatus status) {
        LambdaUpdateWrapper<MachineInquiryPriceDO> updateWrapper = Wrappers.lambdaUpdate(MachineInquiryPriceDO.class).set(MachineInquiryPriceDO::getHandleStatus, status)
                .in(MachineInquiryPriceDO::getSerialNo, serialNos);
        mapper.update(updateWrapper);
    }

    public void partOrdered(String serialNo, String projectCode, String partCode, String partVersion) {
        LambdaUpdateWrapper<MachineInquiryPriceDetailDO> updateWrapper = Wrappers.lambdaUpdate(MachineInquiryPriceDetailDO.class)
                .set(MachineInquiryPriceDetailDO::getOrdered, true)
                .eq(MachineInquiryPriceDetailDO::getSerialNo, serialNo)
                .eq(MachineInquiryPriceDetailDO::getProjectCode, projectCode)
                .eq(MachineInquiryPriceDetailDO::getPartCode, partCode)
                .eq(MachineInquiryPriceDetailDO::getPartVersion, partVersion);
        detailMapper.update(updateWrapper);
    }

    public void updateHandleStatus(String serialNo) {
        LambdaQueryWrapper<MachineInquiryPriceDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineInquiryPriceDetailDO.class)
                .eq(MachineInquiryPriceDetailDO::getSerialNo, serialNo)
                .eq(MachineInquiryPriceDetailDO::getOrdered, false);
        Long notOrdered = detailMapper.selectCount(detailWrapper);
        if (notOrdered == 0) {
            LambdaUpdateWrapper<MachineInquiryPriceDO> updateWrapper = Wrappers.lambdaUpdate(MachineInquiryPriceDO.class)
                    .set(MachineInquiryPriceDO::getHandleStatus, InquiryPriceStatus.ORDERED)
                    .eq(MachineInquiryPriceDO::getSerialNo, serialNo);
            mapper.update(updateWrapper);
        }
    }

}
