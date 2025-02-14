package com.greenstone.mes.asset.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.asset.domain.converter.AssetConverter;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.domain.entity.AssetClear;
import com.greenstone.mes.asset.infrastructure.mapper.AssetClearDetailMapper;
import com.greenstone.mes.asset.infrastructure.mapper.AssetClearMapper;
import com.greenstone.mes.asset.infrastructure.persistence.AssetClearDO;
import com.greenstone.mes.asset.infrastructure.persistence.AssetClearDetailDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/22 13:53
 */
@Slf4j
@AllArgsConstructor
@Service
public class AssetClearRepository {

    private final AssetClearMapper clearMapper;
    private final AssetClearDetailMapper clearDetailMapper;
    private final AssetConverter converter;
    private final AssetRepository assetRepository;

    public AssetClear getBySn(String serialNo) {
        AssetClearDO select = clearMapper.getOneOnly(AssetClearDO.builder().serialNo(serialNo).build());
        return converter.toAssetClear(clearMapper.getOneOnly(select));
    }

    public List<AssetClear> clears() {
        List<AssetClear> clears = new ArrayList<>();
        List<AssetClearDO> clearDOs = clearMapper.selectList(null);
        for (AssetClearDO clearDO : clearDOs) {
            AssetClear assetClear = converter.toAssetClear(clearDO);
            assetClear.setAssets(findAssets(clearDO.getSerialNo()));
            clears.add(assetClear);
        }
        return clears;
    }

    public void save(AssetClear clear) {
        AssetClearDO clearIns = converter.toAssetClearDO(clear);
        List<AssetClearDetailDO> clearDetailIns = new ArrayList<>();
        for (Asset asset : clear.getAssets()) {
            clearDetailIns.add(AssetClearDetailDO.builder().serialNo(clear.getSerialNo()).barCode(asset.getBarCode()).build());
        }
        clearMapper.insert(clearIns);
        clear.setId(clearIns.getId());
        clearDetailMapper.insertBatchSomeColumn(clearDetailIns);
    }


    public void restore(List<String> serialNos) {
        LambdaQueryWrapper<AssetClearDO> wrapper = Wrappers.lambdaQuery(AssetClearDO.class).in(AssetClearDO::getSerialNo, serialNos);
        clearMapper.delete(wrapper);
    }

    public List<String> findClearBarCodes(String clearSerialNo) {
        List<AssetClearDetailDO> clearAssetDetails = clearDetailMapper.list(AssetClearDetailDO.builder().serialNo(clearSerialNo).build());
        return clearAssetDetails.stream().map(AssetClearDetailDO::getBarCode).toList();
    }

    private List<Asset> findAssets(String clearSerialNo) {
        List<String> barCodes = findClearBarCodes(clearSerialNo);
        List<Asset> assets = new ArrayList<>();
        for (String barCode : barCodes) {
            Asset asset = assetRepository.getByBarCode(barCode);
            assets.add(asset);
        }
        return assets;
    }

}
