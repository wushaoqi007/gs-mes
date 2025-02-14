package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialReceivingStatusChange;
import com.greenstone.mes.material.infrastructure.mapper.MaterialReceivingStatusChangeMapper;
import com.greenstone.mes.material.domain.service.IMaterialReceivingStatusChangeService;
import org.springframework.stereotype.Service;

/**
 * 领料单状态变更Service业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-15-8:22
 */
@Service
public class MaterialReceivingStatusChangeServiceImpl extends ServiceImpl<MaterialReceivingStatusChangeMapper, MaterialReceivingStatusChange> implements IMaterialReceivingStatusChangeService {
}
