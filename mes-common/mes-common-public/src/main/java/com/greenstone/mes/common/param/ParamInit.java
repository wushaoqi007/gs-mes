package com.greenstone.mes.common.param;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.param.mapper.CommonParamMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ParamInit {

    private final CommonParamMapper commonParamMapper;

    @PostConstruct
    public void initParam() {
        log.info("Common param: load param.");
        List<CommonParam> commonParams = commonParamMapper.selectList(null);
        if (CollUtil.isEmpty(commonParams)) {
            log.info("Common param: no param need to init.");
            return;
        }
        Map<String, List<CommonParam>> classMap = commonParams.stream().collect(Collectors.groupingBy(CommonParam::getClazz));
        classMap.forEach((className, list) -> {
            Class<?> c = null;
            try {
                c = Class.forName(className);
            } catch (ClassNotFoundException e) {
                log.error("Param set failed, can't find class with name " + className);
            }

            if (c != null) {
                for (CommonParam commonParam : list) {
                    if (StrUtil.isEmpty(commonParam.getValue())) {
                        log.error("Param set failed, value is empty " + className + "." + commonParam.getField());
                        continue;
                    }
                    Field field = null;
                    try {
                        field = c.getField(commonParam.getField());
                    } catch (NoSuchFieldException e) {
                        log.error("Param set failed, can't find field with " + className + "." + commonParam.getField());
                    }
                    if (field != null) {
                        Class<?> filedType = field.getType();
                        try {
//                            log.info("Param set " + className + "." + commonParam.getField() + ": " + commonParam.getValue());
                            if (filedType.equals(int.class) || filedType.equals(Integer.class)) {
                                field.set(null, Integer.parseInt(commonParam.getValue()));
                            } else if (filedType.equals(long.class) || filedType.equals(Long.class)) {
                                field.set(null, Long.parseLong(commonParam.getValue()));
                            } else if (filedType.equals(String.class)) {
                                field.set(null, commonParam.getValue());
                            } else if (filedType.equals(boolean.class) || filedType.equals(Boolean.class)) {
                                field.set(null, Boolean.valueOf(commonParam.getValue()));
                            } else {
                                log.error("Param set failed, Unsupported filed type " + className + "." + commonParam.getField() + ": " + filedType.getName());
                            }
                        } catch (IllegalAccessException e) {
                            log.error("Param set failed, can't access field " + className + "." + commonParam.getField());
                        }
                    }
                }

            }
        });
    }


    @PostConstruct
    public void reload() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                initParam();
            }
        }, 5 * 60 * 1000, 5 * 60 * 1000);
    }
}
