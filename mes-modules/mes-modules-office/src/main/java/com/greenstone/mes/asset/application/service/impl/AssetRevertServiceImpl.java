package com.greenstone.mes.asset.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.asset.application.assembler.AssetAssembler;
import com.greenstone.mes.asset.application.assembler.AssetEventAssembler;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRevertCreateCmd;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRevertE;
import com.greenstone.mes.asset.application.dto.result.AssetRevertCreateR;
import com.greenstone.mes.asset.application.dto.result.AssetRevertR;
import com.greenstone.mes.asset.application.event.AssetRevertEvent;
import com.greenstone.mes.asset.application.service.AssetRevertService;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.domain.entity.AssetRevert;
import com.greenstone.mes.asset.domain.repository.AssetRepository;
import com.greenstone.mes.asset.domain.repository.AssetRevertRepository;
import com.greenstone.mes.asset.infrastructure.constant.AssetSnConst;
import com.greenstone.mes.asset.infrastructure.enums.AssetState;
import com.greenstone.mes.asset.infrastructure.util.ThrowUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.core.enums.AssetError;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.model.LoginUser;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/9 10:32
 */
@Service
public class AssetRevertServiceImpl implements AssetRevertService {

    private final AssetRepository assetRepository;
    private final AssetRevertRepository assetRevertRepository;
    private final AssetAssembler assetAssembler;
    private final AssetEventAssembler assetEventAssembler;
    private final RemoteUserService userService;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;

    public AssetRevertServiceImpl(AssetRepository assetRepository, AssetRevertRepository assetRevertRepository, AssetAssembler assetAssembler,
                                  AssetEventAssembler assetEventAssembler, RemoteUserService userService, RemoteSystemService systemService,
                                  ApplicationEventPublisher eventPublisher) {
        this.assetRepository = assetRepository;
        this.assetRevertRepository = assetRevertRepository;
        this.assetAssembler = assetAssembler;
        this.assetEventAssembler = assetEventAssembler;
        this.userService = userService;
        this.systemService = systemService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<AssetRevertR> list() {
        List<AssetRevert> reverts = assetRevertRepository.list();
        return assetAssembler.toAssetRevertRs(reverts);
    }

    @Transactional
    @Override
    public AssetRevertCreateR create(AssetRevertCreateCmd createCmd) {
        if (createCmd.getRevertedId() == null) {
            createCmd.setRevertedId(SecurityUtils.getLoginUser().getUserid());
        }
        AssetRevert revert = assetAssembler.toAssetRevert(createCmd);
        // 获取并校验：归还人
        String receivedBy = userService.getNickName(revert.getRevertedId(), SecurityConstants.INNER);
        ThrowUtil.nullThrow(receivedBy, AssetError.E64003);
        // 获取并校验：退库单号
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type(AssetSnConst.REVERT_SN_TYPE).prefix(AssetSnConst.REVERT_SN_PREFIX + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);
        ThrowUtil.trueThrow(serialNoR == null || serialNoR.getSerialNo() == null, AssetError.E64001);
        // 当前登陆人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 校验：资产应存在且处于在用状态
        List<Asset> assets = new ArrayList<>();
        for (Asset asset : revert.getAssets()) {
            Asset assetFound = assetRepository.getByBarCode(asset.getBarCode());
            ThrowUtil.nullThrow(assetFound, AssetError.E62004);
            ThrowUtil.trueThrow(assetFound.getState() != AssetState.IN_USE, AssetError.E64002);
            assets.add(assetFound);
        }
        // 赋值：设置属性
        revert.setAssets(assets);
        revert.setSerialNo(serialNoR.getSerialNo());
        revert.setRevertedBy(receivedBy);
        if (revert.getRevertedTime() == null) {
            revert.setRevertedTime(LocalDateTime.now());
        }
        revert.setOperatedId(loginUser.getUserid());
        revert.setOperatedBy(loginUser.getUsername());
        // 持久层：保存
        assetRevertRepository.save(revert);

        // 通知：退库单
        AssetRevertE revertEventData = assetEventAssembler.toAssetRevertEventData(revert);
        String change = StrUtil.format("使用人变更为'空'");
        revertEventData.setChangeContent(change);
        eventPublisher.publishEvent(new AssetRevertEvent(revertEventData));

        return AssetRevertCreateR.builder().serialNo(serialNoR.getSerialNo()).build();
    }


}
