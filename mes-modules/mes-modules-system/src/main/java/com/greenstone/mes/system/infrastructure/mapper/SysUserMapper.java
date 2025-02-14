package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.system.infrastructure.po.UserPo;
import org.springframework.stereotype.Repository;

/**
 * 用户 mapper
 *
 * @author gurenkai
 */
@Repository
public interface SysUserMapper extends EasyBaseMapper<UserPo> {

}
