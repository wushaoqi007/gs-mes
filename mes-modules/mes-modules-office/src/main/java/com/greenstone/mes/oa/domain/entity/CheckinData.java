package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/24 13:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckinData implements Comparable<CheckinData> {

    private WxUserId userId;

    private Integer groupId;

    private String groupName;

    private Integer scheduleId;

    private String checkinType;

    private Long checkinTime;

    private Long schCheckinTime;

    private String locationTitle;

    private String locationDetail;

    private String notes;

    private List<String> mediaIds;

    private String deviceId;

    private boolean invalid;

    private boolean remit;

    /**
     * lat	位置打卡地点纬度，是实际纬度的1000000倍，与腾讯地图一致采用GCJ-02坐标系统标准
     */
    private Integer lat;

    /**
     * lng	位置打卡地点经度，是实际经度的1000000倍，与腾讯地图一致采用GCJ-02坐标系统标准
     */
    private Integer lng;

    @Override
    public int compareTo(CheckinData o) {
        return (this.getCheckinTime() - o.getCheckinTime()) < 0 ? -1 : 1;
    }
}
