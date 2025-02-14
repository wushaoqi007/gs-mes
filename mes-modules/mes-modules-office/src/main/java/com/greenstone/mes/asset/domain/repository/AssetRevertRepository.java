package com.greenstone.mes.asset.domain.repository;

import com.greenstone.mes.asset.domain.converter.AssetConverter;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.domain.entity.AssetRevert;
import com.greenstone.mes.asset.infrastructure.mapper.AssetRevertDetailMapper;
import com.greenstone.mes.asset.infrastructure.mapper.AssetRevertMapper;
import com.greenstone.mes.asset.infrastructure.persistence.AssetRevertDO;
import com.greenstone.mes.asset.infrastructure.persistence.AssetRevertDetailDO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/9 10:19
 */
@Service
public class AssetRevertRepository {

    private final AssetRevertMapper assetRevertMapper;
    private final AssetRevertDetailMapper assetRevertDetailMapper;
    private final AssetRepository assetRepository;
    private final AssetConverter assetConverter;

    public AssetRevertRepository(AssetRevertMapper assetRevertMapper, AssetRevertDetailMapper assetRevertDetailMapper,
                                 AssetRepository assetRepository, AssetConverter assetConverter) {
        this.assetRevertMapper = assetRevertMapper;
        this.assetRevertDetailMapper = assetRevertDetailMapper;
        this.assetRepository = assetRepository;
        this.assetConverter = assetConverter;
    }

    public List<AssetRevert> list() {
        List<AssetRevertDO> reqsDOList = assetRevertMapper.list(null);
        List<AssetRevert> assetReverts = assetConverter.toAssetRevertList(reqsDOList);

        for (AssetRevert revert : assetReverts) {
            List<Asset> assets = new ArrayList<>();
            revert.setAssets(assets);
            List<AssetRevertDetailDO> detailDOList =
                    assetRevertDetailMapper.list(AssetRevertDetailDO.builder().serialNo(revert.getSerialNo()).build());
            for (AssetRevertDetailDO detailDO : detailDOList) {
                Asset asset = assetRepository.getByBarCode(detailDO.getBarCode());
                assets.add(asset);
            }
        }
        return assetReverts;
    }

    public void save(AssetRevert revert) {
        AssetRevertDO assetRevertDO = assetConverter.toAssetRevertDO(revert);

        assetRevertMapper.insert(assetRevertDO);
        revert.setId(assetRevertDO.getId());

        List<AssetRevertDetailDO> revertDetailDOList = new ArrayList<>();
        for (Asset asset : revert.getAssets()) {
            revertDetailDOList.add(AssetRevertDetailDO.builder().serialNo(revert.getSerialNo()).barCode(asset.getBarCode()).build());
        }
        assetRevertDetailMapper.insertBatchSomeColumn(revertDetailDOList);
    }

}
