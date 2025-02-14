package com.greenstone.mes.system.domain.entity;

import com.greenstone.mes.common.security.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-10-17-16:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Navigation {

    private Long id;

    private Long parentId;

    private String name;

    private String category;

    private String navigationType;

    private Boolean active;

    private Boolean visible;

    private Boolean cacheable;

    private Boolean openInNewtab;

    private Boolean showNavigation;

    private String icon;

    private Long functionId;

    private String link;

    private String queryParam;

    private Integer orderNum;

    private Long createId;

    private LocalDateTime createTime;

    private Long updateId;

    private LocalDateTime updateTime;

    public void autoCompleteCreateData() {
        this.createId = SecurityUtils.getLoginUser().getUser().getUserId();
        this.createTime = LocalDateTime.now();
    }

    public void autoCompleteUpdateData() {
        this.updateId = SecurityUtils.getLoginUser().getUser().getUserId();
        this.updateTime = LocalDateTime.now();
    }
}
