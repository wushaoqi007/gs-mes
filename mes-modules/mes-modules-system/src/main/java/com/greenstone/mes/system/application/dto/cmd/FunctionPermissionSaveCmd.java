package com.greenstone.mes.system.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-13:54
 */
@Data
public class FunctionPermissionSaveCmd {

    private Long functionPermissionId;

    @NotEmpty(message = "请选择导航")
    private List<Long> navigationIds;

    @Valid
    @NotEmpty(message = "请添加成员")
    private List<MemberInfo> members;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        @NotNull(message = "成员id不能为空")
        private Long memberId;
        @NotBlank(message = "成员类型不能为空")
        private String memberType;
    }


}
