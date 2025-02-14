package com.greenstone.mes.mail.interfaces.schedule;

import com.greenstone.mes.mail.domain.service.MailBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class MailSchedule {

    private final MailBoxService mailBoxService;

    /**
     * 每天 3:00 自动删除邮箱
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void mailAutoDelete() {
        log.info("MailSchedule.mailAutoDelete task start");
        mailBoxService.deleteExpirationMailBox();
        log.info("MailSchedule.mailAutoDelete task end");
    }

}
