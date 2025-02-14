package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineTransfer;
import com.greenstone.mes.machine.domain.entity.MachineTransferDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineTransferDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineTransferDetailDO;
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
public interface MachineTransferConverter {

    @Mapping(target = "id", source = "machineTransferDO.id")
    @Mapping(target = "serialNo", source = "machineTransferDO.serialNo")
    @Mapping(target = "transferBy", source = "machineTransferDO.transferBy")
    @Mapping(target = "transferById", source = "machineTransferDO.transferById")
    @Mapping(target = "transferByNo", source = "machineTransferDO.transferByNo")
    @Mapping(target = "transferTime", source = "machineTransferDO.transferTime")
    @Mapping(target = "remark", source = "machineTransferDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineTransfer toMachineTransfer(MachineTransferDO machineTransferDO, List<MachineTransferDetailDO> detailDOS);

    MachineTransferDO entity2Do(MachineTransfer machineTransfer);

    List<MachineTransferDO> entities2Dos(List<MachineTransfer> machineTransfers);


    MachineTransfer do2Entity(MachineTransferDO machineTransferDO);

    List<MachineTransfer> dos2Entities(List<MachineTransferDO> machineTransferDOS);

    // MachineTransferDetail
    MachineTransferDetailDO detailEntity2Do(MachineTransferDetail machineTransferDetail);

    List<MachineTransferDetailDO> detailEntities2Dos(List<MachineTransferDetail> machineTransferDetails);

    MachineTransferDetail detailDo2Entity(MachineTransferDetailDO machineTransferDetailDO);

    List<MachineTransferDetail> detailDos2Entities(List<MachineTransferDetailDO> machineTransferDetailDOS);

}
