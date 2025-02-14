package com.greenstone.mes.system.interfaces.rest.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserAddCmd {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "姓名不能为空")
    private String nickName;

    private String employeeNo;

    private String email;

    private String phonenumber;

    private String wxUserId;

    private String wxCpId;

    private Long roleId;

    private Long deptId;

    private String position;

    private String sex;

    private String avatar;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "权限类型不能为空")
    private String permissionType;

}
