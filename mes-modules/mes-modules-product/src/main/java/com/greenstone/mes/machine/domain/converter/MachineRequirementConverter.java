package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineRequirement;
import com.greenstone.mes.machine.domain.entity.MachineRequirementDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineRequirementDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineRequirementDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:38
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MachineRequirementConverter {


    MachineRequirementDO entity2Do(MachineRequirement machineRequirement);

    default String listToString(List<Long> list) {
        return cn.hutool.core.collection.CollUtil.join(list, ",");
    }

    default List<Long> stringToList(String s) {
        return cn.hutool.core.util.StrUtil.split(s, ',', -1, true, Long::valueOf);
    }

    List<MachineRequirementDO> entities2Dos(List<MachineRequirement> machineRequirements);

    MachineRequirement do2Entity(MachineRequirementDO machineRequirementDO);

    List<MachineRequirement> dos2Entities(List<MachineRequirementDO> machineRequirementDOS);

    // MachineRequirementDetail
    MachineRequirementDetailDO detailEntity2Do(MachineRequirementDetail machineRequirementDetail);

    List<MachineRequirementDetailDO> detailEntities2Dos(List<MachineRequirementDetail> machineRequirementDetails);

    MachineRequirementDetail detailDo2Entity(MachineRequirementDetailDO machineRequirementDetailDO);

    List<MachineRequirementDetail> detailDos2Entities(List<MachineRequirementDetailDO> machineRequirementDetailDOS);

}
