package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineMaterialReturn;
import com.greenstone.mes.machine.domain.entity.MachineMaterialReturnDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineMaterialReturnDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineMaterialReturnDetailDO;
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
public interface MachineMaterialReturnConverter {

    @Mapping(target = "id", source = "machineMaterialReturnDO.id")
    @Mapping(target = "serialNo", source = "machineMaterialReturnDO.serialNo")
    @Mapping(target = "returnById", source = "machineMaterialReturnDO.returnById")
    @Mapping(target = "returnBy", source = "machineMaterialReturnDO.returnBy")
    @Mapping(target = "returnByNo", source = "machineMaterialReturnDO.returnByNo")
    @Mapping(target = "operatorId", source = "machineMaterialReturnDO.operatorId")
    @Mapping(target = "operator", source = "machineMaterialReturnDO.operator")
    @Mapping(target = "operatorNo", source = "machineMaterialReturnDO.operatorNo")
    @Mapping(target = "returnTime", source = "machineMaterialReturnDO.returnTime")
    @Mapping(target = "remark", source = "machineMaterialReturnDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineMaterialReturn toMachineMaterialReturn(MachineMaterialReturnDO machineMaterialReturnDO, List<MachineMaterialReturnDetailDO> detailDOS);

    MachineMaterialReturnDO entity2Do(MachineMaterialReturn machineMaterialReturn);

    List<MachineMaterialReturnDO> entities2Dos(List<MachineMaterialReturn> machineMaterialReturns);

    MachineMaterialReturn do2Entity(MachineMaterialReturnDO machineMaterialReturnDO);

    List<MachineMaterialReturn> dos2Entities(List<MachineMaterialReturnDO> machineMaterialReturnDOS);

    // MachineMaterialReturnDetail
    MachineMaterialReturnDetailDO detailEntity2Do(MachineMaterialReturnDetail machineMaterialReturnDetail);

    List<MachineMaterialReturnDetailDO> detailEntities2Dos(List<MachineMaterialReturnDetail> machineMaterialReturnDetails);

    MachineMaterialReturnDetail detailDo2Entity(MachineMaterialReturnDetailDO machineMaterialReturnDetailDO);

    List<MachineMaterialReturnDetail> detailDos2Entities(List<MachineMaterialReturnDetailDO> machineMaterialReturnDetailDOS);

}
