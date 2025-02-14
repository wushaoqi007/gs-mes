package com.greenstone.mes.meeting.domain.entity;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRoomReserve {
    private String id;
    private String roomId;
    private String roomName;
    private Integer capacity;
    private String theme;
    private LocalDate useDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime reserveTime;
    private String reserveById;
    private String reserveByNo;
    private String reserveBy;
    private Integer attendeeCount;
    private Integer status;
    private String description;

    private String attendeeJson;
    private List<MeetingRoomAttendee> attendeeList;

    public boolean suitableTime() {
        return this.startTime.isBefore(this.endTime);
    }

    public String attendeeListToJson() {
        if (CollUtil.isNotEmpty(this.attendeeList)) {
            return JSONObject.toJSONString(this.attendeeList);
        }
        return null;
    }

    public List<MeetingRoomAttendee> attendeeJsonToList() {
        if (StrUtil.isNotEmpty(this.attendeeJson)) {
            return JSONArray.parseArray(this.attendeeJson, MeetingRoomAttendee.class);
        }
        return null;
    }
}
