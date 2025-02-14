package com.greenstone.mes.asset.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.asset.application.assembler.AssetAssembler;
import com.greenstone.mes.asset.application.assembler.AssetEventAssembler;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetDeleteCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetImportCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetInsertCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetUpdateCmd;
import com.greenstone.mes.asset.application.dto.cqe.event.*;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetFuzzyQuery;
import com.greenstone.mes.asset.application.dto.result.AssetExportResult;
import com.greenstone.mes.asset.application.event.AssetEditEvent;
import com.greenstone.mes.asset.application.service.AssetService;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.domain.entity.AssetType;
import com.greenstone.mes.asset.domain.repository.AssetRepository;
import com.greenstone.mes.asset.domain.repository.AssetTypeRepository;
import com.greenstone.mes.asset.infrastructure.constant.AssetSnConst;
import com.greenstone.mes.asset.infrastructure.enums.AssetState;
import com.greenstone.mes.asset.infrastructure.util.ThrowUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.core.enums.AssetError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author gu_renkai
 * @date 2023/1/31 17:05
 */
@AllArgsConstructor
@Slf4j
@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final AssetAssembler assetAssembler;
    private final AssetEventAssembler assetEventAssembler;
    private final RemoteSystemService systemService;
    private final RemoteUserService userService;
    private final AssetTypeRepository assetTypeRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    public List<Asset> list() {
        List<Asset> list = assetRepository.list();
        for (Asset asset : list) {
            if (asset.getReceivedId() != null) {
                asset.setReceivedByUser(userService.getById(asset.getReceivedId()));
            }
        }
        return list;
    }

    @Override
    public List<Asset> queryMyAsset(AssetFuzzyQuery query) {
        return assetRepository.fuzzyQueryMyAsset(query);
    }

    @Override
    public List<Asset> fuzzyQuery(AssetFuzzyQuery query) {
        return assetRepository.fuzzyQuery(query);
    }

    @Transactional
    @Override
    public void insert(AssetInsertCmd saveCmd) {
        Asset asset = assetAssembler.toAsset(saveCmd);
        save(asset, true);
    }

    @Transactional
    @Override
    public void update(AssetUpdateCmd updateCmd) {
        Asset asset = assetAssembler.toAsset(updateCmd);
        Asset oldAsset = assetRepository.getByBarCode(updateCmd.getBarCode());
        save(asset, false);
        if (!Objects.equals(oldAsset.getReceivedId(), asset.getReceivedId())) {
            AssetEditE assetEditE = AssetEditE.builder().barCode(asset.getBarCode())
                    .editedBy(SecurityUtils.getLoginUser().getUser().getUserId())
                    .editedByName(SecurityUtils.getLoginUser().getUser().getNickName())
                    .editedTime(LocalDateTime.now())
                    .changeContent(StrUtil.format("使用人由'{}'变更为'{}'", oldAsset.getReceivedBy(), asset.getReceivedBy())).build();
            publisher.publishEvent(new AssetEditEvent(assetEditE));
        }
    }

    @Transactional
    @Override
    public void remove(AssetDeleteCmd deleteCmd) {
        assetRepository.clear(deleteCmd.getBarCodes());
    }

    private void save(Asset asset, boolean isNew) {
        // 校验：资产分类是否存在
        AssetType assetType = assetTypeRepository.getByCode(asset.getTypeCode());
        ThrowUtil.nullThrow(assetType, AssetError.E60101);
        // 校验：只允许选择末级分类
        boolean haveChildren = assetTypeRepository.haveChildren(asset.getTypeCode());
        ThrowUtil.trueThrow(haveChildren, AssetError.E62001);
        // 校验：领用人是否存在
        if (asset.getReceivedId() != null) {
            String userName = userService.getNickName(asset.getReceivedId(), SecurityConstants.INNER);
            ThrowUtil.nullThrow(userName, AssetError.E62002);

            asset.setReceivedBy(userName);
            asset.setState(AssetState.IN_USE);
        } else {
            asset.setState(AssetState.IDLE);
        }

        if (isNew) {
            // 赋值：默认的资产编码
            if (StrUtil.isEmpty(asset.getBarCode())) {
                SerialNoNextCmd nextCmd =
                        SerialNoNextCmd.builder().type(AssetSnConst.getAssetSnType(asset.getTypeCode())).prefix(asset.getTypeCode() + DateUtil.yearSerialStrNow()).build();
                SerialNoR serialNoR = systemService.getNextSn(nextCmd);
                if (serialNoR == null || serialNoR.getSerialNo() == null) {
                    throw new ServiceException(AssetError.E62005);
                }
                asset.setBarCode(serialNoR.getSerialNo());
            }
            // 校验：资产编码是否重复
            Asset byBarCode = assetRepository.getByBarCode(asset.getBarCode());
            ThrowUtil.presentThrow(byBarCode, AssetError.E62003);
        } else {
            // 校验：资产是否存在
            Asset byBarCode = assetRepository.getByBarCode(asset.getBarCode());
            ThrowUtil.nullThrow(byBarCode, AssetError.E62004);
        }

        asset.setTypeId(assetType.getId());
        asset.setTypeCode(assetType.getTypeCode());
        asset.setTypeName(assetType.getTypeName());
        asset.setTypeHierarchy(assetType.getNameHierarchy());
        assetRepository.save(asset, isNew);
    }

    @Transactional
    @Override
    public void requisitionEvent(AssetRequisitionE eventData) {
        List<Asset> assets = assetEventAssembler.toAssets(eventData);
        for (Asset asset : assets) {
            asset.setState(AssetState.IN_USE);
        }
        assetRepository.requisition(assets);
    }

    @Transactional
    @Override
    public void revertEvent(AssetRevertE eventData) {
        for (Asset asset : eventData.getAssets()) {
            asset.setState(AssetState.IDLE);
        }
        assetRepository.revert(eventData.getAssets());
    }

    @Transactional
    @Override
    public void typeEditEvent(AssetTypeEditE eventData) {
        assetRepository.updateType(assetEventAssembler.toAssets(eventData));
    }

    @Transactional
    @Override
    public void typeAddEvent(AssetTypeAddE eventData) {
        // 将父分类的资产移到子分类
        if (StrUtil.isNotEmpty(eventData.getParentTypeCode()) &&
                assetTypeRepository.countChildren(eventData.getParentTypeCode()) == 1) {
            assetRepository.moveToSubType(assetEventAssembler.toAssets(eventData), eventData.getParentTypeCode());
        }
    }

    @Override
    public List<AssetExportResult> exportResults(AssetFuzzyQuery query) {
        List<AssetExportResult> exportResults = new ArrayList<>();
        List<Asset> assets = assetRepository.fuzzyQuery(query);
        for (Asset asset : assets) {
            AssetExportResult assetExportResult = assetEventAssembler.toAssetExportResult(asset);
            if (asset.getReceivedId() != null) {
                asset.setReceivedByUser(userService.getById(asset.getReceivedId()));
            }
            if (asset.getReceivedByUser() != null) {
                if (asset.getReceivedByUser().getDept() != null) {
                    assetExportResult.setDeptName(asset.getReceivedByUser().getDept().getDeptName());
                }
                assetExportResult.setEmployeeNo(asset.getReceivedByUser().getEmployeeNo());
            }
            exportResults.add(assetExportResult);
        }
        return exportResults;
    }

    @Transactional
    @Override
    public void importAssets(List<AssetImportCmd> importCmds) {
        Map<String, AssetType> assetTypeMap = new HashMap<>();
        for (AssetImportCmd importCmd : importCmds) {
            if (!assetTypeMap.containsKey(importCmd.getTypeName())) {
                AssetType assetType = assetTypeRepository.getByTypeName(importCmd.getTypeName());
                if (assetType == null) {
                    String errorMsg = StrUtil.format("不存在名称为'{}'的资产类型，请先添加资产类型后再尝试导入", importCmd.getTypeName());
                    log.error(errorMsg);
                    throw new ServiceException(errorMsg);
                }
                assetTypeMap.put(importCmd.getTypeName(), assetType);
            }
        }

        List<Asset> assets = new ArrayList<>();

        for (AssetImportCmd importCmd : importCmds) {
            Asset asset = Asset.builder().barCode(importCmd.getBarCode())
                    .name(importCmd.getName())
                    .typeCode(assetTypeMap.get(importCmd.getTypeName()).getTypeCode())
                    .specification(importCmd.getSpecification())
                    .purchasedDate(LocalDate.now())
                    .unit("台")
                    .location(importCmd.getLocation())
                    .fileNumber(importCmd.getFileNumber())
                    .remark(importCmd.getNote()).build();
            if (StrUtil.isNotBlank(importCmd.getEmployeeNo())) {
                SysUser user = userService.getUser(SysUser.builder().employeeNo(importCmd.getEmployeeNo()).build());
                if (user == null) {
                    String errorMsg = StrUtil.format("不存在工号为'{}'的用户，请修改后再尝试导入", importCmd.getEmployeeNo());
                    log.error(errorMsg);
                    throw new ServiceException(errorMsg);
                }
                asset.setReceivedId(user.getUserId());
            }
            assets.add(asset);
        }

        for (Asset asset : assets) {
            this.save(asset, true);
        }
    }

}
