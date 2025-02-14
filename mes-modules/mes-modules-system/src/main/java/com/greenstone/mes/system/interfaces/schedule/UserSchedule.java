package com.greenstone.mes.system.interfaces.schedule;

import com.greenstone.mes.system.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class UserSchedule {

    private final UserService userService;

    // 同步工号
    @Scheduled(cron = "0 0 9,10,11,12,13,14,15,16,17 * * ?")
    public void syncUserEmpNo() {
        userService.syncWxEmployeeNo();
    }

}
