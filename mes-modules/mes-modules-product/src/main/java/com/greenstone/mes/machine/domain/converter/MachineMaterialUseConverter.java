package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineMaterialUse;
import com.greenstone.mes.machine.domain.entity.MachineMaterialUseDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineMaterialUseDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineMaterialUseDetailDO;
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
public interface MachineMaterialUseConverter {

    @Mapping(target = "id", source = "machineMaterialUseDO.id")
    @Mapping(target = "serialNo", source = "machineMaterialUseDO.serialNo")
    @Mapping(target = "sponsorId", source = "machineMaterialUseDO.sponsorId")
    @Mapping(target = "sponsor", source = "machineMaterialUseDO.sponsor")
    @Mapping(target = "sponsorNo", source = "machineMaterialUseDO.sponsorNo")
    @Mapping(target = "operatorId", source = "machineMaterialUseDO.operatorId")
    @Mapping(target = "operator", source = "machineMaterialUseDO.operator")
    @Mapping(target = "operatorNo", source = "machineMaterialUseDO.operatorNo")
    @Mapping(target = "useTime", source = "machineMaterialUseDO.useTime")
    @Mapping(target = "remark", source = "machineMaterialUseDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineMaterialUse toMachineMaterialUse(MachineMaterialUseDO machineMaterialUseDO, List<MachineMaterialUseDetailDO> detailDOS);

    MachineMaterialUseDO entity2Do(MachineMaterialUse machineMaterialUse);

    List<MachineMaterialUseDO> entities2Dos(List<MachineMaterialUse> machineMaterialUses);

    MachineMaterialUse do2Entity(MachineMaterialUseDO machineMaterialUseDO);

    List<MachineMaterialUse> dos2Entities(List<MachineMaterialUseDO> machineMaterialUseDOS);

    // MachineMaterialUseDetail
    MachineMaterialUseDetailDO detailEntity2Do(MachineMaterialUseDetail machineMaterialUseDetail);

    List<MachineMaterialUseDetailDO> detailEntities2Dos(List<MachineMaterialUseDetail> machineMaterialUseDetails);

    MachineMaterialUseDetail detailDo2Entity(MachineMaterialUseDetailDO machineMaterialUseDetailDO);

    List<MachineMaterialUseDetail> detailDos2Entities(List<MachineMaterialUseDetailDO> machineMaterialUseDetailDOS);

}
