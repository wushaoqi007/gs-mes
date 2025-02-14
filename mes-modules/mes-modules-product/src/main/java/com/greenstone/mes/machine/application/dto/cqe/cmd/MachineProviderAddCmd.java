package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2024-05-22-13:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineProviderAddCmd {
    private String id;
    @NotEmpty(message = "供应商名称不为空")
    private String name;
    @NotEmpty(message = "供应商全称不为空")
    private String fullName;
    @NotEmpty(message = "供应商缩写不为空")
    private String abbrName;
    private String contactName;
    private String contactPhone;
    private String phone;
    private String address;
    private String bank;
    private String account;
    private String taxNumber;
    private String email;
}
