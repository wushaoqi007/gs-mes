package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineCheckTake;
import com.greenstone.mes.machine.domain.entity.MachineCheckTakeDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckTakeDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckTakeDetailDO;
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
public interface MachineCheckTakeConverter {

    @Mapping(target = "id", source = "machineCheckTakeDO.id")
    @Mapping(target = "serialNo", source = "machineCheckTakeDO.serialNo")
    @Mapping(target = "sponsorId", source = "machineCheckTakeDO.sponsorId")
    @Mapping(target = "sponsor", source = "machineCheckTakeDO.sponsor")
    @Mapping(target = "takeById", source = "machineCheckTakeDO.takeById")
    @Mapping(target = "takeBy", source = "machineCheckTakeDO.takeBy")
    @Mapping(target = "takeByNo", source = "machineCheckTakeDO.takeByNo")
    @Mapping(target = "takeTime", source = "machineCheckTakeDO.takeTime")
    @Mapping(target = "remark", source = "machineCheckTakeDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineCheckTake toMachineCheckTake(MachineCheckTakeDO machineCheckTakeDO, List<MachineCheckTakeDetailDO> detailDOS);

    MachineCheckTakeDO entity2Do(MachineCheckTake machineCheckTake);

    List<MachineCheckTakeDO> entities2Dos(List<MachineCheckTake> machineCheckTakes);


    MachineCheckTake do2Entity(MachineCheckTakeDO machineCheckTakeDO);

    List<MachineCheckTake> dos2Entities(List<MachineCheckTakeDO> machineCheckTakeDOS);

    // MachineCheckTakeDetail
    MachineCheckTakeDetailDO detailEntity2Do(MachineCheckTakeDetail machineCheckTakeDetail);

    List<MachineCheckTakeDetailDO> detailEntities2Dos(List<MachineCheckTakeDetail> machineCheckTakeDetails);


    MachineCheckTakeDetail detailDo2Entity(MachineCheckTakeDetailDO machineCheckTakeDetailDO);

    List<MachineCheckTakeDetail> detailDos2Entities(List<MachineCheckTakeDetailDO> machineCheckTakeDetailDOS);

}
