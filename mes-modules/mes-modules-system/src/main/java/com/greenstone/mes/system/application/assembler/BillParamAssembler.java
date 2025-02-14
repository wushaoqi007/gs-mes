package com.greenstone.mes.system.application.assembler;

import com.greenstone.mes.system.domain.BillParam;
import com.greenstone.mes.system.infrastructure.po.BillParamDo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:10
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BillParamAssembler {

    BillParam do2Entity(BillParamDo billParamDo);

    List<BillParam> dos2Entities(List<BillParamDo> billParamDos);

}
