package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.material.domain.MaterialStock;
import com.greenstone.mes.material.request.StockSearchReq;
import com.greenstone.mes.material.response.StockSearchResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物料查询Mapper接口
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Repository
public interface StockSearchMapper extends BaseMapper<MaterialStock> {

    /**
     * 查询仓库中的库存
     *
     * @param searchRequest 查询信息
     * @return 库存信息
     */
    List<StockSearchResp> searchMaterialInStock(StockSearchReq searchRequest);

}
