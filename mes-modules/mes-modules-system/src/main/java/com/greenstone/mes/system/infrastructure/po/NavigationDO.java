package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "sys_navigation")
public class NavigationDO {

    @TableId(type = IdType.AUTO)
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
}
