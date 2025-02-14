package com.greenstone.mes.bom.service;

import com.greenstone.mes.bom.domain.BomDetail;
import com.greenstone.mes.common.mybatisplus.IServiceWrapper;

import java.util.List;

/**
 * BOM明细Service接口
 *
 * @author gu_renkai
 * @date 2022-01-25
 */
public interface BomDetailService extends IServiceWrapper<BomDetail> {


    void saveOrUpdateDetail(BomDetail bomDetail);


    /**
     * 查询BOM明细
     *
     * @param id BOM明细主键
     * @return BOM明细
     */
    BomDetail selectBomDetailById(Long id);

    /**
     * 查询BOM明细列表
     *
     * @param bomDetail BOM明细
     * @return BOM明细集合
     */
    List<BomDetail> selectBomDetailList(BomDetail bomDetail);

    /**
     * 新增BOM明细
     *
     * @param bomDetail BOM明细
     * @return 结果
     */
    int insertBomDetail(BomDetail bomDetail);

    /**
     * 修改BOM明细
     *
     * @param bomDetail BOM明细
     * @return 结果
     */
    int updateBomDetail(BomDetail bomDetail);

    /**
     * 批量删除BOM明细
     *
     * @param ids 需要删除的BOM明细主键集合
     * @return 结果
     */
    int deleteBomDetailByIds(Long[] ids);

    /**
     * 删除BOM明细信息
     *
     * @param id BOM明细主键
     * @return 结果
     */
    int deleteBomDetailById(Long id);
}