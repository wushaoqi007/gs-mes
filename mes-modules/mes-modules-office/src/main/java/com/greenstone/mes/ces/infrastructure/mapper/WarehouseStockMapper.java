package com.greenstone.mes.ces.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.ces.application.dto.query.WarehouseStockFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.WarehouseStockResult;
import com.greenstone.mes.ces.domain.entity.WarehouseStockDetail;
import com.greenstone.mes.ces.infrastructure.persistence.WarehouseStockDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:18
 */
@Repository
public interface WarehouseStockMapper extends EasyBaseMapper<WarehouseStockDO> {
    List<WarehouseStockDetail> checkStock();

    List<WarehouseStockResult> search(WarehouseStockFuzzyQuery query);
}
