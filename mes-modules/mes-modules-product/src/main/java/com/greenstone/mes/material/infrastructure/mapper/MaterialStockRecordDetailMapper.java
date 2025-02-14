package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.material.domain.MaterialStockRecordDetail;
import com.greenstone.mes.material.response.MaterialInfoResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物料出入库记录明细Mapper接口
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@Repository
public interface MaterialStockRecordDetailMapper extends BaseMapper<MaterialStockRecordDetail> {

    List<MaterialInfoResp> listStockRecordDetail(Long recordId);


    /**
     * 查询物料出入库记录明细
     *
     * @param id 物料出入库记录明细主键
     * @return 物料出入库记录明细
     */
    MaterialStockRecordDetail selectMaterialStockRecordDetailById(Long id);

    /**
     * 查询物料出入库记录明细列表
     *
     * @param materialStockRecordDetail 物料出入库记录明细
     * @return 物料出入库记录明细集合
     */
    List<MaterialStockRecordDetail> selectMaterialStockRecordDetailList(MaterialStockRecordDetail materialStockRecordDetail);

    /**
     * 新增物料出入库记录明细
     *
     * @param materialStockRecordDetail 物料出入库记录明细
     * @return 结果
     */
    int insertMaterialStockRecordDetail(MaterialStockRecordDetail materialStockRecordDetail);

    /**
     * 修改物料出入库记录明细
     *
     * @param materialStockRecordDetail 物料出入库记录明细
     * @return 结果
     */
    int updateMaterialStockRecordDetail(MaterialStockRecordDetail materialStockRecordDetail);

    /**
     * 删除物料出入库记录明细
     *
     * @param id 物料出入库记录明细主键
     * @return 结果
     */
    int deleteMaterialStockRecordDetailById(Long id);

    /**
     * 批量删除物料出入库记录明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteMaterialStockRecordDetailByIds(Long[] ids);
}