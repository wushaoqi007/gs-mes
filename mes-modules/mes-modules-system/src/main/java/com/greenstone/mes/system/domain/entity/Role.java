package com.greenstone.mes.system.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.system.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-21-10:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private Long roleId;
    @NotBlank(message = "请填写角色名称")
    private String roleName;
    private Integer userNum;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private List<User> users;
}
