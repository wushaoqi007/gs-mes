package com.greenstone.mes.material.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.material.domain.MaterialStock;
import com.greenstone.mes.material.request.StockListReq;
import com.greenstone.mes.material.request.StockSearchListReq;
import com.greenstone.mes.material.request.StockTimeoutSearchReq;
import com.greenstone.mes.material.request.StockTotalListReq;
import com.greenstone.mes.material.response.StockListResp;
import com.greenstone.mes.material.response.StockTimeOutListResp;
import com.greenstone.mes.material.response.StockTotalListResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物料库存Mapper接口
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Repository
public interface StockMapper extends EasyBaseMapper<MaterialStock> {

    /**
     * 查询物料库存
     *
     * @param id 物料库存主键
     * @return 物料库存
     */
    public MaterialStock selectMaterialStockById(Long id);

    /**
     * 查询物料库存列表
     *
     * @param materialStock 物料库存
     * @return 物料库存集合
     */
    public List<MaterialStock> selectMaterialStockList(MaterialStock materialStock);

    /**
     * 新增物料库存
     *
     * @param materialStock 物料库存
     * @return 结果
     */
    public int insertMaterialStock(MaterialStock materialStock);

    /**
     * 修改物料库存
     *
     * @param materialStock 物料库存
     * @return 结果
     */
    public int updateMaterialStock(MaterialStock materialStock);

    /**
     * 删除物料库存
     *
     * @param id 物料库存主键
     * @return 结果
     */
    public int deleteMaterialStockById(Long id);

    /**
     * 批量删除物料库存
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMaterialStockByIds(Long[] ids);

    List<StockTotalListResp> listStockTotal(StockTotalListReq searchRequest);

    List<StockListResp> listStock(StockListReq searchRequest);

    /**
     * 查询指定物料指定仓库的
     */
    List<StockListResp> listSearchStock(StockSearchListReq searchRequest);

    /**
     * 包含子仓库的滞留库存
     *
     * @return 结果
     */
    List<StockTimeOutListResp> listStockTimeoutContainsChildren(StockTimeoutSearchReq searchReq);

    /**
     * 滞留库存
     *
     * @return 结果
     */
    List<StockTimeOutListResp> listStockTimeout(StockTimeoutSearchReq searchReq);

}
