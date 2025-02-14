package com.greenstone.mes.oa.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/22 15:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalContentPunchCorrection {

    private String state;

    private Date time;

}
