package com.greenstone.mes.system.api.model;

import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户登录信息
 *
 * @author gurenkai
 */
@Data
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 用户名id
     */
    private Long userid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 角色
     */
    private Set<String> roles;

    /**
     * 权限
     */
    private Set<UserPermissionResult> permissions = new HashSet<>();

    /**
     * 用户信息
     */
    private User user;

    public boolean isAdmin() {
        if (this.user == null) {
            return false;
        }
        return this.getUser().isAdmin();
    }

}
