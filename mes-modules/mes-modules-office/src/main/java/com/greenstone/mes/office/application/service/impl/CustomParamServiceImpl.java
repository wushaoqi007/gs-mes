package com.greenstone.mes.office.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.greenstone.mes.office.application.service.CustomParamService;
import com.greenstone.mes.office.application.dto.CustomParamListQuery;
import com.greenstone.mes.office.application.dto.CustomParamQuery;
import com.greenstone.mes.office.application.dto.CustomParamSaveCmd;
import com.greenstone.mes.office.infrastructure.mapper.CustomParamMapper;
import com.greenstone.mes.office.infrastructure.persistence.CustomParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomParamServiceImpl implements CustomParamService {

    private final CustomParamMapper customParamMapper;

    @Override
    public List<String> fromJsonList(CustomParamQuery query) {
        CustomParam param = getParam(query);
        if (param != null) {
            if (StrUtil.isBlank(param.getParamValue())) {
                return List.of();
            }
            return JSON.parseArray(param.getParamValue(), String.class);
        }
        return List.of();
    }

    @Override
    public List<String> fromJsonList(String moduleCode, String paramKey) {
        return fromJsonList(CustomParamQuery.builder().moduleCode(moduleCode).paramKey(paramKey).build());
    }

    @Override
    public List<String> fromStringList(String moduleCode, String paramKey) {
        CustomParam param = getParam(CustomParamQuery.builder().moduleCode(moduleCode).paramKey(paramKey).build());
        if (param != null) {
            if (StrUtil.isBlank(param.getParamValue())) {
                return List.of();
            }
            return Arrays.asList(param.getParamValue().split(","));
        }
        return List.of();
    }

    @Override
    public CustomParam getParam(CustomParamQuery query) {
        CustomParam selectParam = CustomParam.builder().moduleCode(query.getModuleCode())
                .paramKey(query.getParamKey()).build();
        return customParamMapper.getOneOnly(selectParam);
    }

    @Override
    public CustomParam getParam(String moduleCode, String paramKey) {
        return getParam(CustomParamQuery.builder().moduleCode(moduleCode).paramKey(paramKey).build());
    }

    public List<CustomParam> listParam(CustomParamListQuery query) {
        CustomParam selectParam = CustomParam.builder().moduleCode(query.getModuleCode())
                .paramKey(query.getParamKey()).build();
        return customParamMapper.list(selectParam);
    }

    @Override
    public void save(CustomParamSaveCmd saveCmd) {
        CustomParam selectParam = CustomParam.builder().moduleCode(saveCmd.getModuleCode())
                .paramKey(saveCmd.getParamKey()).build();
        CustomParam existParam = customParamMapper.getOneOnly(selectParam);
        if (existParam == null) {
            CustomParam insertParam = CustomParam.builder().moduleCode(saveCmd.getModuleCode())
                    .paramKey(saveCmd.getParamKey())
                    .paramValue(saveCmd.getParamValue()).build();
            customParamMapper.insert(insertParam);
        } else {
            existParam.setParamValue(saveCmd.getParamValue());
            customParamMapper.updateById(existParam);
        }
    }

    @Override
    public void saveBatch(List<CustomParamSaveCmd> saveCmds) {
        for (CustomParamSaveCmd saveCmd : saveCmds) {
            save(saveCmd);
        }
    }

}
