package com.greenstone.mes.oa.domain.converter;

import com.greenstone.mes.oa.domain.entity.CustomShift;
import com.greenstone.mes.oa.infrastructure.persistence.CustomShiftDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BaseTypeConverter.class},
        imports = {Date.class}
)
public interface CustomShiftConverter {

    CustomShiftDO toDO(CustomShift customShift);

    List<CustomShiftDO> toDOs(List<CustomShift> customShifts);

    CustomShift toEntity(CustomShiftDO customShiftDO);

    List<CustomShift> toEntities(List<CustomShiftDO> customShiftDOS);

}
