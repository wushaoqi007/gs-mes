package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-13:54
 */
@Data
public class MemberPermissionSaveCmd {
    @NotEmpty(message = "请选择功能权限组")
    private List<Long> functionPermissionIds;

    @NotEmpty(message = "请选择导航")
    private List<Long> navigationIds;

    private Long memberId;
    private String memberType;

}
