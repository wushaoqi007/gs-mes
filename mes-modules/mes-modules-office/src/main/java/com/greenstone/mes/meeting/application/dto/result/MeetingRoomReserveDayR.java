package com.greenstone.mes.meeting.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:33
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MeetingRoomReserveDayR {
    private String roomName;
    private Integer capacity;
    private List<DayReserve> reserveList;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class DayReserve {
        private String theme;
        private String reserveBy;
        private LocalDate useDate;
        @JsonFormat(pattern = "HH:mm")
        private LocalDateTime startTime;
        @JsonFormat(pattern = "HH:mm")
        private LocalDateTime endTime;
    }
}
