package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.domain.service.WorksheetDetailService;
import com.greenstone.mes.material.domain.service.WorksheetService;
import com.greenstone.mes.material.infrastructure.mapper.PurchaseOrderMapper;
import com.greenstone.mes.material.request.PurchaseOrderListReq;
import com.greenstone.mes.material.response.PurchaseOrderListResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PurchaseOrderServiceImpl接口实现
 *
 * @author wushaoqi
 * @date 2022-05-16-12:57
 */
@Service
public class WorksheetServiceImpl extends ServiceImpl<PurchaseOrderMapper, ProcessOrderDO> implements WorksheetService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private WorksheetDetailService worksheetDetailService;

    @Autowired
    private IBaseMaterialService materialService;

    @Override
    public List<PurchaseOrderListResp> selectPurchaseOrderList(PurchaseOrderListReq purchaseOrderListReq) {
        if (purchaseOrderListReq.getId() != null) {
            // 零件id不为空，查询物料信息
            BaseMaterial baseMaterial = materialService.getById(purchaseOrderListReq.getId());
            if (Objects.isNull(baseMaterial)) {
                throw new ServiceException("some.of.the.material.does.not.exist");
            }
            purchaseOrderListReq.setCode(baseMaterial.getCode());
            purchaseOrderListReq.setVersion(baseMaterial.getVersion());
        }
        List<PurchaseOrderListResp> purchaseOrderListResp = purchaseOrderMapper.selectPurchaseOrderList(purchaseOrderListReq);
        if (CollectionUtil.isNotEmpty(purchaseOrderListResp)) {
            for (PurchaseOrderListResp purchaseOrderResp : purchaseOrderListResp) {
                List<ProcessOrderDetailDO> detailDOS = worksheetDetailService.list(Wrappers.query(ProcessOrderDetailDO.builder().processOrderId(purchaseOrderResp.getId()).build()));
                long sum = detailDOS.stream().collect(Collectors.summarizingLong(a -> a.getGetNumber() == null ? 0L : a.getGetNumber())).getSum();
                purchaseOrderResp.setGetAndPurchaseNumber(sum + "/" + purchaseOrderResp.getPurchaseNumber());
            }
        }
        return purchaseOrderListResp;
    }

    @Override
    public ProcessOrderDO selectByCode(String code) {
        ProcessOrderDO selectDO = ProcessOrderDO.builder().code(code).build();
        return getOneOnly(selectDO);
    }
}
