package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineWarehouseOut;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseOutDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseOutDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseOutDetailDO;
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
public interface MachineWarehouseOutConverter {

    @Mapping(target = "id", source = "machineWarehouseOutDO.id")
    @Mapping(target = "serialNo", source = "machineWarehouseOutDO.serialNo")
    @Mapping(target = "sponsorId", source = "machineWarehouseOutDO.sponsorId")
    @Mapping(target = "sponsor", source = "machineWarehouseOutDO.sponsor")
    @Mapping(target = "sponsorNo", source = "machineWarehouseOutDO.sponsorNo")
    @Mapping(target = "applicantId", source = "machineWarehouseOutDO.applicantId")
    @Mapping(target = "applicant", source = "machineWarehouseOutDO.applicant")
    @Mapping(target = "applicantNo", source = "machineWarehouseOutDO.applicantNo")
    @Mapping(target = "outStockTime", source = "machineWarehouseOutDO.outStockTime")
    @Mapping(target = "remark", source = "machineWarehouseOutDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineWarehouseOut toMachineWarehouseOut(MachineWarehouseOutDO machineWarehouseOutDO, List<MachineWarehouseOutDetailDO> detailDOS);

    MachineWarehouseOutDO entity2Do(MachineWarehouseOut machineWarehouseOut);

    List<MachineWarehouseOutDO> entities2Dos(List<MachineWarehouseOut> machineWarehouseOuts);


    MachineWarehouseOut do2Entity(MachineWarehouseOutDO machineWarehouseOutDO);

    List<MachineWarehouseOut> dos2Entities(List<MachineWarehouseOutDO> machineWarehouseOutDOS);

    // MachineWarehouseOutDetail
    MachineWarehouseOutDetailDO detailEntity2Do(MachineWarehouseOutDetail machineWarehouseOutDetail);

    List<MachineWarehouseOutDetailDO> detailEntities2Dos(List<MachineWarehouseOutDetail> machineWarehouseOutDetails);

    MachineWarehouseOutDetail detailDo2Entity(MachineWarehouseOutDetailDO machineWarehouseOutDetailDO);

    List<MachineWarehouseOutDetail> detailDos2Entities(List<MachineWarehouseOutDetailDO> machineWarehouseOutDetailDOS);

}
