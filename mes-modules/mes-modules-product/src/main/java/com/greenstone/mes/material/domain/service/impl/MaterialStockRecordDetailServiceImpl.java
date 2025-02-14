package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialStockRecordDetail;
import com.greenstone.mes.material.domain.service.IMaterialStockRecordDetailService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialStockRecordDetailMapper;
import com.greenstone.mes.material.response.MaterialInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物料出入库记录明细Service业务层处理
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@Service
public class MaterialStockRecordDetailServiceImpl extends ServiceImpl<MaterialStockRecordDetailMapper, MaterialStockRecordDetail> implements IMaterialStockRecordDetailService {

    @Autowired
    private MaterialStockRecordDetailMapper materialStockRecordDetailMapper;

    @Override
    public List<MaterialInfoResp> listStockRecordDetail(Long recordId) {
        return materialStockRecordDetailMapper.listStockRecordDetail(recordId);
    }

    /**
     * 查询物料出入库记录明细
     *
     * @param id 物料出入库记录明细主键
     * @return 物料出入库记录明细
     */
    @Override
    public MaterialStockRecordDetail selectMaterialStockRecordDetailById(Long id) {
        return materialStockRecordDetailMapper.selectMaterialStockRecordDetailById(id);
    }

    /**
     * 查询物料出入库记录明细列表
     *
     * @param materialStockRecordDetail 物料出入库记录明细
     * @return 物料出入库记录明细
     */
    @Override
    public List<MaterialStockRecordDetail> selectMaterialStockRecordDetailList(MaterialStockRecordDetail materialStockRecordDetail) {
        return materialStockRecordDetailMapper.selectMaterialStockRecordDetailList(materialStockRecordDetail);
    }

    /**
     * 新增物料出入库记录明细
     *
     * @param materialStockRecordDetail 物料出入库记录明细
     * @return 结果
     */
    @Override
    public int insertMaterialStockRecordDetail(MaterialStockRecordDetail materialStockRecordDetail) {

        return materialStockRecordDetailMapper.insertMaterialStockRecordDetail(materialStockRecordDetail);
    }

    /**
     * 修改物料出入库记录明细
     *
     * @param materialStockRecordDetail 物料出入库记录明细
     * @return 结果
     */
    @Override
    public int updateMaterialStockRecordDetail(MaterialStockRecordDetail materialStockRecordDetail) {

        return materialStockRecordDetailMapper.updateMaterialStockRecordDetail(materialStockRecordDetail);
    }

    /**
     * 批量删除物料出入库记录明细
     *
     * @param ids 需要删除的物料出入库记录明细主键
     * @return 结果
     */
    @Override
    public int deleteMaterialStockRecordDetailByIds(Long[] ids) {
        return materialStockRecordDetailMapper.deleteMaterialStockRecordDetailByIds(ids);
    }

    /**
     * 删除物料出入库记录明细信息
     *
     * @param id 物料出入库记录明细主键
     * @return 结果
     */
    @Override
    public int deleteMaterialStockRecordDetailById(Long id) {
        return materialStockRecordDetailMapper.deleteMaterialStockRecordDetailById(id);
    }
}