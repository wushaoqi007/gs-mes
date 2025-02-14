package com.greenstone.mes.common.mybatisplus;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.github.yitter.idgen.YitIdHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 自定义 UUID 生成器
 *
 * @author gu_renkai
 * @date 2023/3/2 16:59
 */
@Primary
@Component
public class CustomIdGenerator extends DefaultIdentifierGenerator {

    @Override
    public String nextUUID(Object entity) {
        return IdUtil.fastUUID();
    }

    @Override
    public Long nextId(Object entity) {
        return YitIdHelper.nextId();
    }

    @Bean
    public IdentifierGenerator idGenerator() {
        return new CustomIdGenerator();
    }

    @Bean
    public MybatisPlusPropertiesCustomizer plusPropertiesCustomizer() {
        return plusProperties -> plusProperties.getGlobalConfig().setIdentifierGenerator(new CustomIdGenerator());
    }
}
