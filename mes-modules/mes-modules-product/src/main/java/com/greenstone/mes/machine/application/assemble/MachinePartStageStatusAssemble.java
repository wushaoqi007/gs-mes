package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.domain.entity.MachinePartStageStatus;
import com.greenstone.mes.machine.infrastructure.persistence.MachinePartStageStatusDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface MachinePartStageStatusAssemble {

    MachinePartStageStatus doToEntity(MachinePartStageStatusDO partStageStatusDO);

    List<MachinePartStageStatus> doSToEntities(List<MachinePartStageStatusDO> partStageStatusDOs);

    MachinePartStageStatusDO entityToDo(MachinePartStageStatus entity);

    List<MachinePartStageStatusDO> entitiesToDos(List<MachinePartStageStatus> entities);
}
