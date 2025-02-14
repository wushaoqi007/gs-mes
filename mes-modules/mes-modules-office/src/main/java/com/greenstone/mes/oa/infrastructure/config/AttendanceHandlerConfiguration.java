package com.greenstone.mes.oa.infrastructure.config;

import com.greenstone.mes.oa.domain.service.handler.*;
import com.greenstone.mes.oa.domain.service.impl.AttendanceCalcServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author gu_renkai
 * @date 2022/11/30 11:35
 */
@RequiredArgsConstructor
@Component
public class AttendanceHandlerConfiguration {

    private final AttendanceCalcServiceImpl attendanceCalcService;
    private final PrepareHandler prepareHandler;
    private final CheckinTimeHandler checkinTimeHandler;
    private final CheckOptionHandler checkOptionHandler;
    private final TripHandler tripHandler;
    private final RestHandler restHandler;
    private final VacationHandler vacationHandler;
    private final ComeLateHandler comeLateHandler;
    private final LeaveEarlyHandler leaveEarlyHandler;
    private final AbsentHandler absentHandler;
    private final ExtraWorkHandler extraWorkHandler;
    private final CorrectHandler correctHandler;
    private final ResultHandler resultHandler;
    private final WorkTimeHandler workTimeHandler;

    @PostConstruct
    public void addHandlers() {
        attendanceCalcService.addHandler(prepareHandler);
        attendanceCalcService.addHandler(checkinTimeHandler);
        attendanceCalcService.addHandler(checkOptionHandler);
        attendanceCalcService.addHandler(tripHandler);
        attendanceCalcService.addHandler(restHandler);
        attendanceCalcService.addHandler(vacationHandler);
        attendanceCalcService.addHandler(comeLateHandler);
        attendanceCalcService.addHandler(leaveEarlyHandler);
        attendanceCalcService.addHandler(absentHandler);
        attendanceCalcService.addHandler(extraWorkHandler);
        attendanceCalcService.addHandler(workTimeHandler);
        attendanceCalcService.addHandler(correctHandler);
        attendanceCalcService.addHandler(resultHandler);
    }

}
