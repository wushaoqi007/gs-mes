package com.greenstone.mes.meeting.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum MeetingRoomReserveStatus {

    NOT_STARTED(0, "未开始"),
    ONGOING(1, "进行中"),
    ENDED(2, "已结束"),
    ;

    @EnumValue
    @Getter
    private final int state;

    @Getter
    private final String name;

    MeetingRoomReserveStatus(int state, String name) {
        this.state = state;
        this.name = name;
    }

    public static List<MeetingRoomReserveStatus> statesByName(String nameLike){
        List<MeetingRoomReserveStatus> stateList = new ArrayList<>();
        for (MeetingRoomReserveStatus value : MeetingRoomReserveStatus.values()) {
            if (value.getName().contains(nameLike)){
                stateList.add(value);
            }
        }
        return stateList;
    }
}
