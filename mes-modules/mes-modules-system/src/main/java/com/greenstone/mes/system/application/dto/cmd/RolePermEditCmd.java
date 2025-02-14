package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RolePermEditCmd {

    @NotNull(message = "缺少角色信息")
    private Long roleId;

    @NotEmpty(message = "缺少权限信息")
    private List<String> permIds;

}
