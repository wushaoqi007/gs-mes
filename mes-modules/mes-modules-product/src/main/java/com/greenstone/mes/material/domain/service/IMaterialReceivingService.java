package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.domain.MaterialReceiving;
import com.greenstone.mes.material.request.MaterialReceivingListReq;
import com.greenstone.mes.material.response.MaterialReceivingDetailResp;
import com.greenstone.mes.material.response.MaterialReceivingListResp;

import java.util.List;

/**
 * 领料单Service
 *
 * @author wushaoqi
 * @date 2022-08-15-8:21
 */
public interface IMaterialReceivingService extends IServiceWrapper<MaterialReceiving> {
    /**
     * 查询领料单详情
     *
     * @param id 领料单ID
     * @return 详细信息
     */
    List<MaterialReceivingDetailResp> selectMaterialReceivingDetailById(Long id);

    /**
     * 查询领料单列表
     */
    List<MaterialReceivingListResp> selectMaterialReceivingList(MaterialReceivingListReq receivingListReq);
}
