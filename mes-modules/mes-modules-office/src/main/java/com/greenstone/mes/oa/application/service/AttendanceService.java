package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.oa.application.dto.AttendCheatResult;
import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.interfaces.request.AttendanceMyMonthQuery;
import com.greenstone.mes.oa.interfaces.resp.AttendanceMyMonthResult;

import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/30 13:54
 */

public interface AttendanceService {

    List<AttendanceResultDetail> calcYesterday();

    void calcAndSaveYesterday();

    void calcAndSaveYesterdayAsync();

    List<AttendanceResultDetail> calc(Date start, Date end);

    void calcAndSaveAsync(Date start, Date end);

    void calcAndSave(Date start, Date end);

    List<AttendanceResultDetail> calc(Date start, Date end, CpId cpId, String userId);

    void calcAndSaveAsync(Date start, Date end, CpId cpId, String userId);

    void calcAndSave(Date start, Date end, CpId cpId, String userId);

    List<AttendanceResultDetail> calc(Date start, Date end, CpId cpId, List<WxUserId> wxUserIds);

    void calcAndSave(Date start, Date end, CpId cpId, List<WxUserId> wxUserIds);

    void calcAndSave(Date start, Date end, CpId cpId, WxUserId userId);

    AttendanceMyMonthResult statMyMonthAttendance(AttendanceMyMonthQuery query);

    List<AttendCheatResult> analyseCheat(Date start, Date end, CpId cpId);

}
