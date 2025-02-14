package com.greenstone.mes.bom.manager.impl;

import cn.hutool.core.util.ObjectUtil;
import com.greenstone.mes.bom.domain.BomDetail;
import com.greenstone.mes.bom.manager.BomDetailManager;
import com.greenstone.mes.bom.request.BomDetailEditReq;
import com.greenstone.mes.bom.service.BomDetailService;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BomDetailManagerImpl implements BomDetailManager {


    @Autowired
    private BomDetailService bomDetailService;


    @Override
    public void delete(Long id) {
        BomDetail bomDetail = bomDetailService.selectBomDetailById(id);

        if (ObjectUtil.isNull(bomDetail)) {
            throw new ServiceException("错误的id: " + id);
        }

        bomDetailService.removeById(bomDetail);
    }

    @Override
    public void update(BomDetailEditReq editRequest) {
        BomDetail bomDetail = bomDetailService.selectBomDetailById(editRequest.getId());

        if (ObjectUtil.isNull(bomDetail)) {
            throw new ServiceException("错误的id: " + editRequest.getId());
        }
        if (editRequest.getMaterialNumber() != null) {
            bomDetail.setMaterialNumber(editRequest.getMaterialNumber());
        }
        bomDetailService.updateById(bomDetail);
    }
}
