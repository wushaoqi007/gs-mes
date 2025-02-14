package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineStockChange;
import com.greenstone.mes.machine.domain.entity.MachineStockChangeDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockChangeDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockChangeDetailDO;
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
public interface MachineStockChangeConverter {

    @Mapping(target = "id", source = "machineStockChangeDO.id")
    @Mapping(target = "serialNo", source = "machineStockChangeDO.serialNo")
    @Mapping(target = "changedById", source = "machineStockChangeDO.changedById")
    @Mapping(target = "changedBy", source = "machineStockChangeDO.changedBy")
    @Mapping(target = "changedByNo", source = "machineStockChangeDO.changedByNo")
    @Mapping(target = "changeTime", source = "machineStockChangeDO.changeTime")
    @Mapping(target = "remark", source = "machineStockChangeDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineStockChange toMachineStockChange(MachineStockChangeDO machineStockChangeDO, List<MachineStockChangeDetailDO> detailDOS);

    MachineStockChangeDO entity2Do(MachineStockChange machineStockChange);

    List<MachineStockChangeDO> entities2Dos(List<MachineStockChange> machineStockChanges);


    MachineStockChange do2Entity(MachineStockChangeDO machineStockChangeDO);

    List<MachineStockChange> dos2Entities(List<MachineStockChangeDO> machineStockChangeDOS);

    // MachineStockChangeDetail
    MachineStockChangeDetailDO detailEntity2Do(MachineStockChangeDetail machineStockChangeDetail);

    List<MachineStockChangeDetailDO> detailEntities2Dos(List<MachineStockChangeDetail> machineStockChangeDetails);

    MachineStockChangeDetail detailDo2Entity(MachineStockChangeDetailDO machineStockChangeDetailDO);

    List<MachineStockChangeDetail> detailDos2Entities(List<MachineStockChangeDetailDO> machineStockChangeDetailDOS);

}
