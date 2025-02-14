package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.common.mybatisplus.IServiceWrapper;

import java.util.List;

/**
 * 物料配置Service接口
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
public interface IBaseMaterialService extends IServiceWrapper<BaseMaterial> {

    /**
     * 获取物料信息，若物料不存在，则会先保存
     *
     * @param material 物料
     * @return 物料
     */
    BaseMaterial getOrSave(BaseMaterial material);

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
     * 查询物料配置列表
     *
     * @param baseMaterial 物料配置
     * @return 物料配置集合
     */
    BaseMaterial queryBaseMaterial(BaseMaterial baseMaterial);

    /**
     * 新增物料配置
     *
     * @param baseMaterial 物料配置
     * @return 结果
     */
    public BaseMaterial insertBaseMaterial(BaseMaterial baseMaterial, boolean updateSupport);

    /**
     * 修改物料配置
     *
     * @param baseMaterial 物料配置
     * @return 结果
     */
    public int updateBaseMaterial(BaseMaterial baseMaterial);

    public int updatePrice(BaseMaterial baseMaterial);

    /**
     * 批量删除物料配置
     *
     * @param ids 需要删除的物料配置主键集合
     * @return 结果
     */
    public int deleteBaseMaterialByIds(Long[] ids);

    /**
     * 删除物料配置信息
     *
     * @param id 物料配置主键
     * @return 结果
     */
    public int deleteBaseMaterialById(Long id);
}