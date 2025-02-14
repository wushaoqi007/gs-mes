package com.greenstone.mes.oa.domain.event.data;

import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.Builder;
import lombok.Data;
import me.chanjar.weixin.cp.bean.oa.WxCpApprovalDetailResult;

/**
 * @author gu_renkai
 * @date 2022/11/16 16:03
 */
@Data
@Builder
public class WxApprovalData {

    private CpId cpId;

    private WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail;

}
