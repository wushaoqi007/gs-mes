package com.greenstone.mes.oa.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.oa.domain.converter.AttendanceConverter;
import com.greenstone.mes.oa.domain.entity.AttendanceResult;
import com.greenstone.mes.oa.domain.entity.AttendanceResultDetail;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.infrastructure.mapper.AttendanceResultMapper;
import com.greenstone.mes.oa.infrastructure.persistence.AttendanceResultDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2022/11/28 13:39
 */
@Slf4j
@Service
public class AttendanceResultRepository {

    private final AttendanceResultMapper attendanceResultMapper;

    private final AttendanceConverter attendanceConverter;

    public AttendanceResultRepository(AttendanceResultMapper attendanceResultMapper, AttendanceConverter attendanceConverter) {
        this.attendanceResultMapper = attendanceResultMapper;
        this.attendanceConverter = attendanceConverter;
    }

    public void addBatch(List<AttendanceResultDetail> resultDetails) {
        List<AttendanceResult> resultList = attendanceConverter.toResults(resultDetails);
        List<AttendanceResultDO> resultDOs = attendanceConverter.toDOs(resultList);
        List<List<AttendanceResultDO>> lists = CollUtil.split(resultDOs, 500);
        lists.forEach(attendanceResultMapper::insertBatchSomeColumn);
    }

    public void save(AttendanceResultDetail resultDetail) {
        AttendanceResult result = attendanceConverter.toResult(resultDetail);
        AttendanceResultDO resultDO = attendanceConverter.convert(result);
        AttendanceResultDO uniqueCnd = AttendanceResultDO.builder().cpId(resultDetail.getCpId().id()).userId(result.getUserId().id()).day(result.getDay()).build();
        AttendanceResultDO existRecord = attendanceResultMapper.getOneOnly(uniqueCnd);
        if (Objects.isNull(existRecord)) {
            attendanceResultMapper.insert(resultDO);
        } else {
            resultDO.setId(existRecord.getId());
            attendanceResultMapper.updateById(resultDO);
        }
    }

    public void remove(Date start, Date end) {
        start = DateUtil.beginOfDay(start);
        LambdaQueryWrapper<AttendanceResultDO> queryWrapper = Wrappers.lambdaQuery(AttendanceResultDO.class)
                .ge(AttendanceResultDO::getDay, start)
                .le(AttendanceResultDO::getDay, end);
        attendanceResultMapper.delete(queryWrapper);
    }

    public void remove(Date start, Date end, CpId cpId, String userId) {
        start = DateUtil.beginOfDay(start);
        LambdaQueryWrapper<AttendanceResultDO> queryWrapper = Wrappers.lambdaQuery(AttendanceResultDO.class)
                .ge(AttendanceResultDO::getDay, start)
                .le(AttendanceResultDO::getDay, end)
                .eq(AttendanceResultDO::getCpId, cpId.id());
        if(StrUtil.isNotEmpty(userId)){
            queryWrapper.eq(AttendanceResultDO::getUserId,userId);
        }
        attendanceResultMapper.delete(queryWrapper);
    }

    public void remove(Date start, Date end, CpId cpId, List<WxUserId> userIds) {
        start = DateUtil.beginOfDay(start);
        LambdaQueryWrapper<AttendanceResultDO> queryWrapper = Wrappers.lambdaQuery(AttendanceResultDO.class)
                .ge(AttendanceResultDO::getDay, start)
                .le(AttendanceResultDO::getDay, end)
                .eq(AttendanceResultDO::getCpId, cpId.id())
                .in(AttendanceResultDO::getUserId, userIds.stream().map(WxUserId::id).toList());
        attendanceResultMapper.delete(queryWrapper);
    }

    public List<AttendanceResult> listResult(Date start, Date end, CpId cpId) {
        start = DateUtil.beginOfDay(start);
        LambdaQueryWrapper<AttendanceResultDO> query = Wrappers.lambdaQuery(AttendanceResultDO.class).ge(AttendanceResultDO::getDay, start)
                .le(AttendanceResultDO::getDay, end).eq(AttendanceResultDO::getCpId, cpId.id());
        List<AttendanceResultDO> resultDOList = attendanceResultMapper.selectList(query);
        return attendanceConverter.convertList(resultDOList);
    }

    public List<AttendanceResult> listResult(Date start, Date end, CpId cpId, WxUserId userId) {
        start = DateUtil.beginOfDay(start);
        LambdaQueryWrapper<AttendanceResultDO> query = Wrappers.lambdaQuery(AttendanceResultDO.class).
                ge(AttendanceResultDO::getDay, start)
                .le(AttendanceResultDO::getDay, end).
                eq(AttendanceResultDO::getCpId, cpId.id())
                .eq(AttendanceResultDO::getUserId, userId.id());
        List<AttendanceResultDO> resultDOList = attendanceResultMapper.selectList(query);
        return attendanceConverter.convertList(resultDOList);
    }


}
