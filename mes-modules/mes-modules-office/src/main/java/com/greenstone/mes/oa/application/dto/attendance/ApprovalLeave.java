package com.greenstone.mes.oa.application.dto.attendance;

import com.greenstone.mes.oa.infrastructure.util.Periods;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApprovalLeave extends ApprovalVector {

    /**
     * 请假类型
     */
    private String type;

    private String reason;

    /**
     * 请假区间
     */
    private Periods periods;

}
