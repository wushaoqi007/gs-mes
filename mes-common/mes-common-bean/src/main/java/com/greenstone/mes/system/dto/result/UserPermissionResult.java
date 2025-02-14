package com.greenstone.mes.system.dto.result;

import com.greenstone.mes.system.domain.Condition;
import com.greenstone.mes.system.domain.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-25-14:16
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserPermissionResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long functionId;

    private String functionName;

    private String functionType;

    private String source;

    private String component;

    private String formComponent;

    private Boolean usingProcess;

    private String templateId;

    private Integer orderNum;

    private Long functionPermissionId;

    private String permissionGroupName;

    private String permissionGroupTypeName;

    private Permission permission;

    private List<String> rights;

    private List<Condition> viewFilter;

    private List<Condition> updateFilter;

    private List<Navigation> navigations;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class Navigation {
        private Long navigationId;
        private String navigationName;
    }
}
