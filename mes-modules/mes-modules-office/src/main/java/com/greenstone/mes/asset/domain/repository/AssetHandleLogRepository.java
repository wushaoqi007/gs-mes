package com.greenstone.mes.asset.domain.repository;

import com.greenstone.mes.asset.domain.converter.AssetConverter;
import com.greenstone.mes.asset.domain.entity.AssetHandleLog;
import com.greenstone.mes.asset.infrastructure.mapper.AssetHandleLogMapper;
import com.greenstone.mes.asset.infrastructure.persistence.AssetHandleLogDO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:16
 */
@Service
public class AssetHandleLogRepository {

    private AssetHandleLogMapper assetHandleLogMapper;

    private AssetConverter assetConverter;

    public AssetHandleLogRepository(AssetHandleLogMapper assetHandleLogMapper, AssetConverter assetConverter) {
        this.assetHandleLogMapper = assetHandleLogMapper;
        this.assetConverter = assetConverter;
    }

    public List<AssetHandleLog> list(String barCode) {
        List<AssetHandleLogDO> logDOS = assetHandleLogMapper.list(AssetHandleLogDO.builder().barCode(barCode).build());
        return assetConverter.toAssetHandleLogs(logDOS);
    }

    public void save(AssetHandleLog assetHandleLog) {
        assetHandleLogMapper.insert(assetConverter.toAssetHandleLogDO(assetHandleLog));
    }

    public void save(List<AssetHandleLog> assetHandleLogs) {
        assetHandleLogMapper.insertBatchSomeColumn(assetConverter.toAssetHandleLogDOList(assetHandleLogs));
    }

}
