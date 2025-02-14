package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.lang.UUID;
import com.greenstone.mes.oa.application.assembler.DormAssembler;
import com.greenstone.mes.oa.application.service.DormService;
import com.greenstone.mes.oa.domain.entity.Dorm;
import com.greenstone.mes.oa.domain.repository.DormRepository;
import com.greenstone.mes.oa.dto.cmd.DormMemberOperationCmd;
import com.greenstone.mes.oa.dto.cmd.DormSaveCmd;
import com.greenstone.mes.oa.dto.cmd.DormUpdateCmd;
import com.greenstone.mes.oa.dto.query.DormListQuery;
import com.greenstone.mes.oa.dto.query.DormRecordQuery;
import com.greenstone.mes.oa.dto.result.*;
import com.greenstone.mes.oa.enums.DormCityType;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DormServiceImpl implements DormService {

    private final DormRepository dormRepository;
    private final DormAssembler assembler;
    private final RemoteUserService userService;

    @Override
    public DormResult detail(String dormNo) {
        Dorm detail = dormRepository.detail(dormNo);
        return assembler.toDormResult(detail);
    }

    @Override
    public List<DormTreeResult> tree() {
        List<Dorm> list = dormRepository.list(new DormListQuery());
        List<DormTreeResult> dormResults = assembler.toDormTreeResults(list);
        List<DormTreeResult> result = new ArrayList<>();
        Map<String, List<DormTreeResult>> cityMap = dormResults.stream().collect(Collectors.groupingBy(DormTreeResult::getCity));
        cityMap.forEach((city, dorms) -> {
            DormTreeResult node = DormTreeResult.builder().id(UUID.fastUUID().toString())
                    .label(city)
                    .children(dorms).build();
            result.add(node);

            for (DormTreeResult dorm : dorms) {
                dorm.setId(UUID.fastUUID().toString());
                dorm.setParentId(node.getId());
                dorm.setLabel(dorm.getAddress() + "-" + dorm.getRoomNo());
            }
        });
        return result;
    }

    @Override
    public List<DormResult> cities() {
        List<Dorm> cities = dormRepository.cities();
        return assembler.toDormResults(cities);
    }

    @Override
    public List<DormResult> list(DormListQuery query) {
        List<Dorm> dorms = dormRepository.list(query);
        return assembler.toDormResults(dorms);
    }

    @Override
    public List<DormResult> detailList(DormListQuery query) {
        List<Dorm> dorms = dormRepository.detailList(query);
        List<DormResult> dormResults = assembler.toDormResults(dorms);
        for (DormResult dormResult : dormResults) {
            long count = dormResult.getMembers().stream().filter(m -> m.getId() == null).count();
            dormResult.setFreeBedNumber((int) count);
        }
        return dormResults;
    }

    @Override
    public DormMemberResult getDormMember(Long employeeId) {
        return dormRepository.getDormMember(employeeId);
    }

    @Transactional
    @Override
    public DormSaveResult add(DormSaveCmd saveCmd) {
        SysUser userinfo = null;
        if (saveCmd.getManageBy() != null) {
            userinfo = userService.userinfo(saveCmd.getManageBy());
            if (userinfo == null) {
                throw new RuntimeException("选择的负责人不存在");
            }
        }
        Dorm dorm = assembler.toDorm(saveCmd);
        if (userinfo != null) {
            dorm.setManageByName(userinfo.getNickName());
        }
        String dormNo = dormRepository.add(dorm);
        return DormSaveResult.builder().dormNo(dormNo).build();
    }

    @Transactional
    @Override
    public DormSaveResult update(DormUpdateCmd updateCmd) {
        SysUser userinfo = null;
        if (updateCmd.getManageBy() != null) {
            userinfo = userService.userinfo(updateCmd.getManageBy());
            if (userinfo == null) {
                throw new RuntimeException("选择的负责人不存在");
            }
        }
        Dorm dorm = assembler.toDorm(updateCmd);
        if (userinfo != null) {
            dorm.setManageByName(userinfo.getNickName());
        }
        String dormNo = dormRepository.update(dorm);
        return DormSaveResult.builder().dormNo(dormNo).build();
    }

    @Transactional
    @Override
    public void dormOperation(DormMemberOperationCmd operationCmd) {
        dormRepository.dormOperation(operationCmd);
    }

    @Transactional
    @Override
    public void remove(String dormNo) {
        dormRepository.delete(dormNo);
    }

    @Override
    public List<DormExportResult> exportDorm(DormCityType cityType) {
        List<Dorm> dorms = dormRepository.detailList(DormListQuery.builder().cityType(cityType).build());
        return assembler.toDormExportResults(dorms);
    }

    @Override
    public List<DormRecordResult> records(DormRecordQuery query) {
        return dormRepository.records(query);
    }
}
