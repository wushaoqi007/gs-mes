package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStatusChangeCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentRecord;
import com.greenstone.mes.machine.domain.converter.MachineSurfaceTreatmentConverter;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatment;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatmentDetail;
import com.greenstone.mes.machine.infrastructure.mapper.MachineSurfaceTreatmentDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineSurfaceTreatmentMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineSurfaceTreatmentDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineSurfaceTreatmentDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MachineSurfaceTreatmentRepository {
    private final MachineSurfaceTreatmentMapper surfaceTreatmentMapper;
    private final MachineSurfaceTreatmentDetailMapper surfaceTreatmentDetailMapper;
    private final MachineSurfaceTreatmentConverter converter;

    public List<MachineSurfaceTreatment> list(MachineFuzzyQuery fuzzyQuery) {
        QueryWrapper<MachineSurfaceTreatmentDO> fuzzyQueryWrapper = new QueryWrapper<>();
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
        List<MachineSurfaceTreatmentDO> machineSurfaceTreatmentDOS = surfaceTreatmentMapper.selectList(fuzzyQueryWrapper);
        return converter.dos2Entities(machineSurfaceTreatmentDOS);
    }

    public List<MachineSurfaceTreatmentDetail> selectDetailsByPart(String requirementSerialNo, String projectCode, String partCode, String partVersion) {
        List<MachineSurfaceTreatmentDetailDO> detailDOS = surfaceTreatmentDetailMapper.list(MachineSurfaceTreatmentDetailDO.builder()
                .requirementSerialNo(requirementSerialNo)
                .projectCode(projectCode)
                .partCode(partCode).partVersion(partVersion).build());
        return converter.detailDos2Entities(detailDOS);
    }

    public List<MachineSurfaceTreatmentDetail> selectDetailsByPartAndSerialNo(String serialNo, String requirementSerialNo, String projectCode, String partCode, String partVersion) {
        List<MachineSurfaceTreatmentDetailDO> detailDOS = surfaceTreatmentDetailMapper.list(MachineSurfaceTreatmentDetailDO.builder()
                .serialNo(serialNo)
                .requirementSerialNo(requirementSerialNo)
                .projectCode(projectCode)
                .partCode(partCode).partVersion(partVersion).build());
        return converter.detailDos2Entities(detailDOS);
    }

    public MachineSurfaceTreatment detail(String serialNo) {
        MachineSurfaceTreatmentDO select = MachineSurfaceTreatmentDO.builder().serialNo(serialNo).build();
        MachineSurfaceTreatmentDO requisitionDO = surfaceTreatmentMapper.getOneOnly(select);
        if (requisitionDO == null) {
            throw new ServiceException(StrUtil.format("表处单不存在，单号：{}", serialNo));
        }
        List<MachineSurfaceTreatmentDetailDO> detailDOS = surfaceTreatmentDetailMapper.list(MachineSurfaceTreatmentDetailDO.builder().serialNo(serialNo).build());
        return converter.toMachineSurfaceTreatment(requisitionDO, detailDOS);
    }

    public List<MachineSurfaceTreatmentRecord> listRecord(MachineRecordQuery query) {
        return surfaceTreatmentMapper.listRecord(query);
    }

    public void add(MachineSurfaceTreatment surfaceTreatment) {
        MachineSurfaceTreatmentDO surfaceTreatmentDO = converter.entity2Do(surfaceTreatment);
        surfaceTreatmentMapper.insert(surfaceTreatmentDO);
        List<MachineSurfaceTreatmentDetailDO> detailList = converter.detailEntities2Dos(surfaceTreatment.getParts());
        surfaceTreatmentDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void edit(MachineSurfaceTreatment surfaceTreatment) {
        MachineSurfaceTreatmentDO surfaceTreatmentDO = converter.entity2Do(surfaceTreatment);
        MachineSurfaceTreatmentDO revokeFound = surfaceTreatmentMapper.selectById(surfaceTreatment.getId());
        if (revokeFound == null) {
            throw new ServiceException(MachineError.E200101);
        }
        if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
            throw new ServiceException(MachineError.E200102);
        }
        surfaceTreatmentMapper.updateById(surfaceTreatmentDO);
        surfaceTreatmentDetailMapper.delete(MachineSurfaceTreatmentDetailDO.builder().serialNo(surfaceTreatment.getSerialNo()).build());
        List<MachineSurfaceTreatmentDetailDO> detailList = converter.detailEntities2Dos(surfaceTreatment.getParts());
        surfaceTreatmentDetailMapper.insertBatchSomeColumn(detailList);
    }

    public void updateDetailById(MachineSurfaceTreatmentDetail part) {
        MachineSurfaceTreatmentDetailDO updateDo = converter.detailEntity2Do(part);
        surfaceTreatmentDetailMapper.updateById(updateDo);
    }

    public void statusChange(MachineStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<MachineSurfaceTreatmentDO> updateWrapper = Wrappers.lambdaUpdate(MachineSurfaceTreatmentDO.class).set(MachineSurfaceTreatmentDO::getStatus, statusChangeCmd.getStatus())
                .in(MachineSurfaceTreatmentDO::getSerialNo, statusChangeCmd.getSerialNos());
        surfaceTreatmentMapper.update(updateWrapper);
    }

    public void changeStatus(MachineSurfaceTreatment surfaceTreatment) {
        LambdaUpdateWrapper<MachineSurfaceTreatmentDO> updateWrapper = Wrappers.lambdaUpdate(MachineSurfaceTreatmentDO.class)
                .eq(MachineSurfaceTreatmentDO::getSerialNo, surfaceTreatment.getSerialNo())
                .set(MachineSurfaceTreatmentDO::getStatus, surfaceTreatment.getStatus());
        surfaceTreatmentMapper.update(updateWrapper);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            MachineSurfaceTreatmentDO revokeFound = surfaceTreatmentMapper.getOneOnly(MachineSurfaceTreatmentDO.builder().serialNo(serialNo).build());
            if (revokeFound == null) {
                throw new ServiceException(MachineError.E200101);
            }
            if (revokeFound.getStatus() != ProcessStatus.DRAFT) {
                throw new ServiceException(MachineError.E200102);
            }
        }
        LambdaQueryWrapper<MachineSurfaceTreatmentDO> revokeWrapper = Wrappers.lambdaQuery(MachineSurfaceTreatmentDO.class).in(MachineSurfaceTreatmentDO::getSerialNo, serialNos);
        surfaceTreatmentMapper.delete(revokeWrapper);
        LambdaQueryWrapper<MachineSurfaceTreatmentDetailDO> detailWrapper = Wrappers.lambdaQuery(MachineSurfaceTreatmentDetailDO.class).in(MachineSurfaceTreatmentDetailDO::getSerialNo,
                serialNos);
        surfaceTreatmentDetailMapper.delete(detailWrapper);
    }

    public List<MachineSurfaceTreatmentDetailDO> selectDetailsByOrderSerialNos(List<String> serialNos) {
        LambdaQueryWrapper<MachineSurfaceTreatmentDetailDO> queryWrapper = Wrappers.lambdaQuery(MachineSurfaceTreatmentDetailDO.class)
                .select(MachineSurfaceTreatmentDetailDO::getSerialNo, MachineSurfaceTreatmentDetailDO::getRequirementSerialNo, MachineSurfaceTreatmentDetailDO::getOrderSerialNo, MachineSurfaceTreatmentDetailDO::getProjectCode, MachineSurfaceTreatmentDetailDO::getPartCode, MachineSurfaceTreatmentDetailDO::getPartVersion, MachineSurfaceTreatmentDetailDO::getHandleNumber)
                .in(MachineSurfaceTreatmentDetailDO::getOrderSerialNo, serialNos);
        return surfaceTreatmentDetailMapper.selectList(queryWrapper);
    }

    public List<MachineSurfaceTreatmentDO> selectSubmitSerialNo(List<String> serialNos) {
        LambdaQueryWrapper<MachineSurfaceTreatmentDO> queryWrapper = Wrappers.lambdaQuery(MachineSurfaceTreatmentDO.class)
                .select(MachineSurfaceTreatmentDO::getSerialNo)
                .eq(MachineSurfaceTreatmentDO::getStatus, ProcessStatus.COMMITTED)
                .in(MachineSurfaceTreatmentDO::getSerialNo, serialNos);
        return surfaceTreatmentMapper.selectList(queryWrapper);
    }
}
