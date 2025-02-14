package com.greenstone.mes.machine.application.dto.cqe.cmd;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2024-05-22-13:36
 */
@Data
public class MachineProviderImportCmd {
    @Excel(name = "供应商名称")
    @NotEmpty(message = "供应商名称不为空")
    private String name;
    @Excel(name = "供应商全称")
    @NotEmpty(message = "供应商全称不为空")
    private String fullName;
    @Excel(name = "拼音简称")
    private String abbrName;
    @Excel(name = "联系人")
    private String contactName;
    @Excel(name = "联系方式")
    private String contactPhone;
    @Excel(name = "电话")
    private String phone;
    @Excel(name = "地址")
    private String address;
    @Excel(name = "开户行")
    private String bank;
    @Excel(name = "帐号")
    private String account;
    @Excel(name = "税号")
    private String taxNumber;
    @Excel(name = "邮箱")
    private String email;
}
