package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.material.domain.BaseMaterial;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物料配置Mapper接口
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Repository
public interface BaseMaterialMapper extends BaseMapper<BaseMaterial> {
    /**
     * 查询物料配置
     *
     * @param id 物料配置主键
     * @return 物料配置
     */
    public BaseMaterial selectBaseMaterialById(Long id);

    /**
     * 查询物料配置列表
     *
     * @param baseMaterial 物料配置
     * @return 物料配置集合
     */
    public List<BaseMaterial> selectBaseMaterialList(BaseMaterial baseMaterial);

    /**
     * 新增物料配置
     *
     * @param baseMaterial 物料配置
     * @return 结果
     */
    public int insertBaseMaterial(BaseMaterial baseMaterial);

    /**
     * 修改物料配置
     *
     * @param baseMaterial 物料配置
     * @return 结果
     */
    public int updateBaseMaterial(BaseMaterial baseMaterial);

    public int updatePrice(BaseMaterial baseMaterial);

    /**
     * 删除物料配置
     *
     * @param id 物料配置主键
     * @return 结果
     */
    public int deleteBaseMaterialById(Long id);

    /**
     * 批量删除物料配置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBaseMaterialByIds(Long[] ids);
}