package com.greenstone.mes.system.interfaces.rest.cmd;

import com.greenstone.mes.common.core.constant.UserConstants;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class UserResetPassword {

    @NotNull(message = "请选择需要更新的用户")
    private Long userId;

    @NotNull(message = "密码不能为空")
    @Length(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH, message = "请输入5到20位的密码")
    private String password;

}
