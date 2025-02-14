package com.greenstone.mes.asset.domain.repository;

import com.greenstone.mes.asset.domain.converter.AssetConverter;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.domain.entity.AssetRequisition;
import com.greenstone.mes.asset.infrastructure.enums.AssetState;
import com.greenstone.mes.asset.infrastructure.mapper.AssetReqsDetailMapper;
import com.greenstone.mes.asset.infrastructure.mapper.AssetReqsMapper;
import com.greenstone.mes.asset.infrastructure.persistence.AssetReqsDO;
import com.greenstone.mes.asset.infrastructure.persistence.AssetReqsDetailDO;
import com.greenstone.mes.asset.infrastructure.util.ThrowUtil;
import com.greenstone.mes.common.core.enums.AssetError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/6 9:32
 */

@Service
public class AssetReqsRepository {

    private final AssetReqsMapper assetReqsMapper;

    private final AssetReqsDetailMapper assetReqsDetailMapper;

    private final AssetRepository assetRepository;

    private final AssetConverter assetConverter;

    public AssetReqsRepository(AssetReqsMapper assetReqsMapper, AssetReqsDetailMapper assetReqsDetailMapper, AssetRepository assetRepository,
                               AssetConverter assetConverter) {
        this.assetReqsMapper = assetReqsMapper;
        this.assetReqsDetailMapper = assetReqsDetailMapper;
        this.assetRepository = assetRepository;
        this.assetConverter = assetConverter;
    }

    public List<AssetRequisition> list() {
        List<AssetReqsDO> reqsDOList = assetReqsMapper.list(null);
        List<AssetRequisition> assetRequisitions = assetConverter.toAssetRequisitionList(reqsDOList);

        for (AssetRequisition requisition : assetRequisitions) {
            List<Asset> assets = new ArrayList<>();
            requisition.setAssets(assets);
            List<AssetReqsDetailDO> detailDOList =
                    assetReqsDetailMapper.list(AssetReqsDetailDO.builder().serialNo(requisition.getSerialNo()).build());
            for (AssetReqsDetailDO detailDO : detailDOList) {
                Asset asset = assetRepository.getByBarCode(detailDO.getBarCode());
                assets.add(asset);
            }
        }
        return assetRequisitions;
    }

    public void save(AssetRequisition requisition) {
        AssetReqsDO assetReqsDO = assetConverter.toAssetReqsDO(requisition);

        assetReqsMapper.insert(assetReqsDO);
        requisition.setId(assetReqsDO.getId());

        List<AssetReqsDetailDO> reqsDetailDOList = new ArrayList<>();
        for (Asset asset : requisition.getAssets()) {
            Asset assetFound = assetRepository.getByBarCode(asset.getBarCode());
            ThrowUtil.nullThrow(assetFound, AssetError.E62004);
            ThrowUtil.trueThrow(assetFound.getState() != AssetState.IDLE, AssetError.E63002);
            reqsDetailDOList.add(AssetReqsDetailDO.builder().serialNo(assetReqsDO.getSerialNo()).barCode(asset.getBarCode()).build());
        }
        assetReqsDetailMapper.insertBatchSomeColumn(reqsDetailDOList);
    }

}
