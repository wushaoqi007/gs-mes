package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialReceiving;
import com.greenstone.mes.material.domain.service.IMaterialReceivingService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialReceivingMapper;
import com.greenstone.mes.material.request.MaterialReceivingListReq;
import com.greenstone.mes.material.response.MaterialReceivingDetailResp;
import com.greenstone.mes.material.response.MaterialReceivingListResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 领料单Service业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-15-8:22
 */
@Service
public class MaterialReceivingServiceImpl extends ServiceImpl<MaterialReceivingMapper, MaterialReceiving> implements IMaterialReceivingService {

    @Autowired
    private MaterialReceivingMapper receivingMapper;

    @Override
    public List<MaterialReceivingDetailResp> selectMaterialReceivingDetailById(Long id) {
        return receivingMapper.selectMaterialReceivingDetailById(id);
    }

    @Override
    public List<MaterialReceivingListResp> selectMaterialReceivingList(MaterialReceivingListReq receivingListReq) {
        return receivingMapper.selectMaterialReceivingList(receivingListReq);
    }
}
