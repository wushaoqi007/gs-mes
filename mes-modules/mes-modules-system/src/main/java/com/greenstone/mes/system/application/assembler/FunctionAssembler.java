package com.greenstone.mes.system.application.assembler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.system.application.dto.cmd.FunctionAddCmd;
import com.greenstone.mes.system.application.dto.result.FunctionPermissionResult;
import com.greenstone.mes.system.dto.result.FunctionResult;
import com.greenstone.mes.system.domain.entity.Function;
import com.greenstone.mes.system.infrastructure.po.FunctionPermissionDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface FunctionAssembler {

    List<FunctionResult> toFunctionRs(List<Function> functions);

    FunctionResult toFunctionR(Function function);

    Function toFunction(FunctionAddCmd cmd);

    List<FunctionPermissionResult.PermissionGroup> toPermGroups(List<FunctionPermissionDO> perms);

    @Mapping(target = "functionPermissionId", source = "id")
    FunctionPermissionResult.PermissionGroup toPermGroup(FunctionPermissionDO perm);
}
