package com.greenstone.mes.table.adapter;

import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceAdapter {

    private final RemoteUserService userService;


    public User getUserById(Long userId) {
        return CacheUtil.get("userById", userId, userService::getById);
    }

}