package com.greenstone.mes.system.domain.repository;

import com.greenstone.mes.system.infrastructure.mapper.DeptRoleMapper;
import com.greenstone.mes.system.infrastructure.po.DeptRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeptRoleRepository {

    private final DeptRoleMapper deptRoleMapper;

    public List<DeptRole> getDeptRole(Long deptId) {
        return deptRoleMapper.list(DeptRole.builder().deptId(deptId).build());
    }

}
