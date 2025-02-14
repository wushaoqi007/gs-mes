package com.greenstone.mes.oa.application.dto.attendance.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveApprovalExport {

    private Integer number;

    private String name;

    private String type;

    private String startTime;

    private String endTime;

    private String reason;

    private String approver;

    private List<String> fileIds;

}
