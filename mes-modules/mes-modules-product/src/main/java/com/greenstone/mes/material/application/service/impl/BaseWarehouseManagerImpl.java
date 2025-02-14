package com.greenstone.mes.material.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteMaterialStockService;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.enums.WarehouseType;
import com.greenstone.mes.material.request.WarehouseBindProjectCmd;
import com.greenstone.mes.material.request.WarehouseBindReq;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.enums.SysError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.service.BaseWarehouseManager;
import com.greenstone.mes.material.response.StockListResp;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2022-11-22-9:38
 */

@Slf4j
@Service
public class BaseWarehouseManagerImpl implements BaseWarehouseManager {

    @Autowired
    private IBaseWarehouseService baseWarehouseService;

    @Autowired
    private RemoteMaterialStockService materialStockService;

    @Override
    public BaseWarehouse bindWarehouse(WarehouseBindReq bindReq) {
        BaseWarehouse parentWarehouse = baseWarehouseService.getById(bindReq.getWarehouseId());
        if (Objects.isNull(parentWarehouse)) {
            log.error("not find warehouse ,id:{}", bindReq.getWarehouseId());
            throw new ServiceException(BizError.E23001);
        }
        BaseWarehouse query = BaseWarehouse.builder().code(bindReq.getCode()).type(WarehouseType.BOARD.getType()).build();
        BaseWarehouse oneOnly = baseWarehouseService.getOneOnly(query);
        if (Objects.nonNull(oneOnly)) {
            log.error("already exists warehouse ,code:{}", bindReq.getCode());
            throw new ServiceException(BizError.E23005, StrUtil.format("仓库编码重复：{}", bindReq.getCode()));
        }
        BaseWarehouse baseWarehouse = BaseWarehouse.builder().code(bindReq.getCode()).type(WarehouseType.BOARD.getType()).parentId(parentWarehouse.getId())
                .stage(parentWarehouse.getStage()).name(parentWarehouse.getName() + "/" + bindReq.getCode()).build();
        baseWarehouseService.saveOrUpdate(baseWarehouse);
        return baseWarehouse;
    }

    @Override
    public void checkWarehouse(Long[] ids) {
        for (Long id : ids) {
            BaseWarehouse baseWarehouse = baseWarehouseService.getById(id);
            if (Objects.isNull(baseWarehouse)) {
                log.error("not find warehouse ,id:{}", id);
                throw new ServiceException(BizError.E23001);
            }
            if (baseWarehouse.getType() != null && Objects.equals(baseWarehouse.getType(), WarehouseType.BOARD.getType())) {
                R<List<StockListResp>> r = materialStockService.listAllStock(null, baseWarehouse.getCode());
                if (r.isFail()) {
                    log.error("remote service fail");
                    throw new ServiceException(SysError.E10004);
                }
                if (r.isPresent() && r.getData().size() > 0) {
                    log.error("delete temp warehouse fail:stock not empty");
                    throw new ServiceException(BizError.E23003);
                }
            }
        }
    }

    @Override
    public BaseWarehouse bindProject(WarehouseBindProjectCmd bindProjectCmd) {
        BaseWarehouse warehouse = baseWarehouseService.getById(bindProjectCmd.getId());
        if (Objects.isNull(warehouse)) {
            log.error("not find warehouse ,id:{}", bindProjectCmd.getId());
            throw new ServiceException(BizError.E23001);
        }
        warehouse.setProjectCode(bindProjectCmd.getProjectCode());
        baseWarehouseService.updateById(warehouse);
        return warehouse;
    }

    @Override
    public void unBindProject(WarehouseBindProjectCmd bindProjectCmd) {
        BaseWarehouse warehouse = baseWarehouseService.getById(bindProjectCmd.getId());
        if (Objects.isNull(warehouse)) {
            log.error("not find warehouse ,id:{}", bindProjectCmd.getId());
            throw new ServiceException(BizError.E23001);
        }
        warehouse.setProjectCode("");
        baseWarehouseService.updateById(warehouse);
    }
}
