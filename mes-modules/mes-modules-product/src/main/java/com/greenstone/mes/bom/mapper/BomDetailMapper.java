package com.greenstone.mes.bom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.bom.domain.BomDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BOM明细Mapper接口
 *
 * @author gu_renkai
 * @date 2022-01-25
 */
@Repository
public interface BomDetailMapper extends BaseMapper<BomDetail> {
    /**
     * 查询BOM明细
     *
     * @param id BOM明细主键
     * @return BOM明细
     */
    public BomDetail selectBomDetailById(Long id);

    /**
     * 查询BOM明细列表
     *
     * @param bomDetail BOM明细
     * @return BOM明细集合
     */
    public List<BomDetail> selectBomDetailList(BomDetail bomDetail);

    /**
     * 新增BOM明细
     *
     * @param bomDetail BOM明细
     * @return 结果
     */
    public int insertBomDetail(BomDetail bomDetail);

    /**
     * 修改BOM明细
     *
     * @param bomDetail BOM明细
     * @return 结果
     */
    public int updateBomDetail(BomDetail bomDetail);

    /**
     * 删除BOM明细
     *
     * @param id BOM明细主键
     * @return 结果
     */
    public int deleteBomDetailById(Long id);

    /**
     * 批量删除BOM明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBomDetailByIds(Long[] ids);
}