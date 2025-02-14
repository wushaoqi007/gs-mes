package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.system.infrastructure.po.MenuPo;
import org.springframework.stereotype.Repository;

/**
 * 菜单表 数据层
 *
 * @author gu_renkai
 */
@Repository
public interface MenuMapper extends EasyBaseMapper<MenuPo> {

}
