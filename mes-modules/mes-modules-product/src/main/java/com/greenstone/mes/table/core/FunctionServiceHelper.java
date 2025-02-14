package com.greenstone.mes.table.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.greenstone.mes.system.dto.result.FunctionResult;
import com.greenstone.mes.table.TableChangeReason;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.adapter.FunctionServiceAdapter;
import com.greenstone.mes.table.infrastructure.annotation.TableFunction;
import com.greenstone.mes.table.infrastructure.constant.UpdateReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@Service
public class FunctionServiceHelper<E extends TableEntity, P extends TablePo> {

    private volatile Map<String, FunctionModel<E, P>> serviceMap;


    static {
        Map<String, TableService> beansOfType = SpringUtil.getApplicationContext().getBeansOfType(TableService.class);
        FunctionServiceAdapter functionService = SpringUtil.getApplicationContext().getBean(FunctionServiceAdapter.class);
        for (TableService tableService : beansOfType.values()) {
            Class<?> clazz = getClazz(tableService);
            TableFunction tableFunction = AnnotationUtils.getAnnotation(clazz, TableFunction.class);
            if (tableFunction == null) {
                throw new RuntimeException("功能设置错误：存在未定义的TableService, " + tableService.getClass().getName());
            }
            if (tableFunction.updateReason() != UpdateReason.NEVER && tableFunction.reasonClass() == TableChangeReason.class) {
                throw new RuntimeException("功能设置错误：必须指定变更原因的类型, " + tableService.getClass().getName());
            }
            if (!StrUtil.isNumeric(tableFunction.id()) || functionService.getFunctionById(Long.valueOf(tableFunction.id())) == null) {
                log.error("功能设置错误：未在系统中定义此功能, " + tableService.getClass().getName());
            }

        }
    }

    public FunctionModel<E, P> getService(String serviceName) {
        if (serviceMap == null) {
            synchronized (this) {
                if (serviceMap == null) {
                    Map<String, FunctionModel<E, P>> serviceMap1 = new HashMap<>();
                    Map<String, TableService> beansOfType = SpringUtil.getApplicationContext().getBeansOfType(TableService.class);
                    FunctionServiceAdapter functionService = SpringUtil.getApplicationContext().getBean(FunctionServiceAdapter.class);
                    for (TableService tableService : beansOfType.values()) {
                        Class<?> clazz = getClazz(tableService);
                        TableFunction tableFunction = clazz.getDeclaredAnnotation(TableFunction.class);
                        if (tableFunction != null) {
                            FunctionResult function = functionService.getFunctionById(Long.valueOf(tableFunction.id()));
                            if (function == null) {
                                throw new RuntimeException("内部错误，未定义此功能：" + tableFunction.id());
                            }
                            FunctionModel model = new FunctionModel();
                            model.setFunctionId(Long.valueOf(tableFunction.id()));
                            model.setFunctionName(function.getName());
                            model.setTemplateId(function.getTemplateId());
                            model.setUsingProcess(function.getUsingProcess());
                            model.setTableService(tableService);
                            model.setFunction(tableFunction);
                            serviceMap1.put(tableFunction.id(), model);
                        } else {
                            throw new RuntimeException("存在未定义的TableService");
                        }
                    }
                    if (serviceMap1.size() > 0) {
                        serviceMap = serviceMap1;
                    }
                }
            }
            return serviceMap.get(serviceName);
        }
        return serviceMap.get(serviceName);
    }

    private static Class<?> getClazz(Object tableService) {
        Class<?> clazz;
        if (AopUtils.isAopProxy(tableService)) {
            try {
                Object target = ((Advised) tableService).getTargetSource().getTarget();
                if (target == null) {
                    throw new RuntimeException("功能设置错误：无法获取服务原始对象");
                }
                clazz = target.getClass();
            } catch (Exception e) {
                throw new RuntimeException("功能设置错误：无法获取服务原始对象", e);
            }
        } else {
            clazz = tableService.getClass();
        }
        return clazz;
    }

}
