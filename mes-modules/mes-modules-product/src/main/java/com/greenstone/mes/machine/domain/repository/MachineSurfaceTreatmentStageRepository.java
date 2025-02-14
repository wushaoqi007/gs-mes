package com.greenstone.mes.machine.domain.repository;

import com.greenstone.mes.machine.domain.converter.MachineSurfaceTreatmentStageConverter;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatmentStage;
import com.greenstone.mes.machine.infrastructure.mapper.MachineSurfaceTreatmentStageMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineSurfaceTreatmentStageDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wushaoqi
 * @date 2023-12-14-8:54
 */
@Slf4j
@AllArgsConstructor
@Service
public class MachineSurfaceTreatmentStageRepository {
    private final MachineSurfaceTreatmentStageMapper surfaceTreatmentStageMapper;
    private final MachineSurfaceTreatmentStageConverter converter;

    public MachineSurfaceTreatmentStage getByCheckDetailId(String checkDetailId) {
        MachineSurfaceTreatmentStageDO oneOnly = surfaceTreatmentStageMapper.getOneOnly(MachineSurfaceTreatmentStageDO.builder().checkDetailId(checkDetailId).build());
        return converter.do2Entity(oneOnly);
    }

    public void add(MachineSurfaceTreatmentStage treatmentStage) {
        log.info("add MachineSurfaceTreatmentStage:{}", treatmentStage);
        MachineSurfaceTreatmentStageDO machineSurfaceTreatmentStageDO = converter.entity2Do(treatmentStage);
        surfaceTreatmentStageMapper.insert(machineSurfaceTreatmentStageDO);
    }

    public void update(MachineSurfaceTreatmentStage findStage) {
        log.info("update MachineSurfaceTreatmentStage:{}", findStage);
        MachineSurfaceTreatmentStageDO machineSurfaceTreatmentStageDO = converter.entity2Do(findStage);
        surfaceTreatmentStageMapper.updateById(machineSurfaceTreatmentStageDO);
    }
}
