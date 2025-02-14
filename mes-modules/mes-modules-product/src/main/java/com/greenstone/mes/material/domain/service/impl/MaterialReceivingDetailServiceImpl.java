package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialReceivingDetail;
import com.greenstone.mes.material.domain.service.IMaterialReceivingDetailService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialReceivingDetailMapper;
import org.springframework.stereotype.Service;

/**
 * 领料单详情Service业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-15-8:22
 */
@Service
public class MaterialReceivingDetailServiceImpl extends ServiceImpl<MaterialReceivingDetailMapper, MaterialReceivingDetail> implements IMaterialReceivingDetailService {
}
