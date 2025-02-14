package com.greenstone.mes.system.domain.converter;

import com.greenstone.mes.system.domain.entity.Function;
import com.greenstone.mes.system.infrastructure.po.FunctionDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FunctionConverter {

    FunctionDO entity2Do(Function function);

    List<FunctionDO> entities2Dos(List<Function> functions);

    Function do2Entity(FunctionDO functionDO);

    List<Function> dos2Entities(List<FunctionDO> functionDOS);

}
