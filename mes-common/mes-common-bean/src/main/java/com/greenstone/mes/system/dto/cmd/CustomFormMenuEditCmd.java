package com.greenstone.mes.system.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomFormMenuEditCmd {

    @NotBlank(message = "请指定需要修改的菜单")
    private String menuId;

    private String menuName;

    private String icon;

    private String customJson;

    private Boolean usingProcess;

}
