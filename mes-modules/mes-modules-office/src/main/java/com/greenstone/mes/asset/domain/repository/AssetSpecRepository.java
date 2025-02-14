package com.greenstone.mes.asset.domain.repository;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.asset.domain.converter.AssetConverter;
import com.greenstone.mes.asset.domain.entity.AssetSpec;
import com.greenstone.mes.asset.infrastructure.mapper.AssetSpecMapper;
import com.greenstone.mes.asset.infrastructure.persistence.AssetSpecDO;
import com.greenstone.mes.asset.infrastructure.util.ThrowUtil;
import com.greenstone.mes.common.core.enums.AssetError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/3 8:26
 */
@Slf4j
@Service
public class AssetSpecRepository {

    @Autowired
    private AssetSpecMapper specMapper;

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    @Autowired
    private AssetConverter assetConverter;

    public List<AssetSpec> listByTypeCode(String typeCode) {
        List<AssetSpecDO> doList = specMapper.list(AssetSpecDO.builder().typeCode(typeCode).build());
        return assetConverter.toAssetSpecList(doList);
    }

    public void save(AssetSpec spec) {
        boolean isNewOne = spec.getId() == null;
        // 新增规格型号
        if (isNewOne) {
            // 校验：资产分类是否存在
            ThrowUtil.nullThrow(assetTypeRepository.getByCode(spec.getTypeCode()), AssetError.E60101);

            specMapper.insert(assetConverter.toSpecInsertDO(spec));
        }
        // 更新规格型号
        else {
            // 校验：修改的数据是否存在
            ThrowUtil.nullThrow(specMapper.selectById(spec.getId()), AssetError.E61001);

            specMapper.updateById(assetConverter.toSpecUpdateDO(spec));
        }
    }

    public void remove(Long id) {
        // 校验：删除的数据是否存在
        ThrowUtil.nullThrow(specMapper.selectById(id), AssetError.E61001);
        specMapper.deleteById(id);
    }

    public void moveTo(String fromTypeCode, String toTypeCode) {
        LambdaUpdateWrapper<AssetSpecDO> updateWrapper =
                Wrappers.lambdaUpdate(AssetSpecDO.class).eq(AssetSpecDO::getTypeCode, fromTypeCode).set(AssetSpecDO::getTypeCode, toTypeCode);
        specMapper.update(updateWrapper);
    }

    public boolean existByTypeCode(String typeCode) {
        return specMapper.exists(AssetSpecDO.builder().typeCode(typeCode).build());
    }

}
