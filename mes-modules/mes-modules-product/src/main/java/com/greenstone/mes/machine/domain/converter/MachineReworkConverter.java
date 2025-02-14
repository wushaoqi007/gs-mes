package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineRework;
import com.greenstone.mes.machine.domain.entity.MachineReworkDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineReworkDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineReworkDetailDO;
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
public interface MachineReworkConverter {

    @Mapping(target = "id", source = "machineReworkDO.id")
    @Mapping(target = "serialNo", source = "machineReworkDO.serialNo")
    @Mapping(target = "sponsorId", source = "machineReworkDO.sponsorId")
    @Mapping(target = "sponsor", source = "machineReworkDO.sponsor")
    @Mapping(target = "sponsorNo", source = "machineReworkDO.sponsorNo")
    @Mapping(target = "reworkTime", source = "machineReworkDO.reworkTime")
    @Mapping(target = "remark", source = "machineReworkDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineRework toMachineRework(MachineReworkDO machineReworkDO, List<MachineReworkDetailDO> detailDOS);

    MachineReworkDO entity2Do(MachineRework machineRework);

    List<MachineReworkDO> entities2Dos(List<MachineRework> machineReworks);


    MachineRework do2Entity(MachineReworkDO machineReworkDO);

    List<MachineRework> dos2Entities(List<MachineReworkDO> machineReworkDOS);

    // MachineReworkDetail
    MachineReworkDetailDO detailEntity2Do(MachineReworkDetail machineReworkDetail);

    List<MachineReworkDetailDO> detailEntities2Dos(List<MachineReworkDetail> machineReworkDetails);

    MachineReworkDetail detailDo2Entity(MachineReworkDetailDO machineReworkDetailDO);

    List<MachineReworkDetail> detailDos2Entities(List<MachineReworkDetailDO> machineReworkDetailDOS);

}
