package com.greenstone.mes.machine.application.assemble;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialReturnAddCmd;
import com.greenstone.mes.machine.application.dto.result.MachineMaterialReturnResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.domain.entity.MachineMaterialReturn;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface MachineMaterialReturnAssemble {

    MachineMaterialReturn toMachineMaterialReturn(MachineMaterialReturnAddCmd addCmd);

    MachineMaterialReturnResult toMachineMaterialReturnR(MachineMaterialReturn materialReturn);

    List<MachineMaterialReturnResult> toMachineMaterialReturnRs(List<MachineMaterialReturn> list);

    @Mapping(target = "orderDetailId", source = "id")
    @Mapping(target = "orderSerialNo", source = "serialNo")
    MachineOrderPartR toMachineOrderPartR(MachineOrderDetail orderDetail);
}
