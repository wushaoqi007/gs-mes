package com.greenstone.mes.bom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.bom.domain.Bom;
import com.greenstone.mes.bom.dto.MaterialNumberDto;
import com.greenstone.mes.bom.mapper.BomMapper;
import com.greenstone.mes.bom.service.BomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BOMService业务层处理
 *
 * @author gu_renkai
 * @date 2022-01-25
 */
@Slf4j
@Service
public class BomServiceImpl extends ServiceImpl<BomMapper, Bom> implements BomService {

    @Autowired
    private BomMapper bomMapper;

    @Override
    public Bom getWithSaveIfNotExist(Bom bom) {
        Bom bomSelectEntity = Bom.builder().code(bom.getCode()).version(bom.getVersion()).build();
        Bom existBom = this.getOneOnly(bomSelectEntity);
        if (existBom == null) {
            this.save(bom);
            log.info("Save bom: {}", bom);
            return bom;
        } else {
            log.info("Find exist bom: {}", bom);
            return existBom;
        }
    }

    @Override
    public List<MaterialNumberDto> listNumberFromLpWh(Long materialId) {
        return bomMapper.listNumberFromLpWh(materialId);
    }

}