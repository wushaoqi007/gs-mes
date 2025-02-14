package com.greenstone.mes.material.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.greenstone.mes.material.domain.MaterialMachinedPart;
import com.greenstone.mes.material.request.MachinedPartExportReq;
import com.greenstone.mes.material.request.MachinedPartsListReq;

import java.util.List;

/**
 * 机加工件Service接口
 *
 * @author gu_renkai
 * @date 2022-03-08
 */
public interface IMaterialMachinedPartService extends IService<MaterialMachinedPart> {

    /**
     * 查询机加工件
     *
     * @param id 机加工件主键
     * @return 机加工件
     */
    MaterialMachinedPart selectMaterialMachinedPartById(Long id);

    List<MaterialMachinedPart> exportMachinedPart(MachinedPartExportReq exportReq);

    /**
     * 查询机加工件列表
     *
     * @param materialMachinedPart 机加工件
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
     * 批量删除机加工件
     *
     * @param ids 需要删除的机加工件主键集合
     * @return 结果
     */
    int deleteMaterialMachinedPartByIds(Long[] ids);

    /**
     * 删除机加工件信息
     *
     * @param id 机加工件主键
     * @return 结果
     */
    int deleteMaterialMachinedPartById(Long id);
}