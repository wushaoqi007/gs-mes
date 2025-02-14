package com.greenstone.mes.meeting.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-10-27-13:17
 */
@Data
public class MeetingRoomReserveEndCmd {

    @NotEmpty(message = "请选择预约记录")
    private List<String> ids;
}
