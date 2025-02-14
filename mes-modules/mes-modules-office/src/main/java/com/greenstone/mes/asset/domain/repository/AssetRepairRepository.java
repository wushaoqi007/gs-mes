package com.greenstone.mes.asset.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairStatusChangeCmd;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetFuzzyQuery;
import com.greenstone.mes.asset.domain.converter.AssetRepairConverter;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.domain.entity.AssetRepair;
import com.greenstone.mes.asset.infrastructure.mapper.AssetRepairDetailMapper;
import com.greenstone.mes.asset.infrastructure.mapper.AssetRepairMapper;
import com.greenstone.mes.asset.infrastructure.persistence.AssetRepairDO;
import com.greenstone.mes.asset.infrastructure.persistence.AssetRepairDetailDO;
import com.greenstone.mes.asset.infrastructure.util.ThrowUtil;
import com.greenstone.mes.common.core.enums.AssetError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class AssetRepairRepository {

    private final AssetRepairMapper assetRepairMapper;
    private final AssetRepairDetailMapper assetRepairDetailMapper;
    private final AssetRepository assetRepository;
    private final AssetRepairConverter assetRepairConverter;

    public List<AssetRepair> list(AssetFuzzyQuery query) {
        List<AssetRepair> assetRepairs = assetRepairMapper.selectByFuzzyQuery(query);
        for (AssetRepair repair : assetRepairs) {
            selectAssetRepairDetail(repair);
        }
        return assetRepairs;
    }

    public AssetRepair detail(String id) {
        AssetRepairDO assetRepairDO = assetRepairMapper.selectById(id);
        ThrowUtil.nullThrow(assetRepairDO, AssetError.E65001);
        AssetRepair assetRepair = assetRepairConverter.do2Entity(assetRepairDO);
        selectAssetRepairDetail(assetRepair);
        return assetRepair;
    }

    public void selectAssetRepairDetail(AssetRepair assetRepair) {
        List<Asset> assets = new ArrayList<>();
        assetRepair.setAssets(assets);
        List<AssetRepairDetailDO> detailDOList =
                assetRepairDetailMapper.list(AssetRepairDetailDO.builder().serialNo(assetRepair.getSerialNo()).build());
        for (AssetRepairDetailDO detailDO : detailDOList) {
            Asset asset = assetRepository.getByBarCode(detailDO.getBarCode());
            assets.add(asset);
        }
    }

    public void save(AssetRepair repair) {
        AssetRepairDO assetRepairDO = assetRepairConverter.entity2Do(repair);
        assetRepairMapper.insert(assetRepairDO);

        insertAssetRepairDetail(repair);
    }

    public void update(AssetRepair repair) {
        AssetRepairDO find = assetRepairMapper.selectById(repair.getId());
        ThrowUtil.nullThrow(find, AssetError.E65001);
        repair.setSerialNo(find.getSerialNo());

        AssetRepairDO assetRepairDO = assetRepairConverter.entity2Do(repair);
        assetRepairMapper.updateById(assetRepairDO);

        assetRepairDetailMapper.delete(Wrappers.query(AssetRepairDetailDO.builder().serialNo(assetRepairDO.getSerialNo()).build()));
        insertAssetRepairDetail(repair);
    }

    public void insertAssetRepairDetail(AssetRepair repair) {
        List<AssetRepairDetailDO> repairDetailDOList = new ArrayList<>();
        for (Asset asset : repair.getAssets()) {
            Asset assetFound = assetRepository.getByBarCode(asset.getBarCode());
            ThrowUtil.nullThrow(assetFound, AssetError.E62004);
            repairDetailDOList.add(AssetRepairDetailDO.builder().serialNo(repair.getSerialNo()).barCode(asset.getBarCode()).build());
        }
        assetRepairDetailMapper.insertBatchSomeColumn(repairDetailDOList);
    }

    public void remove(List<String> serialNos) {
        for (String serialNo : serialNos) {
            AssetRepairDO revokeFound = assetRepairMapper.getOneOnly(AssetRepairDO.builder().serialNo(serialNo).build());
            ThrowUtil.nullThrow(revokeFound, AssetError.E65001);
        }
        LambdaQueryWrapper<AssetRepairDO> revokeWrapper = Wrappers.lambdaQuery(AssetRepairDO.class).in(AssetRepairDO::getSerialNo, serialNos);
        assetRepairMapper.delete(revokeWrapper);
        LambdaQueryWrapper<AssetRepairDetailDO> detailWrapper = Wrappers.lambdaQuery(AssetRepairDetailDO.class).in(AssetRepairDetailDO::getSerialNo,
                serialNos);
        assetRepairDetailMapper.delete(detailWrapper);
    }

    public void statusChange(AssetRepairStatusChangeCmd statusChangeCmd) {
        LambdaUpdateWrapper<AssetRepairDO> updateWrapper = Wrappers.lambdaUpdate(AssetRepairDO.class).set(AssetRepairDO::getStatus, statusChangeCmd.getStatus())
                .in(AssetRepairDO::getSerialNo, statusChangeCmd.getSerialNos());
        assetRepairMapper.update(updateWrapper);
    }

}
