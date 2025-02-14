package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveImportVO;
import com.greenstone.mes.machine.application.dto.event.MachineReceiveE;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveRecord;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveResult;
import com.greenstone.mes.machine.domain.entity.MachineReceive;
import com.greenstone.mes.machine.domain.entity.MachineReceiveDetail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface MachineReceiveAssemble {

    MachineReceive toMachineReceive(MachineReceiveAddCmd addCmd);

    MachineReceiveResult toMachineReceiveR(MachineReceive receive);

    List<MachineReceiveResult> toMachineReceiveRs(List<MachineReceive> list);

    MachineReceiveE toReceiveEvent(MachineReceive receive);

    default MachineReceiveImportCmd.Part toPartImportCommand(MachineReceiveImportVO importVO) {
        return MachineReceiveImportCmd.Part.builder().partCode(importVO.validAndGetPartCodeNameVersion().getCode())
                .partName(importVO.validAndGetPartCodeNameVersion().getName())
                .partVersion(importVO.validAndGetPartCodeNameVersion().getVersion())
                .projectCode(importVO.getProjectCode())
                .expectedNumber(importVO.getOrderNumber() == null ? null : importVO.getOrderNumber().longValue())
                .actualNumber(importVO.getReceivedNumber() == null ? null : importVO.getReceivedNumber().longValue())
                .receiveTime(importVO.getReceiveTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .provider(importVO.getProvider()).build();
    }

    default List<MachineReceiveDetail> toMachineReceiveDetailsFromImport(List<MachineReceiveImportCmd.Part> parts, String serialNo) {
        List<MachineReceiveDetail> receiveDetailList = new ArrayList<>();
        for (MachineReceiveImportCmd.Part part : parts) {
            MachineReceiveDetail receiveDetail = toMachineReceiveDetailFromImport(part);
            receiveDetail.setSerialNo(serialNo);
            receiveDetail.setOperation(1);
            receiveDetailList.add(receiveDetail);
        }
        return receiveDetailList;
    }

    MachineReceiveDetail toMachineReceiveDetailFromImport(MachineReceiveImportCmd.Part part);

    List<MachineReceiveRecordExportR> toMachineReceiveRecordERS(List<MachineReceiveRecord> listRecordS);

    MachineReceiveRecordExportR toMachineReceiveRecordER(MachineReceiveRecord listRecord);
}
