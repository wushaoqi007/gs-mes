package com.greenstone.mes.bom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.bom.domain.Bom;
import com.greenstone.mes.bom.dto.MaterialNumberDto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BOMMapper接口
 *
 * @author gu_renkai
 * @date 2022-01-25
 */
@Repository
public interface BomMapper extends BaseMapper<Bom> {

    List<MaterialNumberDto> listNumberFromLpWh(Long materialId);
}