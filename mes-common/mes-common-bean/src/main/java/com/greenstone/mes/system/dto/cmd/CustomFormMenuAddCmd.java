package com.greenstone.mes.system.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomFormMenuAddCmd {

    @NotBlank(message = "缺少菜单ID信息")
    private String menuId;

    @NotBlank(message = "缺少菜单名称")
    private String menuName;

    @NotBlank(message = "缺少父菜单名称")
    private String parentId;

    @NotBlank(message = "缺少菜单图标")
    private String icon;

    @NotBlank(message = "缺少数据表名称")
    private String dataTableName;

    @NotNull(message = "请指定是否使用流程")
    private Boolean usingProcess;

}
