package com.greenstone.mes.oa.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.oa.domain.converter.AttendanceConverter;
import com.greenstone.mes.oa.domain.entity.TimeSection;
import com.greenstone.mes.oa.domain.entity.TimeSections;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.oa.infrastructure.mapper.WxCheckinDataMapper;
import com.greenstone.mes.oa.infrastructure.mapper.WxCheckinSyncMapper;
import com.greenstone.mes.oa.infrastructure.persistence.WxCheckinDataDO;
import com.greenstone.mes.oa.infrastructure.persistence.WxCheckinSyncDO;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import com.greenstone.mes.oa.request.SyncCheckinDataCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class WxCheckinDataRepository {

    private final WxCheckinSyncMapper wxCheckinSyncMapper;

    private final WxCheckinDataMapper wxCheckinDataMapper;

    private final AttendanceConverter attendanceConverter;

    public List<WxCpCheckinData> list(CpId cpId, Date startTime, Date endTime, List<String> userList) {
        LambdaQueryWrapper<WxCheckinDataDO> wrapper = Wrappers.lambdaQuery(WxCheckinDataDO.class)
                .eq(WxCheckinDataDO::getCpId, cpId.id())
                .ge(WxCheckinDataDO::getCheckinTime, startTime.getTime() / 1000)
                .le(WxCheckinDataDO::getCheckinTime, endTime.getTime() / 1000)
                .in(WxCheckinDataDO::getWxUserId, userList);
        return attendanceConverter.toCheckinDataList(wxCheckinDataMapper.selectList(wrapper));
    }


    public void deleteAndSave(List<WxCpCheckinData> checkinDataList, SyncCheckinDataCmd syncCheckinDataCmd) {
        if (CollUtil.isEmpty(checkinDataList)) {
            log.info("{}到{}该时间段内，无打卡数据需要同步！", syncCheckinDataCmd.getStartDate(), syncCheckinDataCmd.getEndDate());
            return;
        }
        log.info("{} check data need sync", checkinDataList.size());
        LambdaQueryWrapper<WxCheckinDataDO> queryWrapper = Wrappers.lambdaQuery(WxCheckinDataDO.class).eq(WxCheckinDataDO::getCpId, syncCheckinDataCmd.getCpId())
                .ge(WxCheckinDataDO::getCheckinTime, syncCheckinDataCmd.getStartDate().getTime() / 1000)
                .le(WxCheckinDataDO::getCheckinTime, syncCheckinDataCmd.getEndDate().getTime() / 1000);
        if (StrUtil.isNotEmpty(syncCheckinDataCmd.getWxUserId())) {
            queryWrapper.eq(WxCheckinDataDO::getWxUserId, syncCheckinDataCmd.getWxUserId());
        }
        int deleteNum = wxCheckinDataMapper.delete(queryWrapper);
        log.info("Delete {} checkin data from {} to {}", deleteNum, syncCheckinDataCmd.getStartDate(), syncCheckinDataCmd.getEndDate());
        wxCheckinDataMapper.insertBatchSomeColumn(attendanceConverter.toCheckinDataDOList(checkinDataList, syncCheckinDataCmd.getCpId()));
        // 记录同步时间
        WxCheckinSyncDO wxCheckinSyncDO = WxCheckinSyncDO.builder().beginSec(syncCheckinDataCmd.getStartDate().getTime() / 1000)
                .endSec(syncCheckinDataCmd.getEndDate().getTime() / 1000).wxCpId(syncCheckinDataCmd.getCpId()).wxUserId(syncCheckinDataCmd.getWxUserId()).build();
        wxCheckinSyncMapper.insert(wxCheckinSyncDO);
        log.info("sync check data end");
    }

    public Long lastSync(String cpId, String wxUserId) {
        LambdaQueryWrapper<WxCheckinSyncDO> wrapper = Wrappers.lambdaQuery(WxCheckinSyncDO.class).eq(WxCheckinSyncDO::getWxCpId, cpId)
                .orderByDesc(WxCheckinSyncDO::getEndSec).last("limit 1");
        if (StrUtil.isNotEmpty(wxUserId)) {
            wrapper.eq(WxCheckinSyncDO::getWxUserId, wxUserId);
        } else {
            wrapper.and(w -> w.or(w1 -> w1.eq(WxCheckinSyncDO::getWxUserId, "")).or(w2 -> w2.isNull(WxCheckinSyncDO::getWxUserId)));
        }
        WxCheckinSyncDO wxCheckinSyncDO = wxCheckinSyncMapper.selectOne(wrapper);
        if (wxCheckinSyncDO != null && wxCheckinSyncDO.getEndSec() != null) {
            return wxCheckinSyncDO.getEndSec();
        }

        return DateUtil.beginOfMonth(new Date()).getTime() / 1000;
    }

    public void isSynced(Long startSec, Long endSec) {
        LambdaQueryWrapper<WxCheckinSyncDO> wrapper = Wrappers.lambdaQuery(WxCheckinSyncDO.class)
                .ge(WxCheckinSyncDO::getBeginSec, startSec).le(WxCheckinSyncDO::getEndSec, endSec);
        List<WxCheckinSyncDO> wxCheckinSyncDOS = wxCheckinSyncMapper.selectList(wrapper);
        List<TimeSection> timeSections = wxCheckinSyncDOS.stream().map(s -> new TimeSection(s.getBeginSec(), s.getEndSec())).toList();
        Periods existPeriods = new TimeSections(timeSections).toPeriods();

        Periods targetPeriods = new Periods(startSec, endSec);
        Periods complement = targetPeriods.complement(existPeriods);
        if (complement.sum() > 0) {
            for (Periods.Period period : complement.getPeriods()) {

            }

        }
    }

}
