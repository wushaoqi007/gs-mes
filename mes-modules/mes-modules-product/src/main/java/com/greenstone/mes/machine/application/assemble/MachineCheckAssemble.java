package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckAddCmd;
import com.greenstone.mes.machine.application.dto.event.MachineCheckE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckRecord;
import com.greenstone.mes.machine.application.dto.result.MachineCheckResult;
import com.greenstone.mes.machine.application.dto.result.MachineReworkRecordExportR;
import com.greenstone.mes.machine.domain.entity.MachineCheck;
import com.greenstone.mes.machine.domain.entity.MachineCheckDetail;
import com.greenstone.mes.machine.domain.entity.MachineCheckTakeDetail;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatmentStage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.Date;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface MachineCheckAssemble {


    MachineCheck toMachineCheck(MachineCheckAddCmd addCmd);

    MachineCheckResult toMachineCheckR(MachineCheck check);

    List<MachineCheckResult> toMachineCheckRs(List<MachineCheck> list);

    MachineCheckE toCheckE(MachineCheck check);

    @Mapping(target = "checkSerialNo", source = "serialNo")
    @Mapping(target = "checkDetailId", source = "id")
    @Mapping(target = "id", ignore = true)
    MachineSurfaceTreatmentStage toSurfaceTreatmentStage(MachineCheckDetail machineCheckDetail);

    List<MachineCheckDetail> toMachineCheckParts(List<MachineCheckTakeDetail> allPart);

    @Mapping(target = "toBeCheckedNumber", source = "takeNumber")
    @Mapping(target = "serialNo", ignore = true)
    @Mapping(target = "id", ignore = true)
    MachineCheckDetail toMachineCheckPart(MachineCheckTakeDetail checkTakeDetail);

    List<MachineCheckRecord> toMachineCheckRecords(List<MachineCheckDetail> reworkRecords);

    @Mapping(target = "checkSerialNo", source = "serialNo")
    @Mapping(target = "checkDetailId", source = "id")
    MachineCheckRecord toMachineCheckRecords(MachineCheckDetail reworkRecord);

    List<MachineReworkRecordExportR> toMachineReworkRecordERS(List<MachineCheckRecord> reworkRecordS);

    MachineReworkRecordExportR toMachineReworkRecordER(MachineCheckRecord reworkRecord);
}
