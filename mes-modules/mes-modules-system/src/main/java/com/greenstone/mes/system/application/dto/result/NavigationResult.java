package com.greenstone.mes.system.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-10-18-10:34
 */
@Data
public class NavigationResult {

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Long updateId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
