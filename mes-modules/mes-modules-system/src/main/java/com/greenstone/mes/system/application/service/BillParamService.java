package com.greenstone.mes.system.application.service;


import com.greenstone.mes.system.domain.BillParam;
import com.greenstone.mes.system.infrastructure.po.BillParamDo;

import java.util.List;
import java.util.Map;

public interface BillParamService {

    void save(BillParamDo billParam);

    void save(List<BillParamDo> billParams);

    List<BillParam> list(BillParamDo billParam);

    Map<String, Object> billParams(String billType);

}
