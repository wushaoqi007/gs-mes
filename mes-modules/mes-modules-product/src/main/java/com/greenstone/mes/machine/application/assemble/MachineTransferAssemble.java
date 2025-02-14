package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineTransferAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineTransferE;
import com.greenstone.mes.machine.application.dto.result.MachineTransferResult;
import com.greenstone.mes.machine.domain.entity.MachineTransfer;
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
public interface MachineTransferAssemble {


    MachineTransfer toMachineTransfer(MachineTransferAddCmd addCmd);

    MachineTransferResult toMachineTransferR(MachineTransfer transfer);

    List<MachineTransferResult> toMachineTransferRs(List<MachineTransfer> list);

    MachineTransferE toMachineTransferE(MachineTransfer transfer);

}
