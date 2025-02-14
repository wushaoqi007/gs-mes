package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckTakeCommitCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckTakeAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineCheckTakeE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckTakeResult;
import com.greenstone.mes.machine.domain.entity.MachineCheckTake;
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
        imports = {Date.class, List.class, StrUtil.class, LocalDateTimeUtil.class, DatePattern.class}
)
public interface MachineCheckTakeAssemble {


    MachineCheckTake toMachineCheckTake(MachineCheckTakeAddCmd addCmd);

    MachineCheckTakeResult toMachineCheckTakeR(MachineCheckTake check);

    List<MachineCheckTakeResult> toMachineCheckTakeRs(List<MachineCheckTake> list);

    MachineCheckTakeE toCheckTakeE(MachineCheckTake check);

    @Mapping(target = "takeTime", expression = "java(LocalDateTimeUtil.format(checkTake.getTakeTime(), DatePattern.NORM_DATETIME_PATTERN))")
    WxApprovalCheckTakeCommitCmd toCheckTakeApprovalCmd(MachineCheckTake checkTake);
}
