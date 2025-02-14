package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatment;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatmentDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineSurfaceTreatmentDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineSurfaceTreatmentDetailDO;
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
public interface MachineSurfaceTreatmentConverter {

    @Mapping(target = "id", source = "machineSurfaceTreatmentDO.id")
    @Mapping(target = "serialNo", source = "machineSurfaceTreatmentDO.serialNo")
    @Mapping(target = "sponsorId", source = "machineSurfaceTreatmentDO.sponsorId")
    @Mapping(target = "sponsor", source = "machineSurfaceTreatmentDO.sponsor")
    @Mapping(target = "sponsorNo", source = "machineSurfaceTreatmentDO.sponsorNo")
    @Mapping(target = "handleTime", source = "machineSurfaceTreatmentDO.handleTime")
    @Mapping(target = "remark", source = "machineSurfaceTreatmentDO.remark")
    @Mapping(target = "parts", source = "detailDOS")
    MachineSurfaceTreatment toMachineSurfaceTreatment(MachineSurfaceTreatmentDO machineSurfaceTreatmentDO, List<MachineSurfaceTreatmentDetailDO> detailDOS);

    MachineSurfaceTreatmentDO entity2Do(MachineSurfaceTreatment machineSurfaceTreatment);

    List<MachineSurfaceTreatmentDO> entities2Dos(List<MachineSurfaceTreatment> machineSurfaceTreatments);


    MachineSurfaceTreatment do2Entity(MachineSurfaceTreatmentDO machineSurfaceTreatmentDO);

    List<MachineSurfaceTreatment> dos2Entities(List<MachineSurfaceTreatmentDO> machineSurfaceTreatmentDOS);

    // MachineSurfaceTreatmentDetail
    MachineSurfaceTreatmentDetailDO detailEntity2Do(MachineSurfaceTreatmentDetail machineSurfaceTreatmentDetail);

    List<MachineSurfaceTreatmentDetailDO> detailEntities2Dos(List<MachineSurfaceTreatmentDetail> machineSurfaceTreatmentDetails);


    MachineSurfaceTreatmentDetail detailDo2Entity(MachineSurfaceTreatmentDetailDO machineSurfaceTreatmentDetailDO);

    List<MachineSurfaceTreatmentDetail> detailDos2Entities(List<MachineSurfaceTreatmentDetailDO> machineSurfaceTreatmentDetailDOS);

}
