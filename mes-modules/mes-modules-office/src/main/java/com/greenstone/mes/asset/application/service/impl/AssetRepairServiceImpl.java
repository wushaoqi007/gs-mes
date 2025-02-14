package com.greenstone.mes.asset.application.service.impl;

import com.greenstone.mes.asset.application.assembler.AssetRepairAssembler;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairAddCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairEditCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairStatusChangeCmd;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetFuzzyQuery;
import com.greenstone.mes.asset.application.dto.result.AssetRepairR;
import com.greenstone.mes.asset.application.service.AssetRepairService;
import com.greenstone.mes.asset.domain.entity.AssetRepair;
import com.greenstone.mes.asset.domain.repository.AssetRepairRepository;
import com.greenstone.mes.asset.infrastructure.constant.AssetSnConst;
import com.greenstone.mes.asset.infrastructure.enums.AssetRepairStatus;
import com.greenstone.mes.asset.infrastructure.util.ThrowUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.AssetError;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-05-31-15:22
 */
@AllArgsConstructor
@Slf4j
@Service
public class AssetRepairServiceImpl implements AssetRepairService {

    private final AssetRepairRepository assetRepairRepository;
    private final AssetRepairAssembler assetRepairAssembler;
    private final RemoteSystemService systemService;

    @Override
    public List<AssetRepairR> list(AssetFuzzyQuery query) {
        List<AssetRepair> list = assetRepairRepository.list(query);
        return assetRepairAssembler.toAssetRepairRList(list);
    }

    @Override
    public AssetRepairR detail(String id) {
        AssetRepair assetRepair = assetRepairRepository.detail(id);
        return assetRepairAssembler.toAssetRepairR(assetRepair);
    }

    @Override
    public void save(AssetRepairAddCmd addCmd) {
        AssetRepair assetRepair = assetRepairAssembler.toAssetRepair(addCmd);
        // 获取并校验：领用单号
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type(AssetSnConst.REPAIR_SN_TYPE).prefix(AssetSnConst.REPAIR_SN_PREFIX + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);
        ThrowUtil.trueThrow(serialNoR == null || serialNoR.getSerialNo() == null, AssetError.E65002);

        assetRepair.setSerialNo(serialNoR.getSerialNo());
        if (assetRepair.getStatus() == null) {
            assetRepair.setStatus(AssetRepairStatus.REPAIRING);
        }
        assetRepair.setHandleById(SecurityUtils.getLoginUser().getUser().getUserId());
        assetRepair.setHandleBy(SecurityUtils.getLoginUser().getUser().getNickName());
        assetRepair.setAssets(assetRepairAssembler.toRepairAssetListFromAdd(addCmd.getAssets()));

        assetRepairRepository.save(assetRepair);
    }

    @Override
    public void update(AssetRepairEditCmd editCmd) {
        AssetRepair assetRepair = assetRepairAssembler.toAssetRepair(editCmd);
        if (assetRepair.getStatus() == null) {
            assetRepair.setStatus(AssetRepairStatus.REPAIRING);
        }
        assetRepair.setHandleById(SecurityUtils.getLoginUser().getUser().getUserId());
        assetRepair.setHandleBy(SecurityUtils.getLoginUser().getUser().getNickName());
        assetRepair.setAssets(assetRepairAssembler.toRepairAssetListFromEdit(editCmd.getAssets()));

        assetRepairRepository.update(assetRepair);
    }

    @Override
    public void remove(List<String> serialNos) {
        assetRepairRepository.remove(serialNos);
    }

    @Override
    public void statusChange(AssetRepairStatusChangeCmd statusChangeCmd) {
        assetRepairRepository.statusChange(statusChangeCmd);
    }
}
