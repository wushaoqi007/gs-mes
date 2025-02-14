package com.greenstone.mes;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.greenstone.mes.common.security.annotation.EnableCustomConfig;
import com.greenstone.mes.common.security.annotation.EnableRyFeignClients;
import com.greenstone.mes.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 文件服务
 *
 * @author ruoyi
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@EnableTransactionManagement
@SpringBootApplication(exclude = PageHelperAutoConfiguration.class)
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  文件服务模块启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
