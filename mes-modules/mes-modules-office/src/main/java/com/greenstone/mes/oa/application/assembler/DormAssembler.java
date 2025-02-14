package com.greenstone.mes.oa.application.assembler;

/**
 * @author gu_renkai
 * @date 2022/11/24 9:23
 */

import com.greenstone.mes.oa.domain.entity.Dorm;
import com.greenstone.mes.oa.domain.entity.DormMember;
import com.greenstone.mes.oa.dto.cmd.DormSaveCmd;
import com.greenstone.mes.oa.dto.cmd.DormUpdateCmd;
import com.greenstone.mes.oa.dto.result.DormExportResult;
import com.greenstone.mes.oa.dto.result.DormResult;
import com.greenstone.mes.oa.dto.result.DormTreeResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DormAssembler {

    Dorm toDorm(DormSaveCmd saveCmd);

    Dorm toDorm(DormUpdateCmd updateCmd);

    DormResult toDormResult(Dorm dorm);

    List<DormResult> toDormResults(List<Dorm> dorms);

    DormResult.DormMember toDormMember(DormMember dormMember);

    List<DormResult.DormMember> toDormMembers(List<DormMember> dormMembers);

    DormTreeResult toDormTreeResult(Dorm dorm);

    List<DormTreeResult> toDormTreeResults(List<Dorm> dorms);

    default DormExportResult toDormExportResult(Dorm dorm, DormMember member) {
        return DormExportResult.builder().dormNo(dorm.getDormNo()).roomNo(dorm.getRoomNo())
                .address(dorm.getAddress()).bedNo(member.getBedNo()).employeeName(member.getEmployeeName())
                .employeeNo("").deptName("").duty("").province("").contact(member.getTelephone()).urgentTel(member.getUrgentTel()).dormType(dorm.getBedNumber() + "人间")
                .inTime(member.getInTime()).manager(dorm.getManageByName()).build();
    }

    default List<DormExportResult> toDormExportResults(List<Dorm> dorms) {
        List<DormExportResult> results = new ArrayList<>();
        for (Dorm dorm : dorms) {
            for (DormMember member : dorm.getMembers()) {
                results.add(toDormExportResult(dorm, member));
            }
        }
        return results;
    }

}
