package com.greenstone.mes.oa.application.assembler;

/**
 * @author gu_renkai
 * @date 2022/11/24 9:23
 */

import com.greenstone.mes.oa.application.dto.ApproVacationImportDTO;
import com.greenstone.mes.oa.application.dto.ApprovalCorrectionImportDTO;
import com.greenstone.mes.oa.application.dto.ApprovalExtraWorkImportDTO;
import com.greenstone.mes.oa.application.dto.ApprovalNightImportDTO;
import com.greenstone.mes.oa.domain.converter.BaseTypeConverter;
import com.greenstone.mes.oa.domain.entity.ApprovalCorrection;
import com.greenstone.mes.oa.domain.entity.ApprovalExtraWork;
import com.greenstone.mes.oa.domain.entity.ApprovalNight;
import com.greenstone.mes.oa.domain.entity.ApprovalVacation;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import com.greenstone.mes.oa.infrastructure.enums.VacationType;
import com.greenstone.mes.oa.interfaces.request.ApprovalCorrectionImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalExtraWorkImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalNightImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalVacationImportCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BaseTypeConverter.class},
        imports = {Date.class, List.class, VacationType.class, ApprovalStatus.class}
)
public interface ApprovalAssembler {

    default List<ApproVacationImportDTO> toVacationImportDTOs(CpId cpId, List<ApprovalVacationImportCommand> commands) {
        ArrayList<ApproVacationImportDTO> dtos = new ArrayList<>();
        for (ApprovalVacationImportCommand command : commands) {
            dtos.add(toVacationImportDTO(cpId, command));
        }
        return dtos;
    }

    @Mapping(target = "cpId", source = "cpId")
    @Mapping(target = "spNo", source = "command.spNo")
    @Mapping(target = "applyTime", source = "command.applyTime", dateFormat = "yyyy/MM/dd HH:mm:ss")
    @Mapping(target = "userName", source = "command.userName")
    @Mapping(target = "userId", source = "command.userId")
    @Mapping(target = "type", expression = "java(VacationType.getByName(command.getType()))")
    @Mapping(target = "startTime", expression = "java(VacationType.getByName(command.getType()).castStartTime(command.getStartTime()))")
    @Mapping(target = "endTime", expression = "java(VacationType.getByName(command.getType()).castEndTime(command.getEndTime()))")
    @Mapping(target = "reason", source = "command.reason")
    @Mapping(target = "attachment", source = "command.attachment")
    @Mapping(target = "status", expression = "java(ApprovalStatus.getByName(command.getStatus()))")
    @Mapping(target = "remark", source = "command.remark")
    ApproVacationImportDTO toVacationImportDTO(CpId cpId, ApprovalVacationImportCommand command);

    ApprovalVacation toVacation(ApproVacationImportDTO importDTO);


    default List<ApprovalExtraWorkImportDTO> toExtraWorkImportDTOs(CpId cpId, List<ApprovalExtraWorkImportCommand> commands) {
        ArrayList<ApprovalExtraWorkImportDTO> dtos = new ArrayList<>();
        for (ApprovalExtraWorkImportCommand command : commands) {
            dtos.add(toExtraWorkImportDTO(cpId, command));
        }
        return dtos;
    }

    @Mapping(target = "cpId", source = "cpId")
    @Mapping(target = "spNo", source = "command.spNo")
    @Mapping(target = "applyTime", source = "command.applyTime", dateFormat = "yyyy/MM/dd HH:mm:ss")
    @Mapping(target = "userName", source = "command.userName")
    @Mapping(target = "userId", source = "command.userId")
    @Mapping(target = "startTime", source = "command.startTime", dateFormat = "yyyy/MM/dd HH:mm")
    @Mapping(target = "endTime", source = "command.endTime", dateFormat = "yyyy/MM/dd HH:mm")
    @Mapping(target = "reason", source = "command.reason")
    @Mapping(target = "status", expression = "java(ApprovalStatus.getByName(command.getStatus()))")
    @Mapping(target = "remark", source = "command.remark")
    ApprovalExtraWorkImportDTO toExtraWorkImportDTO(CpId cpId, ApprovalExtraWorkImportCommand command);

    ApprovalExtraWork toExtraWork(ApprovalExtraWorkImportDTO importDTO);

    @Mapping(target = "cpId", source = "cpId")
    @Mapping(target = "spNo", source = "command.spNo")
    @Mapping(target = "applyTime", source = "command.applyTime", dateFormat = "yyyy/MM/dd HH:mm:ss")
    @Mapping(target = "userName", source = "command.userName")
    @Mapping(target = "userId", source = "command.userId")
    @Mapping(target = "startTime", source = "command.startTime", dateFormat = "yyyy/MM/dd")
    @Mapping(target = "endTime", source = "command.endTime", dateFormat = "yyyy/MM/dd")
    @Mapping(target = "reason", source = "command.reason")
    @Mapping(target = "status", expression = "java(ApprovalStatus.getByName(command.getStatus()))")
    @Mapping(target = "remark", source = "command.remark")
    ApprovalNightImportDTO toNightImportDTO(CpId cpId, ApprovalNightImportCommand command);

    default List<ApprovalNightImportDTO> toNightImportDTOs(CpId cpId, List<ApprovalNightImportCommand> commands) {
        ArrayList<ApprovalNightImportDTO> dtos = new ArrayList<>();
        for (ApprovalNightImportCommand command : commands) {
            dtos.add(toNightImportDTO(cpId, command));
        }
        return dtos;
    }

    ApprovalNight toNight(ApprovalNightImportDTO importDTO);

    @Mapping(target = "cpId", source = "cpId")
    @Mapping(target = "spNo", source = "command.spNo")
    @Mapping(target = "applyTime", source = "command.applyTime", dateFormat = "yyyy/MM/dd HH:mm:ss")
    @Mapping(target = "userName", source = "command.userName")
    @Mapping(target = "userId", source = "command.userId")
    @Mapping(target = "correctionTime", source = "command.correctionTime", dateFormat = "yyyy/MM/dd HH:mm")
    @Mapping(target = "reason", source = "command.reason")
    @Mapping(target = "proveFile", source = "command.proveFile")
    @Mapping(target = "status", expression = "java(ApprovalStatus.getByName(command.getStatus()))")
    @Mapping(target = "remark", source = "command.remark")
    ApprovalCorrectionImportDTO toCorrectionImportDTO(CpId cpId, ApprovalCorrectionImportCommand command);

    default List<ApprovalCorrectionImportDTO> toCorrectionImportDTOs(CpId cpId, List<ApprovalCorrectionImportCommand> commands) {
        ArrayList<ApprovalCorrectionImportDTO> dtos = new ArrayList<>();
        for (ApprovalCorrectionImportCommand command : commands) {
            dtos.add(toCorrectionImportDTO(cpId, command));
        }
        return dtos;
    }

    ApprovalCorrection toCorrection(ApprovalCorrectionImportDTO importDTO);

}
