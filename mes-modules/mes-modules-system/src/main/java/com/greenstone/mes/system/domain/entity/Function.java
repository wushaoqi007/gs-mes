package com.greenstone.mes.system.domain.entity;

import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.domain.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-10-18-8:46
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Function implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String type;

    private String source;

    private String component;

    private String formComponent;

    private Boolean usingProcess;

    private String templateId;

    private Integer orderNum;

    private Long createId;

    private LocalDateTime createTime;

    private Long updateId;

    private LocalDateTime updateTime;

    private Permission permission;

    public void autoCompleteCreateData() {
        this.createId = SecurityUtils.getLoginUser().getUser().getUserId();
        this.createTime = LocalDateTime.now();
    }

    public void autoCompleteUpdateData() {
        this.updateId = SecurityUtils.getLoginUser().getUser().getUserId();
        this.updateTime = LocalDateTime.now();
    }


}
