package com.greenstone.mes.external.application.assembler;

import com.greenstone.mes.external.application.dto.result.ProcessDefinitionResult;
import com.greenstone.mes.external.domain.entity.ProcessDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:10
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProcessAssembler {
    // BillProcDef
    ProcessDefinitionResult toProcessR(ProcessDefinition def);

}
