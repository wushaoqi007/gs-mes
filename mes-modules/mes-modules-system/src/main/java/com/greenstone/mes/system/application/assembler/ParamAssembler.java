package com.greenstone.mes.system.application.assembler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.system.application.dto.cmd.ParamAddCmd;
import com.greenstone.mes.system.application.dto.cmd.ParamDataAddCmd;
import com.greenstone.mes.system.application.dto.result.ParamResult;
import com.greenstone.mes.system.domain.entity.ParamData;
import com.greenstone.mes.system.domain.entity.ParamType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-11-16:08
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface ParamAssembler {

    List<ParamResult> toParamRs(List<ParamType> paramTypes);

    ParamResult toParamR(ParamType paramType);

    ParamType toParam(ParamAddCmd cmd);

    ParamData toParamData(ParamDataAddCmd cmd);
}
