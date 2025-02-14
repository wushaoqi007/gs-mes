package com.greenstone.mes.oa.domain.converter;

import com.greenstone.mes.oa.domain.entity.CustomShift;
import com.greenstone.mes.oa.infrastructure.enums.*;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/23 11:11
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BaseTypeConverter {


    default String convert(CpId cpId) {
        return cpId.id();
    }

    default CpId convertCpId(String cpId) {
        return new CpId(cpId);
    }

    default String convert(SpNo spNo) {
        return spNo.no();
    }

    default SpNo convertSpNo(String spNo) {
        return new SpNo(spNo);
    }

    default Long toTimeStamp(Date date) {
        return date.getTime() / 1000;
    }

    default Date toDate(Long date) {
        return new Date(date * 1000);
    }

    default String convert(WxUserId userId) {
        return userId.id();
    }

    default WxUserId convertWxUserId(String userId) {
        return new WxUserId(userId);
    }

    default Integer convert(ApprovalStatus status) {
        return status.getStatus();
    }

    default WxDeptId convertWxDeptId(Long id) {
        return new WxDeptId(id);
    }

    default ApprovalStatus convertApprovalStatus(Integer status) {
        return ApprovalStatus.from(status);
    }

    default Integer convert(ScheduleShift shift) {
        return shift.getId();
    }

    default Long convert(CustomShift customShift) {
        return customShift.getId();
    }

    default ScheduleShift convertScheduleShift(Integer shift) {
        DefaultShift defaultShift = DefaultShift.get(shift);
        return ScheduleShift.builder().id(defaultShift.getId()).name(defaultShift.getName()).workSec(defaultShift.getWorkSec()).offWorkSec(defaultShift.getOffWorkSec()).build();
    }

    default Integer convert(AttendanceExceptionType exceptionType) {
        if (exceptionType != null) {
            return exceptionType.getType();
        }
        return null;
    }

    default AttendanceExceptionType convertAttendanceExceptionType(Integer code) {
        return AttendanceExceptionType.getByCode(code);
    }

    default String toName(VacationType type) {
        return type.getName();
    }

    default VacationType toVacationType(String name) {
        return VacationType.getByName(name);
    }

}
