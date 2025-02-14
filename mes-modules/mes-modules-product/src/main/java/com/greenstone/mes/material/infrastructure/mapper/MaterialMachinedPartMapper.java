package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.material.domain.MaterialMachinedPart;
import com.greenstone.mes.material.request.MachinedPartsListReq;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 机加工件Mapper接口
 *
 * @author gu_renkai
 * @date 2022-03-08
 */
@Repository
public interface MaterialMachinedPartMapper extends BaseMapper<MaterialMachinedPart> {
    /**
     * 查询机加工件
     *
     * @param id 机加工件主键
     * @return 机加工件
     */
    MaterialMachinedPart selectMaterialMachinedPartById(Long id);

    /**
     * 查询机加工件列表
     *
     * @param req 机加工件
     * @return 机加工件集合
     */
    List<MaterialMachinedPart> selectMaterialMachinedPartList(MachinedPartsListReq req);

    /**
     * 新增机加工件
     *
     * @param materialMachinedPart 机加工件
     * @return 结果
     */
    int insertMaterialMachinedPart(MaterialMachinedPart materialMachinedPart);

    /**
     * 修改机加工件
     *
     * @param materialMachinedPart 机加工件
     * @return 结果
     */
    int updateMaterialMachinedPart(MaterialMachinedPart materialMachinedPart);

    /**
     * 删除机加工件
     *
     * @param id 机加工件主键
     * @return 结果
     */
    int deleteMaterialMachinedPartById(Long id);

    /**
     * 批量删除机加工件
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteMaterialMachinedPartByIds(Long[] ids);
}