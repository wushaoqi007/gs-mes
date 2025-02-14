package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialMachinedPart;
import com.greenstone.mes.material.domain.service.IMaterialMachinedPartService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialMachinedPartMapper;
import com.greenstone.mes.material.request.MachinedPartExportReq;
import com.greenstone.mes.material.request.MachinedPartsListReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 机加工件Service业务层处理
 *
 * @author gu_renkai
 * @date 2022-03-08
 */
@Service
public class MaterialMachinedPartServiceImpl extends ServiceImpl<MaterialMachinedPartMapper, MaterialMachinedPart> implements IMaterialMachinedPartService {
    @Autowired
    private MaterialMachinedPartMapper materialMachinedPartMapper;

    /**
     * 查询机加工件
     *
     * @param id 机加工件主键
     * @return 机加工件
     */
    @Override
    public MaterialMachinedPart selectMaterialMachinedPartById(Long id) {
        return materialMachinedPartMapper.selectMaterialMachinedPartById(id);
    }

    @Override
    public List<MaterialMachinedPart> exportMachinedPart(MachinedPartExportReq exportReq) {
        QueryWrapper<MaterialMachinedPart> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().gt(MaterialMachinedPart::getCreateTime, exportReq.getStartTime()).lt(MaterialMachinedPart::getCreateTime, exportReq.getEndTime());
        return list(queryWrapper);
    }

    /**
     * 查询机加工件列表
     *
     * @param req 机加工件
     * @return 机加工件
     */
    @Override
    public List<MaterialMachinedPart> selectMaterialMachinedPartList(MachinedPartsListReq req) {
        return materialMachinedPartMapper.selectMaterialMachinedPartList(req);
    }

    /**
     * 新增机加工件
     *
     * @param materialMachinedPart 机加工件
     * @return 结果
     */
    @Override
    public int insertMaterialMachinedPart(MaterialMachinedPart materialMachinedPart) {

        return materialMachinedPartMapper.insertMaterialMachinedPart(materialMachinedPart);
    }

    /**
     * 修改机加工件
     *
     * @param materialMachinedPart 机加工件
     * @return 结果
     */
    @Override
    public int updateMaterialMachinedPart(MaterialMachinedPart materialMachinedPart) {

        return materialMachinedPartMapper.updateMaterialMachinedPart(materialMachinedPart);
    }

    /**
     * 批量删除机加工件
     *
     * @param ids 需要删除的机加工件主键
     * @return 结果
     */
    @Override
    public int deleteMaterialMachinedPartByIds(Long[] ids) {
        return materialMachinedPartMapper.deleteMaterialMachinedPartByIds(ids);
    }

    /**
     * 删除机加工件信息
     *
     * @param id 机加工件主键
     * @return 结果
     */
    @Override
    public int deleteMaterialMachinedPartById(Long id) {
        return materialMachinedPartMapper.deleteMaterialMachinedPartById(id);
    }
}