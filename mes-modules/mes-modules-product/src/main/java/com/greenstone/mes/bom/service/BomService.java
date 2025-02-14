package com.greenstone.mes.bom.service;

import com.greenstone.mes.bom.domain.Bom;
import com.greenstone.mes.bom.dto.MaterialNumberDto;
import com.greenstone.mes.common.mybatisplus.IServiceWrapper;

import java.util.List;

/**
 * BOMService接口
 *
 * @author gu_renkai
 * @date 2022-01-25
 */
public interface BomService extends IServiceWrapper<Bom> {

    Bom getWithSaveIfNotExist(Bom bom);

    List<MaterialNumberDto> listNumberFromLpWh(Long materialId);

}