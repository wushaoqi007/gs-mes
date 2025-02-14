package com.greenstone.mes.asset.application.service.impl;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetClearCreateCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetClearRestoreCmd;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetClearE;
import com.greenstone.mes.asset.application.event.AssetClearEvent;
import com.greenstone.mes.asset.application.service.AssetClearService;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.domain.entity.AssetClear;
import com.greenstone.mes.asset.domain.repository.AssetClearRepository;
import com.greenstone.mes.asset.domain.repository.AssetRepository;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.AssetError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class AssetClearServiceImpl implements AssetClearService {

    private final AssetRepository assetRepository;
    private final AssetClearRepository assetClearRepository;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher publisher;

    @Override
    public List<AssetClear> clears() {
        return assetClearRepository.clears();
    }

    @Transactional
    @Override
    public void save(AssetClearCreateCmd createCmd) {
        List<Asset> assets = new ArrayList<>();
        for (AssetClearCreateCmd.ClearAsset asset : createCmd.getAssets()) {
            Asset existingAsset = assetRepository.getByBarCode(asset.getBarCode());
            if (existingAsset == null) {
                throw new ServiceException(AssetError.E62004, "资产编号： " + asset.getBarCode());
            }
            assets.add(existingAsset);
        }
        SerialNoNextCmd nextCmd = SerialNoNextCmd.builder().type("asset_clear").prefix("ACR" + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);
        User currUser = SecurityUtils.getLoginUser().getUser();
        AssetClear assetClear = AssetClear.builder().clearBy(currUser.getUserId())
                .serialNo(serialNoR.getSerialNo())
                .clearByName(currUser.getNickName())
                .clearTime(createCmd.getClearTime())
                .assets(assets)
                .remark(createCmd.getRemark()).build();
        assetClearRepository.save(assetClear);

        List<String> barCodes = createCmd.getAssets().stream().map(AssetClearCreateCmd.ClearAsset::getBarCode).toList();
        assetRepository.clear(barCodes);

        // 记录资产履历
        AssetClearE clearE = AssetClearE.builder().billId(assetClear.getId())
                .clearBy(assetClear.getClearBy())
                .clearByName(assetClear.getClearByName())
                .clearTime(assetClear.getClearTime())
                .restore(false)
                .assets(assets)
                .serialNo(assetClear.getSerialNo())
                .changeContent("资产被清理；状态变更为'报废'").build();
        publisher.publishEvent(new AssetClearEvent(clearE));
    }

    @Transactional
    @Override
    public void restore(AssetClearRestoreCmd restoreCmd) {
        assetClearRepository.restore(restoreCmd.getSerialNos());
        for (String serialNo : restoreCmd.getSerialNos()) {
            AssetClear assetClear = assetClearRepository.getBySn(serialNo);
            List<String> clearBarCodes = assetClearRepository.findClearBarCodes(serialNo);
            assetRepository.restore(clearBarCodes);

            // 记录资产履历
            AssetClearE clearE = AssetClearE.builder().billId(assetClear.getId())
                    .clearBy(assetClear.getClearBy())
                    .clearByName(assetClear.getClearByName())
                    .clearTime(assetClear.getClearTime())
                    .restore(false)
                    .assets(clearBarCodes.stream().map(bc -> Asset.builder().barCode(bc).build()).toList())
                    .serialNo(assetClear.getSerialNo())
                    .changeContent("资产被还原；状态被还原").build();
            publisher.publishEvent(new AssetClearEvent(clearE));
        }

    }

}