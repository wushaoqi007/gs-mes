package com.greenstone.mes.machine.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.machine.domain.converter.MachineRequirementConverter;
import com.greenstone.mes.machine.domain.entity.MachineRequirement;
import com.greenstone.mes.machine.domain.entity.MachineRequirementDetail;
import com.greenstone.mes.machine.infrastructure.mapper.MachineRequirementDetailMapper;
import com.greenstone.mes.machine.infrastructure.mapper.MachineRequirementMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineRequirementDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineRequirementDetailDO;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
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
 * @author wushaoqi
 * @date 2023-11-24-10:27
 */
@Slf4j
@Service
public class MachineRequirementRepository extends AbstractTableRepository<MachineRequirement, MachineRequirementDO, MachineRequirementMapper> {
    private final MachineRequirementConverter converter;
    private final MachineRequirementDetailMapper detailMapper;
    private final IBaseMaterialService materialService;

    public MachineRequirementRepository(MachineRequirementMapper mapper, MachineRequirementDetailMapper requirementDetailMapper,
                                        MachineRequirementConverter converter, IBaseMaterialService materialService) {
        super(mapper);
        this.detailMapper = requirementDetailMapper;
        this.converter = converter;
        this.materialService = materialService;
    }

    @Override
    public MachineRequirement getEntity(Long id) {
        MachineRequirementDO machineRequirementDO = mapper.selectById(id);
        MachineRequirement machineRequirement = converter.do2Entity(machineRequirementDO);
        setDetail(machineRequirement);
        return machineRequirement;
    }

    @Override
    public List<MachineRequirement> getEntities(MachineRequirement requirement) {
        LambdaQueryWrapper<MachineRequirementDO> lambdaQuery = Wrappers.lambdaQuery(MachineRequirementDO.class);
        lambdaQuery.eq(StrUtil.isNotEmpty(requirement.getSerialNo()), MachineRequirementDO::getSerialNo, requirement.getSerialNo());
        lambdaQuery.like(StrUtil.isNotEmpty(requirement.getProjectCode()), MachineRequirementDO::getProjectCode, requirement.getProjectCode());
        lambdaQuery.ne(MachineRequirementDO::getDataStatus, TableConst.DataStatus.DRAFT);
        if (CollUtil.isNotEmpty(requirement.getParams())) {
            log.info("getEntities params:{}", requirement.getParams());
            Map<String, Object> params = requirement.getParams();
            if (params.get("partCode") != null && params.get("partVersion") != null && StrUtil.isNotBlank(params.get("partCode").toString()) && StrUtil.isNotBlank(params.get("partVersion").toString())) {
                lambdaQuery.inSql(MachineRequirementDO::getSerialNo,
                        "select serial_no from machine_requirement_detail d where d.part_code like '%" + params.get("partCode") + "%'" +
                                " and d.part_version = '" + params.get("partVersion") + "'");
            } else if (params.get("partCode") != null && StrUtil.isNotBlank(params.get("partCode").toString())) {
                lambdaQuery.inSql(params.get("partCode") != null, MachineRequirementDO::getSerialNo,
                        "select serial_no from machine_requirement_detail d where d.part_code like '%" + params.get("partCode") + "%'");
            }
            if (params.get("ids") != null && StrUtil.isNotBlank(params.get("ids").toString())) {
                lambdaQuery.in(MachineRequirementDO::getId, (List<Long>) params.get("ids"));
            }
        }
        lambdaQuery.orderByDesc(MachineRequirementDO::getProcessStartTime);
        List<MachineRequirement> machineRequirements = converter.dos2Entities(mapper.selectByDataScopeLambda(lambdaQuery));
        setDetails(machineRequirements);
        return machineRequirements;
    }

    @Override
    public List<MachineRequirement> getDrafts() {
        QueryWrapper<MachineRequirementDO> query = Wrappers.query();
        query.eq("data_status", TableConst.DataStatus.DRAFT);
        query.eq("create_by", SecurityUtils.getUserId());
        query.orderByDesc("create_time");
        List<MachineRequirement> machineRequirements = converter.dos2Entities(mapper.selectList(query));
        setDetails(machineRequirements);
        return machineRequirements;
    }

    @Override
    public MachineRequirement insert(MachineRequirement requirement) {
        validateOrSavePart(requirement);
        MachineRequirementDO machineRequirementDO = converter.entity2Do(requirement);
        mapper.insert(machineRequirementDO);
        if (CollUtil.isNotEmpty(requirement.getParts())) {
            List<MachineRequirementDetailDO> machineRequirementDetailDOS = converter.detailEntities2Dos(requirement.getParts());
            detailMapper.insertBatchSomeColumn(machineRequirementDetailDOS);
        }
        MachineRequirement entity = getEntity(machineRequirementDO.getId());
        entity.setFunctionId(requirement.getFunctionId());
        return entity;
    }

