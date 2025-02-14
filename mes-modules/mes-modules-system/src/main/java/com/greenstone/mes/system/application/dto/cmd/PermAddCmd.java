package com.greenstone.mes.system.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermAddCmd {

    @NotBlank(message = "请填写权限编码")
    private String permCode;

    @NotBlank(message = "请填写权限名称")
    private String permName;

    @NotBlank(message = "请指定权限类型")
    private String permType;

    private Long parentId;



}
