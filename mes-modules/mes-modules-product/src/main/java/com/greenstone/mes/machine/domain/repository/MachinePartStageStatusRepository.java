package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.machine.application.assemble.MachinePartStageStatusAssemble;
import com.greenstone.mes.machine.domain.entity.MachinePartStageStatus;
import com.greenstone.mes.machine.infrastructure.mapper.MachinePartStageStatusMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachinePartStageStatusDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-24-10:27
 */
@Slf4j
@AllArgsConstructor
@Service
public class MachinePartStageStatusRepository {

    private final MachinePartStageStatusMapper partStageStatusMapper;
    private final MachinePartStageStatusAssemble partStageStatusAssemble;

    public MachinePartStageStatus getOne(Integer stage, String orderDetailId) {
        MachinePartStageStatusDO oneOnly = partStageStatusMapper.getOneOnly(MachinePartStageStatusDO.builder().stage(stage).orderDetailId(orderDetailId).build());
        return partStageStatusAssemble.doToEntity(oneOnly);
    }

    public List<MachinePartStageStatus> getListByOrderSerialNo(String orderSerialNo) {
        List<MachinePartStageStatusDO> list = partStageStatusMapper.list(MachinePartStageStatusDO.builder().orderSerialNo(orderSerialNo).build());
        return partStageStatusAssemble.doSToEntities(list);
    }

    public void saveBatch(List<MachinePartStageStatus> addList) {
        if (CollUtil.isNotEmpty(addList)) {
            List<MachinePartStageStatusDO> doList = partStageStatusAssemble.entitiesToDos(addList);
            partStageStatusMapper.insertBatchSomeColumn(doList);
        }
    }

    public void updateBatchById(List<MachinePartStageStatus> updateList) {
        if (CollUtil.isNotEmpty(updateList)) {
            List<MachinePartStageStatusDO> doList = partStageStatusAssemble.entitiesToDos(updateList);
            for (MachinePartStageStatusDO update : doList) {
                partStageStatusMapper.updateById(update);
            }
        }
    }

    public void updateById(MachinePartStageStatus partStageStatus) {
        partStageStatusMapper.updateById(partStageStatusAssemble.entityToDo(partStageStatus));
    }


}
