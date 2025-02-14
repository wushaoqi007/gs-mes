package com.greenstone.mes.machine.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRealStockQuery;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStock;
import com.greenstone.mes.machine.interfaces.resp.MachineStageStockResp;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物料库存Mapper接口
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Repository
public interface MachineStockMapper extends EasyBaseMapper<MachineStock> {

    List<MachinePartStockR> listStock(MachinePartStockQuery query);

    List<MachinePartStockR> listRealStock(MachineRealStockQuery query);

    List<MachineStageStockResp> listStageStock(@Param("materialId") Long materialId, @Param("stages") WarehouseStage[] stages,
                                               @Param("projectCode") String projectCode);

}
