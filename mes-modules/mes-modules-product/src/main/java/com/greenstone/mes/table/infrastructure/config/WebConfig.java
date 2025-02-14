package com.greenstone.mes.table.infrastructure.config;

import com.greenstone.mes.table.core.TableThreadLocalHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TableThreadLocalHandler()).addPathPatterns("/tables/**");
    }

}
