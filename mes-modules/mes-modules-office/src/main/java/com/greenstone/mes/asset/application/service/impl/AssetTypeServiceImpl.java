package com.greenstone.mes.asset.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.asset.application.assembler.AssetAssembler;
import com.greenstone.mes.asset.application.assembler.AssetEventAssembler;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetTypeSaveCmd;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeAddE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeEditE;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetTypeQuery;
import com.greenstone.mes.asset.application.dto.result.AssetTypeListR;
import com.greenstone.mes.asset.application.dto.result.AssetTypeR;
import com.greenstone.mes.asset.application.event.AssetTypeAddEvent;
import com.greenstone.mes.asset.application.event.AssetTypeEditEvent;
import com.greenstone.mes.asset.application.service.AssetTypeService;
import com.greenstone.mes.asset.domain.entity.AssetType;
import com.greenstone.mes.asset.domain.repository.AssetTypeRepository;
import com.greenstone.mes.asset.domain.repository.effect.AssetTypeSaveEffect;
import com.greenstone.mes.asset.infrastructure.util.ThrowUtil;
import com.greenstone.mes.common.core.enums.AssetError;
import com.greenstone.mes.common.core.exception.ServiceException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 15:37
 */
@Service
public class AssetTypeServiceImpl implements AssetTypeService {

    private final AssetTypeRepository assetTypeRepository;
    private final AssetAssembler assetAssembler;
    private final AssetEventAssembler assetEventAssembler;
    private final ApplicationEventPublisher eventPublisher;

    public AssetTypeServiceImpl(AssetTypeRepository assetTypeRepository, AssetAssembler assetAssembler, AssetEventAssembler assetEventAssembler,
                                ApplicationEventPublisher eventPublisher) {
        this.assetTypeRepository = assetTypeRepository;
        this.assetAssembler = assetAssembler;
        this.assetEventAssembler = assetEventAssembler;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<AssetTypeListR> list() {
        List<AssetType> assetTypeList = assetTypeRepository.listAll();
        return assetAssembler.toAssetCateListRespList(assetTypeList);
    }

    @Override
    public AssetTypeR find(AssetTypeQuery query) {
        AssetType assetType = assetTypeRepository.getById(query.getAssetTypeId());
        return assetAssembler.toAssetCateResp(assetType);
    }

    @Transactional
    @Override
    public void save(AssetTypeSaveCmd saveCmd) {
        AssetType assetType = assetAssembler.toAssetCate(saveCmd);

        boolean isNewOne = assetType.getId() == null;
        // 校验：原资产类型是否存在
        AssetType assetTypeExist = assetTypeRepository.getById(assetType.getId());
        if (!isNewOne && assetTypeExist == null) {
            throw new ServiceException(AssetError.E60101);
        }
        // 校验：不允许新增已使用的分类编码
        AssetType assetTypeWithCodeUsed = assetTypeRepository.getByCode(assetType.getTypeCode());
        boolean codeHasBeenUsed = assetTypeWithCodeUsed != null;
        ThrowUtil.trueThrow(isNewOne && codeHasBeenUsed, AssetError.E60105);
        // 校验：不允许更新为已使用的分类编码
        boolean codeUsedByAnOther = codeHasBeenUsed && !assetType.getId().equals(assetTypeWithCodeUsed.getId());
        ThrowUtil.trueThrow(!isNewOne && codeUsedByAnOther, AssetError.E60105);

        AssetType parentType = null;
        boolean haveParent = StrUtil.isNotEmpty(assetType.getParentTypeCode());
        if (haveParent) {
            // 校验：父资产分类不能是自身
            ThrowUtil.trueThrow(assetType.getTypeCode().equals(assetType.getParentTypeCode()), AssetError.E60111);
            // 校验：上级节点是否存在
            parentType = assetTypeRepository.getByCode(assetType.getParentTypeCode());
            if (parentType == null) {
                throw new ServiceException(AssetError.E60104);
            }
        }
        // 校验：更新时，不能将分类移动到末级分类下，如果没有移动结构则不需要校验
        boolean parentTypeChanged = !isNewOne && haveParent && !assetType.getParentTypeCode().equals(assetTypeExist.getParentTypeCode());
        if (parentTypeChanged) {
            if (!assetTypeRepository.haveChildren(parentType.getTypeCode())) {
                throw new ServiceException(AssetError.E60106);
            }
        }

        // 保存资产分类
        if (isNewOne) {
            assetTypeRepository.save(assetType, true);
        }
        // 更新资产分类，保存后需要更新层级结构，在这里一起更新了
        setTypeHierarchy(assetType, parentType);
        assetTypeRepository.save(assetType, false);

        // 更新子分类的信息
        List<AssetType> children = assetTypeRepository.listChildren(assetType.getTypeCode());
        if (CollUtil.isNotEmpty(children)) {
            for (AssetType child : children) {
                child.setParentTypeCode(assetType.getTypeCode());
                setTypeHierarchy(child, assetType);
            }
            assetTypeRepository.updateChildren(children);
        }

        // 通知：新增
        if (isNewOne) {
            AssetTypeAddE eventData = assetEventAssembler.toAssetTypeAddEventData(assetType);
            eventPublisher.publishEvent(new AssetTypeAddEvent(eventData));
        } else {
            // 通知：修改，有修改时才发出通知
            AssetTypeSaveEffect effect = new AssetTypeSaveEffect();
            effect.setTypeCodeChanged(assetType.getTypeCode().equals(assetTypeExist.getTypeCode()));
            effect.setTypeNameChanged(assetType.getTypeName().equals(assetTypeExist.getTypeName()));
            effect.setParentTypeChanged(parentTypeChanged);
            if (effect.isSomethingChanged()) {
                AssetTypeEditE eventData = assetEventAssembler.toAssetTypeEditEventData(assetType);
                eventPublisher.publishEvent(new AssetTypeEditEvent(eventData));
            }
        }

    }

    @Transactional
    @Override
    public void remove(String typeCode) {
        assetTypeRepository.remove(typeCode);
    }

    private void setTypeHierarchy(AssetType self, AssetType parent) {
        String idHierarchy = (parent == null) ? String.valueOf(self.getId()) : parent.getIdHierarchy() + "|" + self.getId();
        self.setIdHierarchy(idHierarchy);
        String nameHierarchy = (parent == null) ? self.getTypeName() : self.getTypeName() + " / " + parent.getNameHierarchy();
        self.setNameHierarchy(nameHierarchy);
    }
}
