package com.greenstone.mes.system.domain.converter;

import com.greenstone.mes.system.domain.entity.ParamData;
import com.greenstone.mes.system.domain.entity.ParamType;
import com.greenstone.mes.system.infrastructure.po.ParamDataDO;
import com.greenstone.mes.system.infrastructure.po.ParamTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-11-16:04
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ParamConverter {

    // Param
    ParamTypeDO entity2Do(ParamType paramType);

    List<ParamTypeDO> entities2Dos(List<ParamType> paramTypes);

    ParamType do2Entity(ParamTypeDO paramTypeDO);

    List<ParamType> dos2Entities(List<ParamTypeDO> paramTypeDOS);

    // ParamDetail
    ParamDataDO dataEntity2Do(ParamData paramData);

    List<ParamDataDO> dataEntities2Dos(List<ParamData> paramData);

    ParamData dataDo2Entity(ParamDataDO paramDataDO);

    List<ParamData> dataDos2Entities(List<ParamDataDO> paramDataDOS);
}
