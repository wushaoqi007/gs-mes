package com.greenstone.mes.oa.domain.converter;

import com.greenstone.mes.oa.domain.entity.Dorm;
import com.greenstone.mes.oa.domain.entity.DormMember;
import com.greenstone.mes.oa.dto.cmd.DormMemberOperationCmd;
import com.greenstone.mes.oa.infrastructure.persistence.DormDo;
import com.greenstone.mes.oa.infrastructure.persistence.DormMemberDo;
import com.greenstone.mes.oa.infrastructure.persistence.DormRecordDo;
import com.greenstone.mes.oa.infrastructure.persistence.DormTimeSectionDo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/23 11:08
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DormConverter {

    DormDo toDormDO(Dorm dorm);

    DormRecordDo toDormRecordDO(DormMemberOperationCmd dormRecord);

    @Mapping(target = "inTime", source = "time")
    DormMemberDo toDormMemberDO(DormMemberOperationCmd cmd);

    DormTimeSectionDo toDormTimeSectionDO(DormMemberOperationCmd cmd);

    Dorm toDorm(DormDo dormDo);

    List<Dorm> toDorms(List<DormDo> dormDos);

    DormMember toDormMember(DormMemberDo dormMemberDo);

    List<DormMember> toDormMembers(List<DormMemberDo> dormMemberDos);

    default Dorm toDorm(DormDo dormDo, List<DormMemberDo> memberDos) {
        Dorm dorm = toDorm(dormDo);
        dorm.setMembers(toDormMembers(memberDos));
        return dorm;
    }
}
