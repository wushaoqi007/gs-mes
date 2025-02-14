package com.greenstone.mes.oa.application.dto;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/24 10:57
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassedApprovalQuery {

    private CpId cpId;

    private WxUserId userId;

    private List<String> userIds;

    private Date start;

    private Date end;

}
