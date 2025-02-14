package com.greenstone.mes.asset.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetFuzzyQuery;
import com.greenstone.mes.asset.domain.converter.AssetConverter;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.infrastructure.enums.AssetState;
import com.greenstone.mes.asset.infrastructure.mapper.AssetMapper;
import com.greenstone.mes.asset.infrastructure.persistence.AssetDO;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:45
 */
@Slf4j
@Service
public class AssetRepository {

    @Autowired
    private AssetConverter assetConverter;

    @Autowired
    private AssetMapper assetMapper;

    public List<Asset> list() {
        return assetConverter.toAssetList(assetMapper.list(null));
    }

    public List<Asset> fuzzyQuery(AssetFuzzyQuery fuzzyQuery) {
        QueryWrapper<AssetDO> fuzzyQueryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(fuzzyQuery.getKeyWord()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKeyWord());
                }
            });
            List<AssetState> stateList = AssetState.statesByName(fuzzyQuery.getKeyWord());
            if (CollUtil.isNotEmpty(stateList)) {
                fuzzyQueryWrapper.or().in("state", stateList);
            }
        }

        if (fuzzyQuery.getBillType() != null) {
            switch (fuzzyQuery.getBillType()) {
                case NONE -> {
                }
                case REQUISITION -> fuzzyQueryWrapper.eq("state", AssetState.IDLE);
                case REVERT -> fuzzyQueryWrapper.eq("state", AssetState.IN_USE);
            }
        }
        List<AssetDO> assetDOList = assetMapper.selectList(fuzzyQueryWrapper);
        return assetConverter.toAssetList(assetDOList);
    }

    public List<Asset> fuzzyQueryMyAsset(AssetFuzzyQuery fuzzyQuery) {
        QueryWrapper<AssetDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.eq("received_id", SecurityUtils.getLoginUser().getUser().getUserId());
        if (StrUtil.isNotEmpty(fuzzyQuery.getKeyWord()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKeyWord());
                }
            });
            List<AssetState> stateList = AssetState.statesByName(fuzzyQuery.getKeyWord());
            if (CollUtil.isNotEmpty(stateList)) {
                fuzzyQueryWrapper.or().in("state", stateList);
            }
        }
        List<AssetDO> assetDOList = assetMapper.selectList(fuzzyQueryWrapper);
        return assetConverter.toAssetList(assetDOList);
    }

    public void save(Asset asset, boolean isNew) {
        AssetDO assetDO = assetConverter.toAssetDO(asset);
        if (isNew) {
            assetDO.setCreateTime(LocalDateTime.now());
            assetDO.setCreateBy(SecurityUtils.getUsername());
            assetMapper.insert(assetDO);
        } else {
            assetDO.setUpdateTime(LocalDateTime.now());
            assetDO.setUpdateBy(SecurityUtils.getUsername());
            LambdaUpdateWrapper<AssetDO> queryWrapper = Wrappers.lambdaUpdate(AssetDO.class)
                    .eq(AssetDO::getBarCode, asset.getBarCode())
                    .set(assetDO.getReceivedId() == null, AssetDO::getReceivedId, null)
                    .set(assetDO.getReceivedId() == null, AssetDO::getReceivedBy, null)
                    .set(assetDO.getReceivedId() == null, AssetDO::getReceivedTime, null);
            assetMapper.update(assetDO, queryWrapper);
        }
    }

    public void clear(List<String> barCodes) {
        LambdaQueryWrapper<AssetDO> wrapper = Wrappers.lambdaQuery(AssetDO.class).in(AssetDO::getBarCode, barCodes);
        assetMapper.delete(wrapper);
    }

    public void restore(List<String> barCodes) {
        LambdaQueryWrapper<AssetDO> wrapper = Wrappers.lambdaQuery(AssetDO.class).in(AssetDO::getBarCode, barCodes);
        assetMapper.recoverBatch(wrapper);
    }

    public void requisition(List<Asset> assets) {
        for (Asset asset : assets) {
            LambdaQueryWrapper<AssetDO> wrapper = Wrappers.lambdaQuery(AssetDO.class).eq(AssetDO::getBarCode, asset.getBarCode());
            assetMapper.update(assetConverter.toAssetDO(asset), wrapper);
        }
    }

    public void revert(List<Asset> assets) {
        for (Asset asset : assets) {
            LambdaUpdateWrapper<AssetDO> updateWrapper = Wrappers.lambdaUpdate(AssetDO.class)
                    .eq(AssetDO::getBarCode, asset.getBarCode())
                    .set(AssetDO::getState, asset.getState())
                    .set(AssetDO::getReceivedId, null)
                    .set(AssetDO::getReceivedBy, null)
                    .set(AssetDO::getReceivedTime, null)
                    .set(AssetDO::getBillSn, null);
            assetMapper.update(updateWrapper);
        }
    }

    public void updateType(Asset asset) {
        LambdaQueryWrapper<AssetDO> wrapper = Wrappers.lambdaQuery(AssetDO.class).eq(AssetDO::getTypeId, asset.getTypeId());
        assetMapper.update(assetConverter.toAssetDO(asset), wrapper);
    }

    public void moveToSubType(Asset asset, String parentTypeCode) {
        LambdaQueryWrapper<AssetDO> wrapper = Wrappers.lambdaQuery(AssetDO.class).eq(AssetDO::getTypeCode, parentTypeCode);
        assetMapper.update(assetConverter.toAssetDO(asset), wrapper);
    }

    public Asset getByBarCode(String barCode) {
        LambdaQueryWrapper<AssetDO> wrapper = Wrappers.lambdaQuery(AssetDO.class).eq(AssetDO::getBarCode, barCode);
        AssetDO assetDO = assetMapper.findOneWithOutLogic(wrapper);
        return assetConverter.toAsset(assetDO);
    }

    public boolean existById(Long id) {
        AssetDO assetDO = assetMapper.selectById(id);
        return assetDO != null;
    }

    public boolean existByType(Long typeId) {
        AssetDO assetDO = assetMapper.getOneOnly(AssetDO.builder().typeId(typeId).build());
        return assetDO != null;
    }

}
