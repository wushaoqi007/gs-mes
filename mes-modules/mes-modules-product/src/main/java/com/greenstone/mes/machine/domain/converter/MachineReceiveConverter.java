package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineReceive;
import com.greenstone.mes.machine.domain.entity.MachineReceiveDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineReceiveDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineReceiveDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
public interface MachineReceiveConverter {

    @Mapping(target = "id", source = "machineReceiveDO.id")
    @Mapping(target = "serialNo", source = "machineReceiveDO.serialNo")
    @Mapping(target = "receiverId", source = "machineReceiveDO.receiverId")
    @Mapping(target = "receiver", source = "machineReceiveDO.receiver")
    @Mapping(target = "receiverNo", source = "machineReceiveDO.receiverNo")
    @Mapping(target = "receiveTime", source = "machineReceiveDO.receiveTime")
    @Mapping(target = "remark", source = "machineReceiveDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineReceive toMachineReceive(MachineReceiveDO machineReceiveDO, List<MachineReceiveDetailDO> detailDOS);

    MachineReceiveDO entity2Do(MachineReceive machineReceive);

    List<MachineReceiveDO> entities2Dos(List<MachineReceive> machineReceives);


    MachineReceive do2Entity(MachineReceiveDO machineReceiveDO);

    List<MachineReceive> dos2Entities(List<MachineReceiveDO> machineReceiveDOS);

    // MachineReceiveDetail
    MachineReceiveDetailDO detailEntity2Do(MachineReceiveDetail machineReceiveDetail);

    List<MachineReceiveDetailDO> detailEntities2Dos(List<MachineReceiveDetail> machineReceiveDetails);


    MachineReceiveDetail detailDo2Entity(MachineReceiveDetailDO machineReceiveDetailDO);

    List<MachineReceiveDetail> detailDos2Entities(List<MachineReceiveDetailDO> machineReceiveDetailDOS);


}
