package com.greenstone.mes.material.domain.service.impl;

import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.entity.MaterialAdditional;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.domain.service.MaterialAdditionalService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialAdditionalMapper;
import com.greenstone.mes.material.infrastructure.persistence.MaterialAdditionalPo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MaterialAdditionalServiceImpl implements MaterialAdditionalService {

    private final MaterialAdditionalMapper materialAdditionalMapper;
    private final IBaseMaterialService materialService;

    @Transactional
    @Override
    public void commit(MaterialAdditional additional) {
        BaseMaterial material = materialService.getOneOnly(BaseMaterial.builder().code(additional.getCode()).version(additional.getVersion()).build());
        if (material != null) {
            throw new RuntimeException("编号【" + material.getCode() + "】版本【" + material.getVersion() + "】的零件已存在，请勿重复补录");
        }
        BaseMaterial materialInsert = entity2MaterialEntity(additional);
        materialService.insertBaseMaterial(materialInsert, false);
        MaterialAdditionalPo additionalPo = entity2po(additional);
        materialAdditionalMapper.insert(additionalPo);
    }

    private MaterialAdditionalPo entity2po(MaterialAdditional additional) {
        return MaterialAdditionalPo.builder().rawMaterial(additional.getRawMaterial())
                .code(additional.getCode())
                .version(additional.getVersion())
                .unit("pcs")
                .weight(additional.getWeight())
                .name(additional.getName())
                .surfaceTreatment(additional.getSurfaceTreatment())
                .designer(additional.getDesigner()).build();
    }

    private BaseMaterial entity2MaterialEntity(MaterialAdditional additional) {
        return BaseMaterial.builder().rawMaterial(additional.getRawMaterial())
                .code(additional.getCode())
                .version(additional.getVersion())
                .unit("pcs")
                .weight(additional.getWeight())
                .name(additional.getName())
                .surfaceTreatment(additional.getSurfaceTreatment())
                .designer(additional.getDesigner())
                .type(1)
                .build();
    }

}
