package com.greenstone.mes.oa.interfaces.request;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;

@Data
@Slf4j
public class ApprovalCorrectionImportCommand {

    @Excel(name = "审批编号")
    @NotEmpty(message = "审批编号不能为空")
    private String spNo;

    @Excel(name = "提交时间")
    private String applyTime;

    @Excel(name = "申请人")
    private String userName;

    @Excel(name = "申请人账号")
    @NotEmpty(message = "申请人账号不能为空")
    private String userId;

    @Excel(name = "补卡时间")
    private String correctionTime;

    @Excel(name = "补卡事由")
    private String reason;

    @Excel(name = "证明照片")
    @NotEmpty(message = "说明附件不能为空")
    private String proveFile;

    @Excel(name = "当前审批状态")
    @NotEmpty(message = "当前审批状态不能为空")
    private String status;

    @Excel(name = "备注")
    private String remark;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
