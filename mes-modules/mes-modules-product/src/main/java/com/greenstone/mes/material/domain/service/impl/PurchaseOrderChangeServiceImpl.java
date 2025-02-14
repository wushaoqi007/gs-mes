package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.PurchaseOrderChange;
import com.greenstone.mes.material.domain.service.PurchaseOrderChangeService;
import com.greenstone.mes.material.infrastructure.mapper.PurchaseOrderChangeMapper;
import org.springframework.stereotype.Service;

/**
 * 机加工单变更记录接口实现
 *
 * @author wushaoqi
 * @date 2022-08-31-12:57
 */
@Service
public class PurchaseOrderChangeServiceImpl extends ServiceImpl<PurchaseOrderChangeMapper, PurchaseOrderChange> implements PurchaseOrderChangeService {

}
