package com.greenstone.mes.system.application.assembler;

import com.greenstone.mes.system.dto.result.UserResult;
import com.greenstone.mes.system.infrastructure.po.UserPo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:10
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserAssembler {

    UserResult do2Result(UserPo userDo);

    List<UserResult> dos2Results(List<UserPo> userDos);

}
