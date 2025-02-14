package com.greenstone.mes.office.application.service;

import com.greenstone.mes.office.application.dto.CustomParamListQuery;
import com.greenstone.mes.office.application.dto.CustomParamQuery;
import com.greenstone.mes.office.application.dto.CustomParamSaveCmd;
import com.greenstone.mes.office.infrastructure.persistence.CustomParam;

import java.util.List;

public interface CustomParamService {

    List<String> fromJsonList(CustomParamQuery query);

    List<String> fromJsonList(String moduleCode, String paramKey);

    List<String> fromStringList(String moduleCode, String paramKey);

    CustomParam getParam(CustomParamQuery query);

    CustomParam getParam(String moduleCode, String paramKey);

    List<CustomParam> listParam(CustomParamListQuery query);

    void save(CustomParamSaveCmd saveCmd);

    void saveBatch(List<CustomParamSaveCmd> saveCmds);
}
