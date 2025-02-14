package com.greenstone.mes.asset.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.asset.application.assembler.AssetAssembler;
import com.greenstone.mes.asset.application.assembler.AssetEventAssembler;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetReqsCreateCmd;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRequisitionE;
import com.greenstone.mes.asset.application.dto.result.AssetReqsCreateR;
import com.greenstone.mes.asset.application.dto.result.AssetRequisitionR;
import com.greenstone.mes.asset.application.event.AssetRequisitionEvent;
import com.greenstone.mes.asset.application.service.AssetReqsService;
import com.greenstone.mes.asset.domain.entity.AssetRequisition;
import com.greenstone.mes.asset.domain.repository.AssetReqsRepository;
import com.greenstone.mes.asset.infrastructure.constant.AssetSnConst;
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
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/6 13:40
 */
@Service
public class AssetReqsServiceImpl implements AssetReqsService {

    private final AssetReqsRepository assetReqsRepository;
    private final RemoteSystemService systemService;
    private final RemoteUserService userService;
    private final AssetAssembler assetAssembler;
    private final AssetEventAssembler assetEventAssembler;
    private final ApplicationEventPublisher eventPublisher;

    public AssetReqsServiceImpl(AssetReqsRepository assetReqsRepository, RemoteSystemService systemService, RemoteUserService userService,
                                AssetAssembler assetAssembler, AssetEventAssembler assetEventAssembler, ApplicationEventPublisher eventPublisher) {
        this.assetReqsRepository = assetReqsRepository;
        this.systemService = systemService;
        this.userService = userService;
        this.assetAssembler = assetAssembler;
        this.assetEventAssembler = assetEventAssembler;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<AssetRequisitionR> list() {
        List<AssetRequisition> requisitions = assetReqsRepository.list();
        return assetAssembler.toAssetRequisitionRs(requisitions);
    }

    @Transactional
    @Override
    public AssetReqsCreateR create(AssetReqsCreateCmd saveCmd) {
        AssetRequisition requisition = assetAssembler.toAssetRequisition(saveCmd);
        // 获取并校验：领用人
        String receivedBy = userService.getNickName(requisition.getReceivedId(), SecurityConstants.INNER);
        ThrowUtil.nullThrow(receivedBy, AssetError.E62002);
        // 获取并校验：领用单号
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type(AssetSnConst.REQUISITION_SN_TYPE).prefix(AssetSnConst.REQUISITION_SN_PREFIX + DateUtil.dateSerialStrNow()).build();
        SerialNoR serialNoR = systemService.getNextSn(nextCmd);
        ThrowUtil.trueThrow(serialNoR == null || serialNoR.getSerialNo() == null, AssetError.E63001);

        LoginUser loginUser = SecurityUtils.getLoginUser();

        requisition.setSerialNo(serialNoR.getSerialNo());
        requisition.setReceivedBy(receivedBy);
        if (requisition.getReceivedTime() == null) {
            requisition.setReceivedTime(LocalDateTime.now());
        }
        requisition.setOperatedId(loginUser.getUserid());
        requisition.setOperatedBy(loginUser.getUsername());
        requisition.setAssets(assetAssembler.toReqsAssetList(saveCmd.getAssets()));

        assetReqsRepository.save(requisition);

        // 通知：领用单
        AssetRequisitionE requisitionEvent = assetEventAssembler.toAssetRequisitionEventData(requisition);
        String change = StrUtil.format("使用人变更为'{}'", receivedBy);
        requisitionEvent.setChangeContent(change);
        eventPublisher.publishEvent(new AssetRequisitionEvent(requisitionEvent));

        return AssetReqsCreateR.builder().serialNo(serialNoR.getSerialNo()).build();
    }
}