    @Override
    public MachineRequirement update(MachineRequirement requirement) {
        validateOrSavePart(requirement);
        MachineRequirement oldEntity = getEntity(requirement.getId());
        MachineRequirementDO machineRequirementDO = converter.entity2Do(requirement);
        mapper.updateById(machineRequirementDO);
        if (CollUtil.isNotEmpty(requirement.getParts())) {
            List<MachineRequirementDetailDO> insertDetails = new ArrayList<>();
            List<MachineRequirementDetailDO> updateDetailS = converter.detailEntities2Dos(requirement.getParts());
            for (MachineRequirementDetailDO updateDo : updateDetailS) {
                if (updateDo.getId() != null) {
                    // 除了更新的都要删掉
                    oldEntity.getParts().removeIf(d -> Objects.equals(d.getId(), updateDo.getId()));
                    updateDo.setScannedPaperNumber(0);
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
        MachineRequirement entity = getEntity(machineRequirementDO.getId());
        entity.setFunctionId(requirement.getFunctionId());
        entity.setChangeReason(requirement.getChangeReason());
        return entity;
    }

    @Override
    public void delete(Long id) {
        MachineRequirementDO machineRequirementDO = mapper.selectById(id);
        if (machineRequirementDO != null) {
            mapper.deleteById(id);
            LambdaQueryWrapper<MachineRequirementDetailDO> deleteWrapper = Wrappers.lambdaQuery(MachineRequirementDetailDO.class)
                    .eq(MachineRequirementDetailDO::getSerialNo, machineRequirementDO.getSerialNo());
            detailMapper.delete(deleteWrapper);
        }
    }

    public synchronized void validateOrSavePart(MachineRequirement requirement) {
        for (MachineRequirementDetail part : requirement.getParts()) {
            BaseMaterial materialFind = materialService.queryBaseMaterial(BaseMaterial.builder().code(part.getPartCode()).version(part.getPartVersion()).build());
            if (Objects.nonNull(materialFind)) {
                part.setMaterialId(materialFind.getId());
                if (!materialFind.getName().equals(part.getPartName())) {
                    throw new ServiceException(BizError.E20002, StrUtil.format("零件号'{}/{}'重复，当前的零件名称：'{}'，已存在的零件名称：'{}'",
                            part.getPartCode(), part.getPartVersion(), part.getPartName(), materialFind.getName()));
                }
            } else {
                BaseMaterial addReq = BaseMaterial.builder().code(part.getPartCode())
                        .version(part.getPartVersion())
                        .name(part.getPartName())
                        .rawMaterial(part.getRawMaterial())
                        .surfaceTreatment(part.getSurfaceTreatment())
                        .weight(part.getWeight())
                        .unit("pcs")
                        .type(1)
                        .designer(part.getDesigner()).build();
                BaseMaterial baseMaterial = materialService.insertBaseMaterial(addReq, false);
                part.setMaterialId(baseMaterial.getId());
            }
        }
    }

    public MachineRequirement getBySerialNo(String serialNo) {
        QueryWrapper<MachineRequirementDO> query = Wrappers.query(MachineRequirementDO.builder().serialNo(serialNo).build());
        MachineRequirement entity = converter.do2Entity(mapper.getOneOnly(query));
        setDetail(entity);
        return entity;
    }

    public MachineRequirement getEffectiveRequirementBySerialNo(String serialNo) {
        QueryWrapper<MachineRequirementDO> query = Wrappers.query(MachineRequirementDO.builder().serialNo(serialNo).dataStatus(TableConst.DataStatus.EFFECTIVE).build());
        MachineRequirement entity = converter.do2Entity(mapper.getOneOnly(query));
        setDetail(entity);
        return entity;
    }

    public Boolean isExist(String serialNo) {
        return mapper.exists(MachineRequirementDO.builder().serialNo(serialNo).build());
    }

    private void setDetails(List<MachineRequirement> machineRequirements) {
        for (MachineRequirement machineRequirement : machineRequirements) {
            setDetail(machineRequirement);
        }
    }

    public void setDetail(MachineRequirement machineRequirement) {
        if (machineRequirement != null) {
            LambdaQueryWrapper<MachineRequirementDetailDO> queryWrapper = Wrappers.lambdaQuery(MachineRequirementDetailDO.class)
                    .eq(MachineRequirementDetailDO::getSerialNo, machineRequirement.getSerialNo());
            queryWrapper.orderByDesc(MachineRequirementDetailDO::getCreateTime);
            List<MachineRequirementDetailDO> machineRequirementDetailDOS = detailMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(machineRequirementDetailDOS)) {
                machineRequirement.setParts(converter.detailDos2Entities(machineRequirementDetailDOS));
            }
        }
    }

    public void saveMailResult(MailSendResult mailSendResult) {
        LambdaUpdateWrapper<MachineRequirementDO> updateWrapper = Wrappers.lambdaUpdate(MachineRequirementDO.class)
                .set(MachineRequirementDO::getMailStatus, mailSendResult.getStatus())
                .set(MachineRequirementDO::getMailMsg, mailSendResult.getErrorMsg())
                .eq(MachineRequirementDO::getSerialNo, mailSendResult.getSerialNo());
        mapper.update(updateWrapper);
    }
}
