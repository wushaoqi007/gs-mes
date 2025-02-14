package com.greenstone.mes.system.domain.service.impl;

import com.greenstone.mes.system.domain.service.TemplateEngineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;

@AllArgsConstructor
@Service
public class TemplateEngineServiceImpl implements TemplateEngineService {

    private final SpringResourceTemplateResolver springResourceTemplateResolver;

    private final StringTemplateResolver stringTemplateResolver;

    private final TemplateEngine templateEngine;

    public String getProcessedContent(String templateContent, Map<String, Object> variables) {
        templateEngine.setTemplateResolver(stringTemplateResolver);
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateContent, context);
    }

}
