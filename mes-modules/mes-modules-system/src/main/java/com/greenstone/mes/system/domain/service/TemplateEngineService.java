package com.greenstone.mes.system.domain.service;

import java.util.Map;

public interface TemplateEngineService {

    String getProcessedContent(String templateContent, Map<String, Object> variables);

}
