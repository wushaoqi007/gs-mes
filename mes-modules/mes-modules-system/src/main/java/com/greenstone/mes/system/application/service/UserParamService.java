package com.greenstone.mes.system.application.service;


import com.greenstone.mes.system.infrastructure.po.UserParamDo;

import java.util.List;
import java.util.Map;

public interface UserParamService {

    void save(UserParamDo userParam);

    void save(List<UserParamDo> userParams);

    List<UserParamDo> list(UserParamDo userParam);

    Map<String, Object> userParams(String billType);

}
