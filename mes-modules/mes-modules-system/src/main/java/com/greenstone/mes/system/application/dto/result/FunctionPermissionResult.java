package com.greenstone.mes.system.application.dto.result;

import com.greenstone.mes.system.domain.Condition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:18
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FunctionPermissionResult {

    private Long functionId;

    private String functionName;

    private String functionType;

    private List<PermissionGroup> permissionGroups;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class PermissionGroup {
        private Long functionId;
        private Long functionPermissionId;
        private String permissionGroupName;
        private String permissionGroupTypeName;
        private List<String> rights;
        private List<Condition> viewFilter;
        private List<Condition> updateFilter;

    }

}
