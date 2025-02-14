package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.dto.StockUpdateQuery;
import com.greenstone.mes.material.application.dto.result.StockUpdateR;
import com.greenstone.mes.material.domain.*;
import com.greenstone.mes.material.domain.service.*;
import com.greenstone.mes.material.infrastructure.mapper.StockMapper;
import com.greenstone.mes.material.request.StockListReq;
import com.greenstone.mes.material.request.StockSearchListReq;
import com.greenstone.mes.material.request.StockTimeoutSearchReq;
import com.greenstone.mes.material.request.StockTotalListReq;
import com.greenstone.mes.material.response.StockListResp;
import com.greenstone.mes.material.response.StockTimeOutListResp;
import com.greenstone.mes.material.response.StockTotalListResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 物料库存Service业务层处理
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Service
public class MaterialStockServiceImpl extends ServiceImpl<StockMapper, MaterialStock> implements MaterialStockService {

    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private WorksheetService worksheetService;
    @Autowired
    private WorksheetDetailService worksheetDetailService;
    @Autowired
    private IBaseMaterialService materialService;
    @Autowired
    private IBaseWarehouseService warehouseService;

    @Override
    public List<StockTotalListResp> listStockTotal(StockTotalListReq searchRequest) {
        return stockMapper.listStockTotal(searchRequest);
    }

    @Override
    public List<StockListResp> listStock(StockListReq searchRequest) {
        return stockMapper.listStock(searchRequest);
    }

    @Override
    public List<StockListResp> listSearchStock(StockSearchListReq searchRequest) {
        return stockMapper.listSearchStock(searchRequest);
    }

    @Override
    public List<StockTimeOutListResp> listStockTimeout(StockTimeoutSearchReq searchReq) {
        if (searchReq.getContainsChildren() != null && searchReq.getContainsChildren()) {
            // 包含子仓库的滞留库存
            return stockMapper.listStockTimeoutContainsChildren(searchReq);
        } else {
            return stockMapper.listStockTimeout(searchReq);
        }
    }

    @Override
    public List<StockUpdateR> listStockForUpdate(StockUpdateQuery query) {
        List<StockUpdateR> stockUpdateRList = new ArrayList<>();
        // 检验加工单是否存在
        ProcessOrderDO processOrderDO = worksheetService.selectByCode(query.getWorksheetCode());
        if (Objects.isNull(processOrderDO)) {
            throw new ServiceException(BizError.E25001);
        }
        // 检验物料是否存在
        BaseMaterial material = materialService.getOneOnly(BaseMaterial.builder().code(query.getPartCode()).version(query.getPartVersion()).build());
        if (Objects.isNull(material)) {
            throw new ServiceException(BizError.E20001);
        }
        // 检验加工单零件是否存在
        ProcessOrderDetailDO selectDetailDO = ProcessOrderDetailDO.builder().materialId(material.getId()).
                processOrderId(processOrderDO.getId()).
                componentCode(query.getComponentCode()).
                code(query.getPartCode()).
                version(query.getPartVersion()).build();
        ProcessOrderDetailDO existDetail = worksheetDetailService.getOneOnly(selectDetailDO);
        if (Objects.isNull(existDetail)) {
            throw new ServiceException(BizError.E25009);
        }
        List<MaterialStock> stockList = stockMapper.list(MaterialStock.builder().materialId(material.getId()).worksheetCode(query.getWorksheetCode()).componentCode(query.getComponentCode()).build());
        if (CollUtil.isNotEmpty(stockList)) {
            for (MaterialStock materialStock : stockList) {
                BaseWarehouse warehouse = warehouseService.selectBaseWarehouseById(materialStock.getWarehouseId());
                if (Objects.isNull(warehouse)) {
                    throw new ServiceException(BizError.E23001);
                }
                StockUpdateR stockUpdateR = StockUpdateR.builder().materialId(material.getId()).number(materialStock.getNumber())
                        .warehouseId(materialStock.getWarehouseId()).warehouseCode(warehouse.getCode()).warehouseName(warehouse.getName())
                        .worksheetCode(query.getWorksheetCode()).projectCode(query.getProjectCode())
                        .componentCode(query.getComponentCode()).partName(query.getPartName())
                        .partCode(query.getPartCode()).partVersion(query.getPartVersion()).build();
                stockUpdateRList.add(stockUpdateR);
            }
        }
        return stockUpdateRList;
    }
}
