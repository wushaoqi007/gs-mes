package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.infrastructure.mapper.BaseMaterialMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 物料配置Service业务层处理
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Slf4j
@Service
public class BaseMaterialServiceImpl extends ServiceImpl<BaseMaterialMapper, BaseMaterial> implements IBaseMaterialService {
    @Autowired
    private BaseMaterialMapper baseMaterialMapper;

    @Override
    public BaseMaterial getOrSave(BaseMaterial material) {
        BaseMaterial selectEntity = BaseMaterial.builder().code(material.getCode()).version(material.getVersion()).build();
        BaseMaterial existMaterial = this.getOneOnly(selectEntity);
        if (existMaterial == null) {
            this.save(material);
            return material;
        } else {
            return existMaterial;
        }
    }

    /**
     * 查询物料配置
     *
     * @param id 物料配置主键
     * @return 物料配置
     */
    @Override
    public BaseMaterial selectBaseMaterialById(Long id) {
        return baseMaterialMapper.selectBaseMaterialById(id);
    }

    /**
     * 查询物料配置列表
     *
     * @param baseMaterial 物料配置
     * @return 物料配置
     */
    @Override
    public List<BaseMaterial> selectBaseMaterialList(BaseMaterial baseMaterial) {
        return baseMaterialMapper.selectBaseMaterialList(baseMaterial);
    }

    @Override
    public BaseMaterial queryBaseMaterial(BaseMaterial baseMaterial) {
        QueryWrapper<BaseMaterial> query = Wrappers.query(baseMaterial);
        return getOneOnly(query);
    }

    /**
     * 新增物料配置
     *
     * @param baseMaterial 物料配置
     * @return 结果
     */
    @Override
    public BaseMaterial insertBaseMaterial(BaseMaterial baseMaterial, boolean updateSupport) {
        // 默认版本为V0
        if (Objects.isNull(baseMaterial.getVersion())) {
            baseMaterial.setVersion("V0");
        }
        // 添加设计者
        if (baseMaterial.getDesigner() == null) {
            baseMaterial.setDesigner(SecurityUtils.getLoginUser().getUser().getNickName());
        }

        if (updateSupport) {
            QueryWrapper<BaseMaterial> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BaseMaterial::getCode, baseMaterial.getCode()).eq(BaseMaterial::getVersion, baseMaterial.getVersion());
            log.info("saveOrUpdate material: {}", baseMaterial);
            saveOrUpdate(baseMaterial, queryWrapper);
            return getOneOnly(queryWrapper);
        } else {
            BaseMaterial checkEntity = BaseMaterial.builder().code(baseMaterial.getCode()).version(baseMaterial.getVersion()).build();
            BaseMaterial oneOnly = getOneOnly(checkEntity);
            if (Objects.nonNull(oneOnly)) {
                baseMaterial.setId(oneOnly.getId());
                log.info("存在相同物料，跳过新增{}", baseMaterial);
            } else {
                log.info("save material: {}", baseMaterial);
                super.save(baseMaterial);
            }
        }
        return baseMaterial;
    }

    /**
     * 修改物料配置
     *
     * @param baseMaterial 物料配置
     * @return 结果
     */
    @Override
    public int updateBaseMaterial(BaseMaterial baseMaterial) {
        return baseMaterialMapper.updateBaseMaterial(baseMaterial);
    }

    @Override
    public int updatePrice(BaseMaterial baseMaterial) {
        log.info("更新零件核算价格:{}", baseMaterial);
        if (StrUtil.isEmpty(baseMaterial.getCode()) || StrUtil.isEmpty(baseMaterial.getVersion())) {
            log.info("零件号和版本不为空：{}/{}", baseMaterial.getCode(), baseMaterial.getVersion());
            return 0;
        }
        if (Objects.isNull(baseMaterial.getPrice())) {
            log.info("更新的价格不为空，零件：{}/{}，价格:{}", baseMaterial.getCode(), baseMaterial.getVersion(), baseMaterial.getPrice());
            return 0;
        }
        return baseMaterialMapper.updatePrice(baseMaterial);
    }

    /**
     * 批量删除物料配置
     *
     * @param ids 需要删除的物料配置主键
     * @return 结果
     */
    @Override
    public int deleteBaseMaterialByIds(Long[] ids) {
        return baseMaterialMapper.deleteBaseMaterialByIds(ids);
    }

    /**
     * 删除物料配置信息
     *
     * @param id 物料配置主键
     * @return 结果
     */
    @Override
    public int deleteBaseMaterialById(Long id) {
        return baseMaterialMapper.deleteBaseMaterialById(id);
    }
}