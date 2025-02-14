package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatmentStage;
import com.greenstone.mes.machine.infrastructure.persistence.MachineSurfaceTreatmentStageDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MachineSurfaceTreatmentStageConverter {


    MachineSurfaceTreatmentStageDO entity2Do(MachineSurfaceTreatmentStage machineSurfaceTreatmentStage);

    List<MachineSurfaceTreatmentStageDO> entities2Dos(List<MachineSurfaceTreatmentStage> machineSurfaceTreatmentStages);


    MachineSurfaceTreatmentStage do2Entity(MachineSurfaceTreatmentStageDO machineSurfaceTreatmentStageDO);

    List<MachineSurfaceTreatmentStage> dos2Entities(List<MachineSurfaceTreatmentStageDO> machineSurfaceTreatmentStageDOS);
}
