package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCalculateAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCalculateImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCalculateImportVO;
import com.greenstone.mes.machine.application.dto.result.MachineCalculateResult;
import com.greenstone.mes.machine.domain.entity.MachineCalculate;
import com.greenstone.mes.machine.domain.entity.MachineCalculateDetail;
import com.greenstone.mes.machine.domain.entity.MachineCalculateHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
@Validated
public interface MachineCalculateAssemble {

    List<MachineCalculateImportCmd.Part> toPartImportCommands(@Valid List<MachineCalculateImportVO> importVOS);

    @Mapping(target = "partCode", expression = "java(importVO.validAndGetPartCode())")
    @Mapping(target = "partVersion", expression = "java(importVO.validAndGetPartVersion())")
    MachineCalculateImportCmd.Part toPartImportCommand(MachineCalculateImportVO importVO);

    default MachineCalculateAddCmd toAddCommand(MachineCalculateImportCmd importCommand) {
        List<MachineCalculateAddCmd.Part> parts = new ArrayList<>();
        MachineCalculateAddCmd calculateAddCmd = MachineCalculateAddCmd.builder().parts(parts)
                .applyTime(LocalDateTime.now())
                .calculateBy(SecurityUtils.getLoginUser().getUser().getNickName())
                .calculateById(SecurityUtils.getLoginUser().getUser().getUserId()).build();
        for (MachineCalculateImportCmd.Part part : importCommand.getParts()) {
            parts.add(toAddPart(part));
        }
        return calculateAddCmd;
    }

    MachineCalculateAddCmd.Part toAddPart(MachineCalculateImportCmd.Part part);

    MachineCalculate toMachineCalculate(MachineCalculateAddCmd addCmd);

    List<MachineCalculateResult> toMachineCalculateRs(List<MachineCalculate> machineCalculates);

    MachineCalculateResult toMachineCalculateR(MachineCalculate machineCalculate);

    @Mapping(target = "calculateDetailId", source = "id")
    @Mapping(target = "calculateSerialNo", source = "serialNo")
    @Mapping(target = "id", ignore = true)
    MachineCalculateHistory toHistory(MachineCalculateDetail calculateDetail);

    List<MachineCalculateHistory> toHistories(List<MachineCalculateDetail> calculateDetails);
}
