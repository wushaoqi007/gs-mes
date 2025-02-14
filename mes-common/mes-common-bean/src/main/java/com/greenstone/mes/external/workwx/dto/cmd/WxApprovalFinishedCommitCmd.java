package com.greenstone.mes.external.workwx.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author wushaoqi
 * @date 2024-07-09-13:59
 */
@Data
public class WxApprovalFinishedCommitCmd {
    private String cpId;
    private Integer appId;
    @NotEmpty(message = "单号不为空")
    private String serialNo;
    @NotEmpty(message = "领用时间不为空")
    private String takeTime;
    @NotEmpty(message = "领用人不为空")
    private String takeBy;
    @NotNull(message = "领用人id不为空")
    private Long takeById;
    @NotEmpty(message = "经手人不为空")
    private String sponsor;
    @NotNull(message = "经手人id不为空")
    private Long sponsorId;

}
