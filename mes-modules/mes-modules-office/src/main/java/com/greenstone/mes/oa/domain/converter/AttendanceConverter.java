package com.greenstone.mes.oa.domain.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.oa.domain.entity.AttendanceResult;
import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.oa.domain.entity.CheckinData;
import com.greenstone.mes.oa.infrastructure.enums.AttendanceExceptionType;
import com.greenstone.mes.oa.infrastructure.persistence.AttendanceResultDO;
import com.greenstone.mes.oa.infrastructure.persistence.WxCheckinDataDO;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import org.mapstruct.*;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/29 10:12
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BaseTypeConverter.class},
        imports = {Date.class, LocalTime.class}
)
public interface AttendanceConverter {

    List<AttendanceResultDO> toDOs(List<AttendanceResult> results);

    AttendanceResultDO convert(AttendanceResult result);

    AttendanceResult convert(AttendanceResultDO result);

    List<AttendanceResult> convertList(List<AttendanceResultDO> resultDOs);

    List<AttendanceResult> toResults(List<AttendanceResultDetail> details);

    @Mapping(target = "userId", expression = "java(dto.getUser().getUserId())")
    @Mapping(target = "day", expression = "java(new Date(dto.getDayBeginTime() * 1000))")
    @Mapping(target = "signInTime", expression = "java(dto.getCheckinTime().getSingInTime() == null ? null :new Date( dto.getCheckinTime().getSingInTime().getCheckinTime() * 1000))")
    @Mapping(target = "signOutTime", expression = "java(dto.getCheckinTime().getSingOutTime() == null ? null :new Date( dto.getCheckinTime().getSingOutTime().getCheckinTime() * 1000))")
    @Mapping(target = "trip", expression = "java(dto.getTrip().isTrip())")
    @Mapping(target = "checkinLocation", expression = "java(dto.getCheckinTime().getCheckinLocation())")
    @Mapping(target = "checkinLocationSecond", expression = "java(dto.getCheckinTime().getCheckinLocationSecond())")
    @Mapping(target = "checkinRemark", expression = "java(dto.getCheckinTime().getCheckinRemark())")
    @Mapping(target = "checkinRemarkSecond", expression = "java(dto.getCheckinTime().getCheckinRemarkSecond())")
    @Mapping(target = "checkinTimes", expression = "java(dto.getCheckinTime() == null? null : dto.getCheckinTime().getCheckinTimes())")
    @Mapping(target = "extraWorkTime", expression = "java(dto.getExtraWork() == null? null : dto.getExtraWork().getDuration())")
    @Mapping(target = "vacationTime", expression = "java(dto.getVacation() == null? null : dto.getVacation().getDuration())")
    @Mapping(target = "vacationType", expression = "java(dto.getVacation().getType())")
    @Mapping(target = "exceptionType", source = "dto", qualifiedByName = "assemblerExceptionType")
    @Mapping(target = "exceptionTime", source = "dto", qualifiedByName = "assemblerExceptionTime")
    @Mapping(target = "lateEarlyRemitTimes", source = "dto", qualifiedByName = "assemblerRemitTimes")
    @Mapping(target = "correctRemitTimes", source = "dto", qualifiedByName = "assemblerCorrectTimes")
    @Mapping(target = "customShiftName", expression = "java(dto.getShift().getCustomShift() == null ? null : dto.getShift().getCustomShift().getName())")
    @Mapping(target = "schSignInTime", expression = "java(new Date((dto.getShift().getWorkSec() + dto.getDayBeginTime()) * 1000))")
    @Mapping(target = "schSignOutTime", expression = "java(new Date((dto.getShift().getOffWorkSec() + dto.getDayBeginTime()) * 1000))")
    AttendanceResult toResult(AttendanceResultDetail dto);

    @Named("assemblerExceptionType")
    default AttendanceExceptionType assemblerExceptionType(AttendanceResultDetail dto) {
        AttendanceResultDetail.ComeLate late = dto.getComeLate();
        AttendanceResultDetail.LeaveEarly early = dto.getLeaveEarly();
        AttendanceResultDetail.Absenteeism absent = dto.getAbsenteeism();
        if (absent.isAbsenteeism()) {
            return AttendanceExceptionType.ABSENT;
        } else if (late.isLate() && early.isEarly()) {
            return AttendanceExceptionType.LATE_AND_EARLY;
        } else if (late.isLate()) {
            return AttendanceExceptionType.LATE;
        } else if (early.isEarly()) {
            return AttendanceExceptionType.EARLY;
        }
        return null;
    }

    @Named("assemblerExceptionTime")
    default Integer assemblerExceptionTime(AttendanceResultDetail dto) {
        AttendanceResultDetail.ComeLate late = dto.getComeLate();
        AttendanceResultDetail.LeaveEarly early = dto.getLeaveEarly();
        AttendanceResultDetail.Absenteeism absent = dto.getAbsenteeism();
        if (absent.isAbsenteeism()) {
            return absent.getDuration();
        } else if (late.isLate() && early.isEarly()) {
            return late.getDuration() + early.getDuration();
        } else if (late.isLate()) {
            return late.getDuration();
        } else if (early.isEarly()) {
            return early.getDuration();
        }
        return null;
    }

    @Named("assemblerRemitTimes")
    default Integer assemblerRemitTimes(AttendanceResultDetail dto) {
        AttendanceResultDetail.ComeLate late = dto.getComeLate();
        AttendanceResultDetail.LeaveEarly early = dto.getLeaveEarly();
        if (late.isRemit() && early.isRemit()) {
            return 2;
        } else if (late.isRemit() || early.isRemit()) {
            return 1;
        }
        return null;
    }

    @Named("assemblerCorrectTimes")
    default Integer assemblerCorrectTimes(AttendanceResultDetail dto) {
        long count = dto.getCheckinDataList().stream().filter(CheckinData::isRemit).count();
        return count == 0 ? null : (int) count;
    }

    @Mapping(target = "wxUserId", source = "userId")
    @Mapping(target = "mediaIds", source = "mediaIds", qualifiedByName = "mediaIdsToString")
    WxCheckinDataDO toCheckinDataDO(WxCpCheckinData cpCheckinData);

    default List<WxCheckinDataDO> toCheckinDataDOList(List<WxCpCheckinData> checkinDataList, String cpId) {
        List<WxCheckinDataDO> wxCheckinDataDOList = new ArrayList<>();
        for (WxCpCheckinData wxCpCheckinData : checkinDataList) {
            WxCheckinDataDO wxCheckinDataDO = toCheckinDataDO(wxCpCheckinData);
            wxCheckinDataDO.setCpId(cpId);
            wxCheckinDataDOList.add(wxCheckinDataDO);
        }
        return wxCheckinDataDOList;
    }

    @Mapping(target = "userId", source = "wxUserId")
    @Mapping(target = "mediaIds", source = "mediaIds", qualifiedByName = "mediaIdsToList")
    WxCpCheckinData toCheckinData(WxCheckinDataDO wxCheckinDataDO);

    List<WxCpCheckinData> toCheckinDataList(List<WxCheckinDataDO> wxCheckinDataDOList);

    @Named("mediaIdsToString")
    default String mediaIdsToString(List<String> mediaIds) {
        if (CollUtil.isNotEmpty(mediaIds)) {
            return String.join("-", mediaIds);
        }
        return "";
    }

    @Named("mediaIdsToList")
    default List<String> mediaIdsToList(String mediaIds) {
        if (StrUtil.isNotEmpty(mediaIds)) {
            return Arrays.asList(mediaIds.split("-"));
        }
        return null;
    }
}
