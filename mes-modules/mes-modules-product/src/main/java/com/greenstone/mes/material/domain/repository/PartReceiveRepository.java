package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.material.domain.converter.PartReceiveConverter;
import com.greenstone.mes.material.domain.entity.PartReceive;
import com.greenstone.mes.material.domain.entity.PartReceiveRecord;
import com.greenstone.mes.material.dto.cmd.PartReceiveRecordListQuery;
import com.greenstone.mes.material.infrastructure.mapper.PartReceiveMapper;
import com.greenstone.mes.material.infrastructure.mapper.PartReceiveRecordMapper;
import com.greenstone.mes.material.infrastructure.persistence.PartReceiveDO;
import com.greenstone.mes.material.infrastructure.persistence.PartReceiveRecordDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class PartReceiveRepository {

    private PartReceiveMapper partReceiveMapper;
    private PartReceiveRecordMapper partReceiveRecordMapper;
    private PartReceiveConverter partReceiveConverter;

    public PartReceiveRepository(PartReceiveMapper partReceiveMapper, PartReceiveConverter partReceiveConverter,
                                 PartReceiveRecordMapper partReceiveRecordMapper) {
        this.partReceiveMapper = partReceiveMapper;
        this.partReceiveConverter = partReceiveConverter;
        this.partReceiveRecordMapper = partReceiveRecordMapper;
    }

    public void save(List<PartReceive> partReceives) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (CollUtil.isNotEmpty(partReceives)) {
            PartReceiveRecordDO selectRecordDO = PartReceiveRecordDO.builder().finish(false)
                    .sponsorId(SecurityUtils.getLoginUser().getUser().getUserId()).receiveTime(format.format(new Date())).build();
            PartReceiveRecordDO findRecord = partReceiveRecordMapper.getOneOnly(selectRecordDO);
            int receiveTimes = 0;
            if (findRecord == null) {
                findRecord = selectRecordDO;
                findRecord.setSponsor(SecurityUtils.getLoginUser().getUser().getNickName());
                findRecord.setTotal(0);
                findRecord.setHandleNum(0);
                findRecord.setFinish(false);
                partReceiveRecordMapper.insert(findRecord);
            }
            List<PartReceiveDO> insertList = new ArrayList<>();
            for (PartReceive partReceive : partReceives) {
                PartReceiveDO partReceiveDO = partReceiveConverter.toPartReceiveDO(partReceive);
                // 同一取件记录的相同零件,且没处理的合并
                PartReceiveDO selectDO = PartReceiveDO.builder().recordId(findRecord.getId()).materialId(partReceive.getMaterialId()).handle(false)
                        .worksheetCode(partReceive.getWorksheetCode()).componentCode(partReceive.getComponentCode()).projectCode(partReceive.getProjectCode()).build();
                PartReceiveDO findPart = partReceiveMapper.getOneOnly(selectDO);
                if (findPart != null) {
                    findPart.setNumber((findPart.getNumber() == null ? 0 : findPart.getNumber()) + partReceiveDO.getNumber());
                    partReceiveMapper.updateById(findPart);
                } else {
                    partReceiveDO.setHandle(false);
                    partReceiveDO.setRecordId(findRecord.getId());
                    insertList.add(partReceiveDO);
                    receiveTimes++;
                }
            }
            if (CollUtil.isNotEmpty(insertList)) {
                partReceiveMapper.insertBatchSomeColumn(insertList);
                findRecord.setTotal((findRecord.getTotal() == null ? 0 : findRecord.getTotal()) + receiveTimes);
                partReceiveRecordMapper.updateById(findRecord);
            }
        }
    }

    public void update(List<PartReceive> partReceives) {
        if (CollUtil.isNotEmpty(partReceives)) {
            PartReceiveRecordDO findRecord = partReceiveRecordMapper.getOneOnly(PartReceiveRecordDO.builder().id(partReceives.get(0).getRecordId()).build());
            if (findRecord != null) {
                int handleNum = 0;
                for (PartReceive partReceive : partReceives) {
                    PartReceiveDO selectDO = PartReceiveDO.builder().recordId(findRecord.getId()).materialId(partReceive.getMaterialId()).handle(false)
                            .worksheetCode(partReceive.getWorksheetCode()).componentCode(partReceive.getComponentCode()).projectCode(partReceive.getProjectCode()).build();
                    PartReceiveDO findPart = partReceiveMapper.getOneOnly(Wrappers.query(selectDO));
                    if (findPart != null) {
                        findPart.setHandle(true);
                        findPart.setWarehouseId(partReceive.getWarehouseId());
                        partReceiveMapper.updateById(findPart);
                        handleNum++;
                    } else {
                        log.info("not found part:{} in part receive", selectDO);
                    }
                }
                findRecord.setHandleNum((findRecord.getHandleNum() == null ? 0 : findRecord.getHandleNum()) + handleNum);
                findRecord.setFinish(findRecord.getHandleNum() >= findRecord.getTotal());
                partReceiveRecordMapper.updateById(findRecord);
            }
        }
    }

    public List<PartReceiveRecord> recordList(PartReceiveRecordListQuery query) {
        LambdaQueryWrapper<PartReceiveRecordDO> queryWrapper = Wrappers.lambdaQuery(PartReceiveRecordDO.class)
                .eq(query.getUserId() != null, PartReceiveRecordDO::getSponsorId, query.getUserId())
                .eq(PartReceiveRecordDO::getFinish, false)
                .ge(query.getStartTime() != null, PartReceiveRecordDO::getCreateTime, query.getStartTime())
                .le(query.getEndTime() != null, PartReceiveRecordDO::getCreateTime, query.getEndTime() == null ? null : DateUtil.endOfDay(query.getEndTime()));
        queryWrapper.orderByDesc(PartReceiveRecordDO::getCreateTime);
        List<PartReceiveRecordDO> partReceiveRecordDOS = partReceiveRecordMapper.selectList(queryWrapper);
        return partReceiveConverter.toPartReceiveRecords(partReceiveRecordDOS);
    }

    public List<PartReceive> listPartsByRecordId(Long recordId) {
        LambdaQueryWrapper<PartReceiveDO> queryWrapper = Wrappers.lambdaQuery(PartReceiveDO.class)
                .eq(PartReceiveDO::getRecordId, recordId)
                .eq(PartReceiveDO::getHandle, false);
        queryWrapper.orderByDesc(PartReceiveDO::getCreateTime);
        List<PartReceiveDO> partReceiveDOs = partReceiveMapper.selectList(queryWrapper);
        return partReceiveConverter.toPartReceives(partReceiveDOs);
    }

}
