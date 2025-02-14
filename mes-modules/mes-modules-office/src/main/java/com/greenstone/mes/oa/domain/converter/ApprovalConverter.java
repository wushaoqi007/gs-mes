package com.greenstone.mes.oa.domain.converter;

import com.greenstone.mes.oa.domain.*;
import com.greenstone.mes.oa.domain.entity.*;
import org.mapstruct.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/23 11:08
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BaseTypeConverter.class},
        imports = {Date.class}
)
public interface ApprovalConverter {

    @Mapping(target = "spStatus", source = "status")
    ApprovalAttendanceDO toDO(ApprovalExtraWork approvalExtraWork);

    @Mapping(target = "spStatus", source = "status")
    ApprovalVacationDO toDO(ApprovalVacation approvalVacation);

    @Mapping(target = "spStatus", source = "status")
    ApprovalNightDO toDO(ApprovalNight approvalNight);

    @Mapping(target = "spStatus", source = "status")
    ApprovalTemporaryChangeDO toDO(ApprovalTemporaryChange approvalTemporaryChange);

    @Mapping(target = "correctionTime", source = "correctionTime")
    @Mapping(target = "spStatus", source = "status")
    ApprovalPunchCorrectionDO toDO(ApprovalCorrection approvalCorrection);

    @Mapping(target = "spStatus", source = "status")
    ApprovalTripDO toDO(ApprovalTrip approvalTrip);

    @InheritInverseConfiguration
    ApprovalExtraWork toEntity(ApprovalAttendanceDO approvalAttendanceDO);

    List<ApprovalExtraWork> toApprovalAttendances(List<ApprovalAttendanceDO> approvalAttendanceDOs);

    @InheritInverseConfiguration
    ApprovalVacation toEntity(ApprovalVacationDO approvalVacationDO);

    List<ApprovalVacation> toApprovalVacations(List<ApprovalVacationDO> approvalVacationDOs);

    @InheritInverseConfiguration
    ApprovalNight toEntity(ApprovalNightDO approvalNightDO);

    List<ApprovalNight> toApprovalNights(List<ApprovalNightDO> approvalNightDOs);

    @InheritInverseConfiguration
    ApprovalTemporaryChange toEntity(ApprovalTemporaryChangeDO approvalTemporaryChangeDO);

    List<ApprovalTemporaryChange> toApprovalTemporaryChanges(List<ApprovalTemporaryChangeDO> approvalTemporaryChangeDOs);

    @InheritInverseConfiguration
    ApprovalCorrection toEntity(ApprovalPunchCorrectionDO approvalPunchCorrectionDO);

    List<ApprovalCorrection> toApprovalPunchCorrections(List<ApprovalPunchCorrectionDO> approvalPunchCorrectionDOs);


}
