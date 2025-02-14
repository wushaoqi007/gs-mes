package com.greenstone.mes.system.application.service.impl;


import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.application.service.UserParamService;
import com.greenstone.mes.system.infrastructure.mapper.UserParamMapper;
import com.greenstone.mes.system.infrastructure.po.UserParamDo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserParamServiceImpl implements UserParamService {

    private final UserParamMapper userParamMapper;

    @Override
    public void save(UserParamDo userParam) {
        UserParamDo selectParam = UserParamDo.builder().billType(userParam.getBillType())
                .userId(SecurityUtils.getUserId())
                .paramKey(userParam.getParamKey()).build();
        UserParamDo existingParam = userParamMapper.getOneOnly(selectParam);
        if (existingParam == null) {
            UserParamDo insertParam = UserParamDo.builder().billType(userParam.getBillType())
                    .userId(SecurityUtils.getUserId())
                    .paramKey(userParam.getParamKey())
                    .paramValue(userParam.getParamValue()).build();
            userParamMapper.insert(insertParam);
        } else {
            UserParamDo updateParam = UserParamDo.builder().billType(userParam.getBillType())
                    .id(existingParam.getId())
                    .paramValue(userParam.getParamValue()).build();
            userParamMapper.updateById(updateParam);
        }
    }

    @Override
    public void save(List<UserParamDo> userParams) {
        for (UserParamDo userParam : userParams) {
            save(userParam);
        }
    }

    @Override
    public List<UserParamDo> list(UserParamDo userParam) {
        userParam.setUserId(SecurityUtils.getUserId());
        return userParamMapper.list(userParam);
    }

    @Override
    public Map<String, Object> userParams(String billType) {
        List<UserParamDo> userParams = userParamMapper.list(UserParamDo.builder().userId(SecurityUtils.getUserId()).billType(billType).build());
        return userParams.stream().collect(Collectors.toMap(UserParamDo::getParamKey, UserParamDo::getParamValue));
    }

}
