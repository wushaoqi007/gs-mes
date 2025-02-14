package com.greenstone.mes.file.application.task;

import com.greenstone.mes.file.application.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class FileTask {

    private final FileService fileService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void autoClear(){
        fileService.clearExpireFile();
    }

}
