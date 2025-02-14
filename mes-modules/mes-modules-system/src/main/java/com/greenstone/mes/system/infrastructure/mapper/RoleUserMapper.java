package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.infrastructure.po.RoleUserDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-21-10:14
 */
@Repository
public interface RoleUserMapper extends EasyBaseMapper<RoleUserDO> {

    List<User> selectUnallocatedUsers(User user);

    List<User> selectAllocatedUsers(Long roleId);
}
