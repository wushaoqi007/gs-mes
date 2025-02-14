package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.material.domain.MaterialReceiving;
import com.greenstone.mes.material.request.MaterialReceivingListReq;
import com.greenstone.mes.material.response.MaterialReceivingDetailResp;
import com.greenstone.mes.material.response.MaterialReceivingListResp;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialReceivingMapper extends BaseMapper<MaterialReceiving> {

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
