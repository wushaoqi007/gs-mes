package com.greenstone.mes.asset.application.service.impl;

import com.greenstone.mes.asset.application.assembler.AssetAssembler;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetSpecDeleteCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetSpecInsertCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetSpecUpdateCmd;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetTypeAddE;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetSpecQuery;
import com.greenstone.mes.asset.application.dto.result.AssetSpecListR;
import com.greenstone.mes.asset.application.service.AssetSpecService;
import com.greenstone.mes.asset.domain.repository.AssetSpecRepository;
import com.greenstone.mes.asset.domain.repository.AssetTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/3 15:25
 */
@Slf4j
@Service
public class AssetSpecServiceImpl implements AssetSpecService {

    private final AssetSpecRepository assetSpecRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final AssetAssembler assetAssembler;

    public AssetSpecServiceImpl(AssetSpecRepository assetSpecRepository, AssetTypeRepository assetTypeRepository, AssetAssembler assetAssembler) {
        this.assetSpecRepository = assetSpecRepository;
        this.assetTypeRepository = assetTypeRepository;
        this.assetAssembler = assetAssembler;
    }

    @Override
    public List<AssetSpecListR> list(AssetSpecQuery query) {
        return assetAssembler.toAssetSpecListRespList(assetSpecRepository.listByTypeCode(query.getTypeCode()));
    }

    @Transactional
    @Override
    public void insert(AssetSpecInsertCmd insertCmd) {
        assetSpecRepository.save(assetAssembler.toAssetSpec(insertCmd));
    }

    @Transactional
    @Override
    public void update(AssetSpecUpdateCmd updateCmd) {
        assetSpecRepository.save(assetAssembler.toAssetSpec(updateCmd));
    }

    @Transactional
    @Override
    public void remove(AssetSpecDeleteCmd deleteCmd) {
        assetSpecRepository.remove(deleteCmd.getId());
    }

    @Transactional
    @Override
    public void typeAddEvent(AssetTypeAddE addE) {
        // 若在末级下新增分类，则将原分类的型号规格移动到新分类下
        Long children = assetTypeRepository.countChildren(addE.getParentTypeCode());
        if (children == 1L) {
            assetSpecRepository.moveTo(addE.getParentTypeCode(), addE.getTypeCode());
        }
    }

}
