package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineCalculate;
import com.greenstone.mes.machine.domain.entity.MachineCalculateDetail;
import com.greenstone.mes.machine.domain.entity.MachineCalculateHistory;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCalculateDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCalculateDetailDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCalculateHistoryDO;
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
public interface MachineCalculateConverter {

    @Mapping(target = "id", source = "machineCalculateDO.id")
    @Mapping(target = "serialNo", source = "machineCalculateDO.serialNo")
    @Mapping(target = "calculateBy", source = "machineCalculateDO.calculateBy")
    @Mapping(target = "calculateById", source = "machineCalculateDO.calculateById")
    @Mapping(target = "applyTime", source = "machineCalculateDO.applyTime")
    @Mapping(target = "confirmTime", source = "machineCalculateDO.confirmTime")
    @Mapping(target = "confirmBy", source = "machineCalculateDO.confirmBy")
    @Mapping(target = "parts", source = "detailDOS")
    MachineCalculate toMachineCalculate(MachineCalculateDO machineCalculateDO, List<MachineCalculateDetailDO> detailDOS);

    // MachineCalculate
    MachineCalculateDO entity2Do(MachineCalculate machineCalculate);

    List<MachineCalculateDO> entities2Dos(List<MachineCalculate> machineCalculates);

    MachineCalculate do2Entity(MachineCalculateDO machineCalculateDO);

    List<MachineCalculate> dos2Entities(List<MachineCalculateDO> machineCalculateDOS);

    // MachineCalculateDetail
    MachineCalculateDetailDO detailEntity2Do(MachineCalculateDetail machineCalculateDetail);

    List<MachineCalculateDetailDO> detailEntities2Dos(List<MachineCalculateDetail> machineCalculateDetails);

    MachineCalculateDetail detailDo2Entity(MachineCalculateDetailDO machineCalculateDetailDO);

    List<MachineCalculateDetail> detailDos2Entities(List<MachineCalculateDetailDO> machineCalculateDetailDOS);

    // MachineCalculateHistory
    MachineCalculateHistoryDO historyEntity2Do(MachineCalculateHistory machineCalculateHistory);

    List<MachineCalculateHistoryDO> historyEntities2Dos(List<MachineCalculateHistory> machineCalculateHistories);

    MachineCalculateHistory historyDo2Entity(MachineCalculateHistoryDO machineCalculateHistoryDO);

    List<MachineCalculateHistory> historyDos2Entities(List<MachineCalculateHistoryDO> machineCalculateHistoryDOs);

}
