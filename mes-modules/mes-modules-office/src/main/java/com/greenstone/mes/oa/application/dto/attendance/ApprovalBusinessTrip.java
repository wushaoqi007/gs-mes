package com.greenstone.mes.oa.application.dto.attendance;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApprovalBusinessTrip extends ApprovalVector {

    private Long start;

    private Long end;

}
