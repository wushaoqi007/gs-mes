package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineCheckedTake;
import com.greenstone.mes.machine.domain.entity.MachineCheckedTakeDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckedTakeDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckedTakeDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MachineCheckedTakeConverter {

    @Mapping(target = "id", source = "machineCheckedTakeDO.id")
    @Mapping(target = "serialNo", source = "machineCheckedTakeDO.serialNo")
    @Mapping(target = "sponsorId", source = "machineCheckedTakeDO.sponsorId")
    @Mapping(target = "sponsor", source = "machineCheckedTakeDO.sponsor")
    @Mapping(target = "takeById", source = "machineCheckedTakeDO.takeById")
    @Mapping(target = "takeBy", source = "machineCheckedTakeDO.takeBy")
    @Mapping(target = "takeByNo", source = "machineCheckedTakeDO.takeByNo")
    @Mapping(target = "takeTime", source = "machineCheckedTakeDO.takeTime")
    @Mapping(target = "remark", source = "machineCheckedTakeDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineCheckedTake toMachineCheckedTake(MachineCheckedTakeDO machineCheckedTakeDO, List<MachineCheckedTakeDetailDO> detailDOS);

    MachineCheckedTakeDO entity2Do(MachineCheckedTake machineCheckedTake);

    List<MachineCheckedTakeDO> entities2Dos(List<MachineCheckedTake> machineCheckedTakes);


    MachineCheckedTake do2Entity(MachineCheckedTakeDO machineCheckedTakeDO);

    List<MachineCheckedTake> dos2Entities(List<MachineCheckedTakeDO> machineCheckedTakeDOS);

    // MachineCheckedTakeDetail
    MachineCheckedTakeDetailDO detailEntity2Do(MachineCheckedTakeDetail machineCheckedTakeDetail);

    List<MachineCheckedTakeDetailDO> detailEntities2Dos(List<MachineCheckedTakeDetail> machineCheckedTakeDetails);


    MachineCheckedTakeDetail detailDo2Entity(MachineCheckedTakeDetailDO machineCheckedTakeDetailDO);

    List<MachineCheckedTakeDetail> detailDos2Entities(List<MachineCheckedTakeDetailDO> machineCheckedTakeDetailDOS);

}
