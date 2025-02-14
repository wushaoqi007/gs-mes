package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineProviderAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineProviderImportCmd;
import com.greenstone.mes.machine.domain.entity.MachineProvider;
import org.mapstruct.Mapper;
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
public interface MachineProviderAssemble {

    MachineProvider toMachineProvider(MachineProviderAddCmd addCmd);

    MachineProvider toMachineProviderFromImport(MachineProviderImportCmd addCmd);

    List<MachineProvider> toMachineProviderFromImportS(List<MachineProviderImportCmd> addCmdS);

}
