package com.greenstone.mes.job.task;

import com.greenstone.mes.office.api.RemoteMeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 会议预约任务
 */
@Slf4j
@Component("meetingReserveTask")
public class MeetingReserveTask {


    @Autowired
    private RemoteMeetingService meetingService;

    /**
     * 会议状态修改
     */
    public void changeStatus() {
        log.info("meeting status change task start");
        meetingService.changeStatus();
    }
}
