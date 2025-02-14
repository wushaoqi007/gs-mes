package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.application.dto.result.MachineCheckPartR;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartStockR;
import com.greenstone.mes.machine.domain.entity.MachineCheck;
import com.greenstone.mes.machine.domain.entity.MachineCheckDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineCheckDetailDO;
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
public interface MachineCheckConverter {

    @Mapping(target = "id", source = "machineCheckDO.id")
    @Mapping(target = "serialNo", source = "machineCheckDO.serialNo")
    @Mapping(target = "checkById", source = "machineCheckDO.checkById")
    @Mapping(target = "checkBy", source = "machineCheckDO.checkBy")
    @Mapping(target = "checkByNo", source = "machineCheckDO.checkByNo")
    @Mapping(target = "checkTime", source = "machineCheckDO.checkTime")
    @Mapping(target = "remark", source = "machineCheckDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineCheck toMachineCheck(MachineCheckDO machineCheckDO, List<MachineCheckDetailDO> detailDOS);

    MachineCheckDO entity2Do(MachineCheck machineCheck);

    List<MachineCheckDO> entities2Dos(List<MachineCheck> machineChecks);


    MachineCheck do2Entity(MachineCheckDO machineCheckDO);

    List<MachineCheck> dos2Entities(List<MachineCheckDO> machineCheckDOS);

    // MachineCheckDetail
    MachineCheckDetailDO detailEntity2Do(MachineCheckDetail machineCheckDetail);

    List<MachineCheckDetailDO> detailEntities2Dos(List<MachineCheckDetail> machineCheckDetails);


    MachineCheckDetail detailDo2Entity(MachineCheckDetailDO machineCheckDetailDO);

    List<MachineCheckDetail> detailDos2Entities(List<MachineCheckDetailDO> machineCheckDetailDOS);


    @Mapping(target = "checkDetailId", source = "id")
    @Mapping(target = "checkSerialNo", source = "serialNo")
    MachineCheckPartR toCheckPartR(MachineCheckDetailDO detailDO);

    @Mapping(target = "checkDetailId", source = "id")
    @Mapping(target = "checkSerialNo", source = "serialNo")
    @Mapping(target = "stockNumber", source = "checkedNumber")
    @Mapping(target = "warehouseCode", source = "inWarehouseCode")
    MachineCheckPartStockR toCheckPartStockR(MachineCheckDetailDO detailDO);
}
