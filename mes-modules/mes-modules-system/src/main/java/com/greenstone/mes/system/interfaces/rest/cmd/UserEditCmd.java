package com.greenstone.mes.system.interfaces.rest.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserEditCmd {

    @NotNull(message = "请选择需要更新的用户")
    private Long userId;

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

    @NotBlank(message = "权限类型不能为空")
    private String permissionType;

}
