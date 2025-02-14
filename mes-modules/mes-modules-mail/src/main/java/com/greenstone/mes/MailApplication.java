package com.greenstone.mes;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.greenstone.mes.common.security.annotation.EnableCustomConfig;
import com.greenstone.mes.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCustomConfig
@EnableRyFeignClients
@EnableTransactionManagement
@SpringBootApplication(exclude = PageHelperAutoConfiguration.class)
@EnableAsync
public class MailApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  邮件模块启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
