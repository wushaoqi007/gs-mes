package com.greenstone.mes.material.infrastructure.mapper;

import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 仓库配置Mapper接口
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Repository
public interface BaseWarehouseMapper extends EasyBaseMapper<BaseWarehouse> {
    /**
     * 查询仓库配置
     *
     * @param id 仓库配置主键
     * @return 仓库配置
     */
    public BaseWarehouse selectBaseWarehouseById(Long id);

    /**
     * 查询仓库配置列表
     *
     * @param baseWarehouse 仓库配置
     * @return 仓库配置集合
     */
    public List<BaseWarehouse> selectBaseWarehouseList(BaseWarehouse baseWarehouse);

    /**
     * 新增仓库配置
     *
     * @param baseWarehouse 仓库配置
     * @return 结果
     */
    public int insertBaseWarehouse(BaseWarehouse baseWarehouse);

    /**
     * 修改仓库配置
     *
     * @param baseWarehouse 仓库配置
     * @return 结果
     */
    public int updateBaseWarehouse(BaseWarehouse baseWarehouse);

    /**
     * 删除仓库配置
     *
     * @param id 仓库配置主键
     * @return 结果
     */
    public int deleteBaseWarehouseById(Long id);

    /**
     * 批量删除仓库配置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBaseWarehouseByIds(Long[] ids);
}