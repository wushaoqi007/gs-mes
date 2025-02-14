package com.greenstone.mes.oa.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.oa.domain.entity.AttendanceRemit;
import com.greenstone.mes.oa.infrastructure.mapper.AttendanceRemitMapper;
import com.greenstone.mes.oa.infrastructure.persistence.AttendanceRemitDO;
import com.greenstone.mes.oa.infrastructure.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2022/11/28 13:39
 */
@Slf4j
@Service
public class AttendanceRemitRepository {

    private final AttendanceRemitMapper attendanceRemitMapper;

    public AttendanceRemitRepository(AttendanceRemitMapper attendanceRemitMapper) {
        this.attendanceRemitMapper = attendanceRemitMapper;
    }

    public boolean exist(AttendanceRemit remit) {
        AttendanceRemitDO remitDO = AttendanceRemitDO.builder().cpId(remit.getCpId().id()).userId(remit.getUserId().id())
                .day(remit.getDay()).checkinType(remit.getCheckinType().getType()).build();
        return Objects.nonNull(attendanceRemitMapper.getOneOnly(remitDO));
    }

    public void save(AttendanceRemit remit) {
        AttendanceRemitDO remitDO = AttendanceRemitDO.builder().cpId(remit.getCpId().id()).userId(remit.getUserId().id())
                .day(remit.getDay()).checkinType(remit.getCheckinType().getType()).remitType(remit.getRemitType().getCode()).build();
        AttendanceRemitDO existRemit = attendanceRemitMapper.getOneOnly(remitDO);
        if (Objects.nonNull(existRemit)) {
            log.info("Remit already exist, '{}' '{}' '{}' '{}' '{}'", remitDO.getCpId(), remitDO.getUserId(),
                    remitDO.getDay(), remitDO.getCheckinType(), remitDO.getRemitType());
        } else {
            log.info("Save remit, '{}' '{}' '{}' '{}' '{}'", remitDO.getCpId(), remitDO.getUserId(),
                    remitDO.getDay(), remitDO.getCheckinType(), remitDO.getRemitType());
            attendanceRemitMapper.insert(remitDO);
        }
    }

    private Long timesInHalfYear(AttendanceRemit remit) {
        AttendanceRemitDO queryDO = AttendanceRemitDO.builder().cpId(remit.getCpId().id())
                .userId(remit.getUserId().id())
                .remitType(remit.getRemitType().getCode()).build();
        LambdaQueryWrapper<AttendanceRemitDO> query = Wrappers.lambdaQuery(queryDO)
                .ge(AttendanceRemitDO::getDay, DateUtil.beginOfHalfYear(remit.getDay()))
                .le(AttendanceRemitDO::getDay, DateUtil.endOfHalfYear(remit.getDay()));
        return attendanceRemitMapper.selectCount(query);
    }

}
