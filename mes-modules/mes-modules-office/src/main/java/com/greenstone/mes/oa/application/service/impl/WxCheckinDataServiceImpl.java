package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.oa.application.service.WxCheckinDataService;
import com.greenstone.mes.oa.domain.repository.WxCheckinDataRepository;
import com.greenstone.mes.oa.infrastructure.enums.WxCp;
import com.greenstone.mes.oa.request.SyncCheckinDataCmd;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-07-11-11:02
 */
@Slf4j
@Service
@AllArgsConstructor
public class WxCheckinDataServiceImpl implements WxCheckinDataService {
    private final WxCheckinDataRepository wxCheckinDataRepository;
    private final WxUserService externalWxUserService;
    private final WxOaService externalWxOaService;

    @Override
    public List<WxCpCheckinData> listCheckinData(CpId cpId, Date startTime, Date endTime, List<String> userList) {
        return wxCheckinDataRepository.list(cpId, startTime, endTime, userList);
    }

    @Transactional
    @Override
    public void syncCheckData(SyncCheckinDataCmd syncCheckinDataCmd) {
        log.info("SyncCheckinDataCmd params:{}", syncCheckinDataCmd);
        if (StrUtil.isEmpty(syncCheckinDataCmd.getCpId())) {
            syncCheckinDataCmd.setCpId(WxCp.AUTOMATION.getCpId());
        }
        // 查询同步记录最新时间，如果没有则同步当月0点到new date()
        Long lastSyncSec = wxCheckinDataRepository.lastSync(syncCheckinDataCmd.getCpId(), syncCheckinDataCmd.getWxUserId());
        if (Objects.isNull(syncCheckinDataCmd.getStartDate())) {
            syncCheckinDataCmd.setStartDate(DateUtil.date(lastSyncSec * 1000));
        }
        if (Objects.isNull(syncCheckinDataCmd.getEndDate())) {
            syncCheckinDataCmd.setEndDate(new Date());
        }
        log.info("start sync check data:{}", syncCheckinDataCmd);
        List<WxUserId> wxUserIds;
        if (StrUtil.isNotEmpty(syncCheckinDataCmd.getWxUserId())) {
            wxUserIds = List.of(new WxUserId(syncCheckinDataCmd.getWxUserId()));
        } else {
            List<WxCpUser> allUsers = externalWxUserService.listAllUser(new CpId(syncCheckinDataCmd.getCpId()));
            wxUserIds = allUsers.stream().map(u -> new WxUserId(u.getUserId())).toList();
        }
        List<String> userIds = wxUserIds.stream().map(WxUserId::id).toList();
        // 获取打卡数据
        List<WxCpCheckinData> wxCheckinDataList = externalWxOaService.listCheckinData(new CpId(syncCheckinDataCmd.getCpId()),
                syncCheckinDataCmd.getStartDate(), syncCheckinDataCmd.getEndDate(), userIds);
        wxCheckinDataList = wxCheckinDataList.stream().filter(c -> !c.getExceptionType().contains("未打卡")).toList();
        wxCheckinDataRepository.deleteAndSave(wxCheckinDataList, syncCheckinDataCmd);
    }
}
