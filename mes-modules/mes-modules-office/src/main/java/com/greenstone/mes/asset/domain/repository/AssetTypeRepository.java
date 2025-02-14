package com.greenstone.mes.asset.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetTypeTreeListQuery;
import com.greenstone.mes.asset.domain.converter.AssetConverter;
import com.greenstone.mes.asset.domain.entity.AssetType;
import com.greenstone.mes.asset.infrastructure.mapper.AssetTypeMapper;
import com.greenstone.mes.asset.infrastructure.persistence.AssetTypeDO;
import com.greenstone.mes.common.core.enums.AssetError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author gu_renkai
 * @date 2023/1/30 15:01
 */
@Slf4j
@Service
public class AssetTypeRepository {

    @Autowired
    private AssetTypeMapper assetTypeMapper;

    @Autowired
    private AssetSpecRepository assetSpecRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetConverter assetConverter;

    public List<AssetType> listAll() {
        return assetConverter.toAssetTypeList(assetTypeMapper.selectList(null));
    }


    public List<AssetType> list(AssetTypeTreeListQuery query) {
        LambdaQueryWrapper<AssetTypeDO> queryWrapper = Wrappers.lambdaQuery(AssetTypeDO.class);
        Optional.of(query.getParentTypeCode()).ifPresentOrElse(ptc -> queryWrapper.eq(AssetTypeDO::getParentTypeCode, ptc),
                () -> queryWrapper.isNull(AssetTypeDO::getParentTypeCode));
        return assetConverter.toAssetTypeList(assetTypeMapper.selectList(queryWrapper));
    }

    public void save(AssetType assetType, boolean isNew) {
        AssetTypeDO assetTypeDO = assetConverter.toAssetTypeDO(assetType);
        if (isNew) {
            assetTypeMapper.insert(assetTypeDO);
            assetType.setId(assetTypeDO.getId());
        } else {
            assetTypeMapper.updateById(assetTypeDO);
        }
    }

    public void updateChildren(List<AssetType> children) {
        for (AssetType child : children) {
            assetTypeMapper.updateById(assetConverter.toAssetTypeDO(child));
        }
    }

    public void remove(String typeCode) {
        // 校验：资产分类是否存在
        AssetTypeDO self = assetTypeMapper.getOneOnly(AssetTypeDO.builder().typeCode(typeCode).build());
        if (self == null) {
            throw new ServiceException(AssetError.E60109);
        }
        // 校验：是否被规格型号引用
        boolean existSpecByType = assetSpecRepository.existByTypeCode(self.getTypeCode());
        if (existSpecByType) {
            throw new ServiceException(AssetError.E60107);
        }
        // 校验：是否被资产数据引用
        if (assetRepository.existByType(self.getId())) {
            throw new ServiceException(AssetError.E60108);
        }
        // 校验：是否有子分类
        if (haveChildren(self.getTypeCode())) {
            throw new ServiceException(AssetError.E60110);
        }
        assetTypeMapper.deleteById(self.getId());
    }

    public AssetType getById(Long id) {
        return assetConverter.toAssetType(assetTypeMapper.selectById(id));
    }

    public AssetType getByCode(String typeCode) {
        AssetTypeDO typeDO = assetTypeMapper.getOneOnly(AssetTypeDO.builder().typeCode(typeCode).build());
        return assetConverter.toAssetType(typeDO);
    }

    public boolean haveChildren(String typeCode) {
        return null != assetTypeMapper.getOneOnly(AssetTypeDO.builder().parentTypeCode(typeCode).build());
    }

    public Long countChildren(String parentTypeCode) {
        return assetTypeMapper.selectCount(AssetTypeDO.builder().parentTypeCode(parentTypeCode).build());
    }

    public List<AssetType> listChildren(String parentTypeCode) {
        List<AssetTypeDO> children = assetTypeMapper.list(AssetTypeDO.builder().parentTypeCode(parentTypeCode).build());
        return assetConverter.toAssetTypeList(children);
    }

    public AssetType getByTypeName(String typeName) {
        AssetTypeDO assetTypeDO = assetTypeMapper.getOneOnly(AssetTypeDO.builder().typeName(typeName).build());
        return assetConverter.toAssetType(assetTypeDO);
    }

}
