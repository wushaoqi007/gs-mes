package com.greenstone.mes.form.domain.helper;

import cn.hutool.extra.spring.SpringUtil;
import com.greenstone.mes.common.datascope.DynamicTableName;
import com.greenstone.mes.form.domain.service.BaseFormDataService;
import com.greenstone.mes.form.infrastructure.annotation.FormService;
import com.greenstone.mes.system.api.RemoteMenuService;
import com.greenstone.mes.system.dto.result.MenuBriefResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class FormHelper {

    private final RemoteMenuService menuService;

    /**
     * MybatisPlus 动态表格处理
     */
    private static final String FORM_TABLE_PREFIX = "form_data_";

    public String getFormDataTableName(String formId) {
        return FORM_TABLE_PREFIX + formId.replaceAll("-", "_");
    }

    public void setFormDataTableName(String formId) {
        String tableName = FORM_TABLE_PREFIX + formId.replaceAll("-", "_");
        DynamicTableName.setTableName(tableName);
    }

    public void clearFormDataTableName() {
        DynamicTableName.clearTableName();
    }

    /**
     * 根据服务名称获取表单数据服务对象
     */
    private static Map<String, BaseFormDataService<?, ?, ?>> formDataServiceMap;

    @SuppressWarnings("rawtypes")
    public BaseFormDataService<?, ?, ?> getFormDataService(String serviceName) {
        // TODO 这里暂时写死
        serviceName = "purchase_application";
        if (formDataServiceMap == null) {
            synchronized (this) {
                formDataServiceMap = new HashMap<>();
                Map<String, BaseFormDataService> flowServiceBeans = SpringUtil.getApplicationContext().getBeansOfType(BaseFormDataService.class);
                for (BaseFormDataService baseFormDataService : flowServiceBeans.values()) {
                    FormService formService = AnnotationUtils.findAnnotation(baseFormDataService.getClass(), FormService.class);
                    if (formService != null) {
                        formDataServiceMap.put(formService.value(), baseFormDataService);
                    } else {
                        throw new RuntimeException("");
                    }
                }
            }
        }
        BaseFormDataService<?, ?, ?> baseFormDataService = formDataServiceMap.get(serviceName);
        if (null == baseFormDataService) {
            log.error("内部错误，无法根据服务'{}'找到Service对象", serviceName);
            throw new RuntimeException("服务器内部错误，请联系管理员。");
        }
        return baseFormDataService;
    }

    public MenuBriefResult getMenuInfo(String formId) {
        return menuService.getBriefForm(formId);
    }

    public boolean usingProcess(String formId) {
        MenuBriefResult briefForm = menuService.getBriefForm(formId);
        return briefForm.isUsingProcess();
    }
}
