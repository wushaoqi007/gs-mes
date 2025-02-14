package com.greenstone.mes.material.application.assembler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.result.PartProgressR;
import com.greenstone.mes.material.response.MaterialWorksheetProgressListResp;
import com.greenstone.mes.material.response.PartStageStatusListResp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {StrUtil.class}
)
public interface PartStageStatusAssembler {

    Logger log = LoggerFactory.getLogger(PartStageStatusAssembler.class);


    MaterialWorksheetProgressListResp toWorksheetProgress(PartStageStatusListResp fulInfoListResp);

    List<MaterialWorksheetProgressListResp> toWorksheetProgressListResp(List<PartStageStatusListResp> partStageStatusListResps);

    @Mapping(target = "purchasedNum", source = "inStockTotal")
    @Mapping(target = "receivedNum", source = "outStockTotal")
    PartProgressR toPartProgressR(PartStageStatusListResp partStageStatusListResp);

}
