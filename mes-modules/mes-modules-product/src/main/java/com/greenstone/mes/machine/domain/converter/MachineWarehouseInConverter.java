package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineWarehouseIn;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseInDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseInDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineWarehouseInDetailDO;
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
public interface MachineWarehouseInConverter {

    @Mapping(target = "id", source = "machineWarehouseInDO.id")
    @Mapping(target = "serialNo", source = "machineWarehouseInDO.serialNo")
    @Mapping(target = "sponsorId", source = "machineWarehouseInDO.sponsorId")
    @Mapping(target = "sponsor", source = "machineWarehouseInDO.sponsor")
    @Mapping(target = "sponsorNo", source = "machineWarehouseInDO.sponsorNo")
    @Mapping(target = "applicantId", source = "machineWarehouseInDO.applicantId")
    @Mapping(target = "applicant", source = "machineWarehouseInDO.applicant")
    @Mapping(target = "applicantNo", source = "machineWarehouseInDO.applicantNo")
    @Mapping(target = "inStockTime", source = "machineWarehouseInDO.inStockTime")
    @Mapping(target = "remark", source = "machineWarehouseInDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineWarehouseIn toMachineWarehouseIn(MachineWarehouseInDO machineWarehouseInDO, List<MachineWarehouseInDetailDO> detailDOS);

    MachineWarehouseInDO entity2Do(MachineWarehouseIn machineWarehouseIn);

    List<MachineWarehouseInDO> entities2Dos(List<MachineWarehouseIn> machineWarehouseIns);


    MachineWarehouseIn do2Entity(MachineWarehouseInDO machineWarehouseInDO);

    List<MachineWarehouseIn> dos2Entities(List<MachineWarehouseInDO> machineWarehouseInDOS);

    // MachineWarehouseInDetail
    MachineWarehouseInDetailDO detailEntity2Do(MachineWarehouseInDetail machineWarehouseInDetail);

    List<MachineWarehouseInDetailDO> detailEntities2Dos(List<MachineWarehouseInDetail> machineWarehouseInDetails);

    MachineWarehouseInDetail detailDo2Entity(MachineWarehouseInDetailDO machineWarehouseInDetailDO);

    List<MachineWarehouseInDetail> detailDos2Entities(List<MachineWarehouseInDetailDO> machineWarehouseInDetailDOS);

}
