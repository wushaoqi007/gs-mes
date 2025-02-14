package com.greenstone.mes.ces.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.ces.infrastructure.persistence.OrderDO;
import org.springframework.stereotype.Repository;

/**
 * @author wushaoqi
 * @date 2023-05-24-9:55
 */
@Repository
public interface OrderMapper extends EasyBaseMapper<OrderDO> {
}
