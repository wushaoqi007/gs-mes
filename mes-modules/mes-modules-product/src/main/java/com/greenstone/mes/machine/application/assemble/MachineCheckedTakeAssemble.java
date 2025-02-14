package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckedTakeCommitCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckedTakeAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineCheckedTakeE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckedTakeResult;
import com.greenstone.mes.machine.domain.entity.MachineCheckedTake;
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
public interface MachineCheckedTakeAssemble {


    MachineCheckedTake toMachineCheckedTake(MachineCheckedTakeAddCmd addCmd);

    MachineCheckedTakeResult toMachineCheckedTakeR(MachineCheckedTake check);

    List<MachineCheckedTakeResult> toMachineCheckedTakeRs(List<MachineCheckedTake> list);

    @Mapping(target = "takeTime", expression = "java(LocalDateTimeUtil.format(checkTake.getTakeTime(), DatePattern.NORM_DATETIME_PATTERN))")
    WxApprovalCheckedTakeCommitCmd toCheckedTakeApprovalCmd(MachineCheckedTake checkTake);

    MachineCheckedTakeE toCheckedTakeE(MachineCheckedTake check);

}
