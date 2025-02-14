package com.greenstone.mes.ces.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.ces.infrastructure.persistence.OrderItemDO;
import org.springframework.stereotype.Repository;

/**
 * @author wushaoqi
 * @date 2023-05-24-9:56
 */
@Repository
public interface OrderItemMapper extends EasyBaseMapper<OrderItemDO> {
}
