package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.request.PurchaseOrderListReq;
import com.greenstone.mes.material.response.PurchaseOrderListResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 采购单Mapper接口
 *
 * @author wushaoqi
 * @date 2022-05-16-12:57
 */
@Repository
public interface PurchaseOrderMapper extends BaseMapper<ProcessOrderDO> {

    /**
     * 查询采购单列表
     *
     * @param purchaseOrderListReq
     */
    List<PurchaseOrderListResp> selectPurchaseOrderList(PurchaseOrderListReq purchaseOrderListReq);
}
