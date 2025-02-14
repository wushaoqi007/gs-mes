package com.greenstone.mes.oa.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.oa.domain.converter.DormConverter;
import com.greenstone.mes.oa.domain.entity.Dorm;
import com.greenstone.mes.oa.domain.entity.DormMember;
import com.greenstone.mes.oa.dto.cmd.DormMemberOperationCmd;
import com.greenstone.mes.oa.dto.query.DormListQuery;
import com.greenstone.mes.oa.dto.query.DormRecordQuery;
import com.greenstone.mes.oa.dto.result.DormMemberResult;
import com.greenstone.mes.oa.dto.result.DormRecordResult;
import com.greenstone.mes.oa.enums.DormMemberStatus;
import com.greenstone.mes.oa.infrastructure.mapper.DormMapper;
import com.greenstone.mes.oa.infrastructure.mapper.DormMemberMapper;
import com.greenstone.mes.oa.infrastructure.mapper.DormRecordMapper;
import com.greenstone.mes.oa.infrastructure.mapper.DormTimeSectionMapper;
import com.greenstone.mes.oa.infrastructure.persistence.DormDo;
import com.greenstone.mes.oa.infrastructure.persistence.DormMemberDo;
import com.greenstone.mes.oa.infrastructure.persistence.DormRecordDo;
import com.greenstone.mes.oa.infrastructure.persistence.DormTimeSectionDo;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DormRepository {

    private final DormMapper dormMapper;
    private final DormMemberMapper dormMemberMapper;
    private final DormRecordMapper dormRecordMapper;
    private final DormTimeSectionMapper dormTimeSectionMapper;
    private final DormConverter converter;
    private final RemoteUserService userService;


    public Dorm detail(String dormNo) {
        DormDo dormDo = dormMapper.selectById(dormNo);
        if (dormDo == null) {
            throw new RuntimeException("选择的宿舍不存在");
        }
        Dorm dorm = converter.toDorm(dormDo);
        List<DormMember> members = new ArrayList<>();
        dorm.setMembers(members);
        List<DormMemberDo> memberDos = dormMemberMapper.list(DormMemberDo.builder().dormNo(dormNo).build());
        for (int i = 1; i <= dorm.getBedNumber(); i++) {
            int finalI = i;
            List<DormMemberDo> theBedMemberDos = memberDos.stream().filter(m -> m.getBedNo() == finalI).toList();
            if (CollUtil.isEmpty(theBedMemberDos)) {
                members.add(DormMember.builder().bedNo(finalI).build());
            } else {
                for (DormMemberDo theBedMemberDo : theBedMemberDos) {
                    members.add(converter.toDormMember(theBedMemberDo));
                }
            }
        }
        return dorm;
    }

    public List<Dorm> list(DormListQuery query) {
        LambdaQueryWrapper<DormDo> wrapper = Wrappers.lambdaQuery(DormDo.class).eq(StrUtil.isNotEmpty(query.getCity()), DormDo::getCity, query.getCity());
        List<DormDo> dormDos = dormMapper.selectList(wrapper);
        return converter.toDorms(dormDos);
    }

    public List<Dorm> detailList(DormListQuery query) {
        List<DormDo> dormDos = dormMapper.selectDormList(query);
        List<Dorm> dorms = converter.toDorms(dormDos);
        for (Dorm dorm : dorms) {
            List<DormMember> members = new ArrayList<>();
            dorm.setMembers(members);
            List<DormMemberDo> memberDos = dormMemberMapper.list(DormMemberDo.builder().dormNo(dorm.getDormNo()).build());
            for (int i = 1; i <= dorm.getBedNumber(); i++) {
                int finalI = i;
                List<DormMemberDo> theBedMemberDos = memberDos.stream().filter(m -> m.getBedNo() == finalI).toList();
                if (CollUtil.isEmpty(theBedMemberDos)) {
                    members.add(DormMember.builder().bedNo(finalI).build());
                } else {
                    for (DormMemberDo theBedMemberDo : theBedMemberDos) {
                        members.add(converter.toDormMember(theBedMemberDo));
                    }
                }
            }
        }
        return dorms;
    }

    public String add(Dorm dorm) {
        DormDo dormDo = dormMapper.selectById(dorm.getDormNo());
        if (dormDo == null) {
            dormDo = converter.toDormDO(dorm);
            dormMapper.insert(dormDo);
        } else {
            throw new RuntimeException("宿舍编号已存在");
        }
        return dorm.getDormNo();
    }

    public String update(Dorm updateDorm) {
        Dorm dorm = detail(updateDorm.getDormNo());
        if (dorm == null) {
            throw new RuntimeException("选择的宿舍不存在");
        } else {
            Integer maxBedNo = dorm.getMembers().stream().map(DormMember::getBedNo).max(Comparator.naturalOrder()).orElse(null);
            if (maxBedNo != null && updateDorm.getBedNumber() < maxBedNo) {
                throw new RuntimeException(maxBedNo + "号床已有人入住");
            }
            DormDo dormDo = converter.toDormDO(updateDorm);
            dormMapper.updateById(dormDo);
        }
        return dorm.getDormNo();
    }

    public void delete(String dormNo) {
        log.info("Delete Dorm: start delete.");
        DormDo dormDo = dormMapper.selectById(dormNo);
        if (dormDo == null) {
            throw new RuntimeException("选择的宿舍不存在。");
        }
        dormMapper.deleteById(dormDo);
        dormMemberMapper.delete(DormMemberDo.builder().dormNo(dormNo).build());
        log.info("Delete Dorm: deleted.");
    }

    public void dormOperation(DormMemberOperationCmd operationCmd) {
        log.info("DormMemberOperation: {}", operationCmd);
        SysUser userinfo = userService.userinfo(operationCmd.getEmployeeId());
        // 保存住宿记录
        DormRecordDo recordDo = converter.toDormRecordDO(operationCmd);
        recordDo.setEmployeeName(userinfo.getNickName());
        dormRecordMapper.insert(recordDo);

        switch (operationCmd.getOperation()) {
            case CHECK_IN -> checkInOperation(operationCmd, userinfo);
            case CHECK_OUT -> checkOutOperation(operationCmd);
            case LEAVE -> leaveOperation(operationCmd);
            case BACK -> backOperation(operationCmd);
        }
        log.info("DormMemberOperation: end.");
    }

    public DormMemberResult getDormMember(Long employeeId) {
        return dormMemberMapper.selectDormMember(employeeId);
    }

    public List<Dorm> cities() {
        QueryWrapper<DormDo> wrapper = new QueryWrapper<>();
        wrapper.select("distinct city");
        List<DormDo> dormDos = dormMapper.selectList(wrapper);
        return converter.toDorms(dormDos);
    }

    public List<DormRecordResult> records(DormRecordQuery query) {
        return dormRecordMapper.selectDormRecords(query);
    }

    private void checkInOperation(DormMemberOperationCmd operationCmd, SysUser userinfo) {
        if(StrUtil.isEmpty(operationCmd.getTelephone())){
            throw new RuntimeException("请输入本人电话");
        }
        if(StrUtil.isEmpty(operationCmd.getUrgentTel())){
            throw new RuntimeException("请输入紧急联系人电话");
        }
        DormDo dormDo = dormMapper.getOneOnly(DormDo.builder().dormNo(operationCmd.getDormNo()).build());
        if (dormDo == null) {
            throw new RuntimeException("选择的宿舍不存在。");
        }
        if (operationCmd.getBedNo() > dormDo.getBedNumber()) {
            throw new RuntimeException("选择的床位不存在。");
        }
        DormMemberDo memberDo = dormMemberMapper.getOneOnly(DormMemberDo.builder().employeeId(operationCmd.getEmployeeId()).build());
        if (memberDo != null) {
            throw new RuntimeException("当前已入住宿舍，请'退房'后再'入住'。");
        }
        memberDo = dormMemberMapper.getOneOnly(DormMemberDo.builder().dormNo(operationCmd.getDormNo()).bedNo(operationCmd.getBedNo()).build());
        if (memberDo != null && memberDo.getStatus() != DormMemberStatus.LEAVE) {
            throw new RuntimeException(operationCmd.getBedNo() + "号床位已有人在住，无法继续入住。");
        }
        // 添加宿舍成员
        DormMemberDo newMemberDo = converter.toDormMemberDO(operationCmd);
        newMemberDo.setStatus(DormMemberStatus.LIVE_IN);
        newMemberDo.setEmployeeName(userinfo.getNickName());
        dormMemberMapper.insert(newMemberDo);
        // 添加住宿时段
        DormTimeSectionDo timeSectionDo = converter.toDormTimeSectionDO(operationCmd);
        timeSectionDo.setInOperation(operationCmd.getOperation());
        timeSectionDo.setInTime(operationCmd.getTime());
        timeSectionDo.setEmployeeName(userinfo.getNickName());
        dormTimeSectionMapper.insert(timeSectionDo);
    }

    private void checkOutOperation(DormMemberOperationCmd operationCmd) {
        DormMemberDo memberDo = dormMemberMapper.getOneOnly(DormMemberDo.builder().employeeId(operationCmd.getEmployeeId()).build());
        if (memberDo == null) {
            throw new RuntimeException("当前无入住宿舍，无需'退房'。");
        }
        DormDo dormDo = dormMapper.getOneOnly(DormDo.builder().dormNo(memberDo.getDormNo()).build());
        if (operationCmd.getBedNo() > dormDo.getBedNumber()) {
            throw new RuntimeException("选择的床位不存在。");
        }
        // 删除宿舍成员
        DormMemberDo delMemberDo = DormMemberDo.builder().employeeId(operationCmd.getEmployeeId()).build();
        dormMemberMapper.delete(delMemberDo);
        // 更新住宿时段
        LambdaQueryWrapper<DormTimeSectionDo> timeSectionWrapper = Wrappers.lambdaQuery(DormTimeSectionDo.class).eq(DormTimeSectionDo::getEmployeeId, operationCmd.getEmployeeId())
                .isNull(DormTimeSectionDo::getOutTime);
        DormTimeSectionDo sectionDo = dormTimeSectionMapper.selectOne(timeSectionWrapper);
        if (sectionDo == null) {
            log.warn("Update DormTimeSection error: can not find with userId {}", operationCmd.getEmployeeId());
        } else {
            DormTimeSectionDo updateTimeSectionDO = DormTimeSectionDo.builder().id(sectionDo.getId())
                    .outOperation(operationCmd.getOperation())
                    .outTime(operationCmd.getTime()).build();
            dormTimeSectionMapper.updateById(updateTimeSectionDO);
        }
    }

    private void leaveOperation(DormMemberOperationCmd operationCmd) {
        DormMemberDo memberDo = dormMemberMapper.getOneOnly(DormMemberDo.builder().employeeId(operationCmd.getEmployeeId()).build());
        if (memberDo == null) {
            throw new RuntimeException("当前无入住宿舍，无需'暂离'。");
        }
        DormDo dormDo = dormMapper.getOneOnly(DormDo.builder().dormNo(memberDo.getDormNo()).build());
        if (operationCmd.getBedNo() > dormDo.getBedNumber()) {
            throw new RuntimeException("选择的床位不存在。");
        }
        // 更新宿舍成员
        DormMemberDo updateMemberDo = DormMemberDo.builder().id(memberDo.getId()).status(DormMemberStatus.LEAVE).build();
        dormMemberMapper.updateById(updateMemberDo);
        // 更新住宿时段
        LambdaQueryWrapper<DormTimeSectionDo> timeSectionWrapper = Wrappers.lambdaQuery(DormTimeSectionDo.class).eq(DormTimeSectionDo::getEmployeeId, operationCmd.getEmployeeId())
                .isNull(DormTimeSectionDo::getOutTime);
        DormTimeSectionDo sectionDo = dormTimeSectionMapper.selectOne(timeSectionWrapper);
        if (sectionDo == null) {
            log.warn("Update DormTimeSection error: can not find with userId {}", operationCmd.getEmployeeId());
        } else {
            DormTimeSectionDo updateTimeSectionDO = DormTimeSectionDo.builder().id(sectionDo.getId())
                    .outOperation(operationCmd.getOperation())
                    .outTime(operationCmd.getTime()).build();
            dormTimeSectionMapper.updateById(updateTimeSectionDO);
        }
    }

    private void backOperation(DormMemberOperationCmd operationCmd) {
        DormMemberDo memberDo = dormMemberMapper.getOneOnly(DormMemberDo.builder().employeeId(operationCmd.getEmployeeId()).build());
        if (memberDo == null) {
            throw new RuntimeException("当前无入住宿舍，无需'返宿'，如要住宿请选择'入住'。");
        }
        if (memberDo.getStatus() != DormMemberStatus.LEAVE) {
            throw new RuntimeException("当前不是'暂离'状态，无需'返宿'。");
        }
        DormDo dormDo = dormMapper.getOneOnly(DormDo.builder().dormNo(memberDo.getDormNo()).build());
        if (operationCmd.getBedNo() > dormDo.getBedNumber()) {
            throw new RuntimeException("选择的床位不存在。");
        }
        // 更新宿舍成员
        DormMemberDo updateMemberDo = DormMemberDo.builder().id(memberDo.getId()).status(DormMemberStatus.LIVE_IN).build();
        dormMemberMapper.updateById(updateMemberDo);
        // 添加住宿时段
        DormTimeSectionDo timeSectionDo = converter.toDormTimeSectionDO(operationCmd);
        timeSectionDo.setInOperation(operationCmd.getOperation());
        timeSectionDo.setInTime(operationCmd.getTime());
        timeSectionDo.setEmployeeName(memberDo.getEmployeeName());
        dormTimeSectionMapper.insert(timeSectionDo);
    }

}
