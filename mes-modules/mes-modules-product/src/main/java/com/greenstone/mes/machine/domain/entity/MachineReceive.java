package com.greenstone.mes.machine.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-08-9:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineReceive {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDateTime receiveTime;
    private String receiver;
    private Long receiverId;
    private String receiverNo;
    private String remark;
    private List<MachineReceiveDetail> parts;

    private String provider;
}
