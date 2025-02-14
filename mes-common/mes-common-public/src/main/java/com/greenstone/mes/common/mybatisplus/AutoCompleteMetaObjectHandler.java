package com.greenstone.mes.common.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.form.infrastructure.persistence.BaseFormPo;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.table.TablePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据库字段自动填充
 */
@Slf4j
@Component
public class AutoCompleteMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        User sysUser = SecurityUtils.getLoginUser() == null ? null : SecurityUtils.getLoginUser().getUser();
        String nickname;
        if (sysUser == null) {
            nickname = "admin";
        } else {
            nickname = sysUser.getNickName();
        }
        if (metaObject.getOriginalObject() instanceof BaseEntity) {
            strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            strictInsertFill(metaObject, "createBy", String.class, nickname);
        } else if (metaObject.getOriginalObject() instanceof TablePo) {
            strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            strictInsertFill(metaObject, "createBy", Long.class, SecurityUtils.getUserId());
            strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            strictInsertFill(metaObject, "updateBy", Long.class, SecurityUtils.getUserId());
        } else if (metaObject.getOriginalObject() instanceof BaseFormPo) {
            strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            strictInsertFill(metaObject, "createBy", String.class, nickname);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        User user = SecurityUtils.getLoginUser() == null ? null : SecurityUtils.getLoginUser().getUser();
        String nickname;
        if (user == null) {
            nickname = "admin";
        } else {
            nickname = user.getNickName();
        }
        if (metaObject.getOriginalObject() instanceof BaseEntity) {
            strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            strictUpdateFill(metaObject, "updateBy", String.class, nickname);
        } else if (metaObject.getOriginalObject() instanceof TablePo) {
            if (user != null) {
                setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
                setFieldValByName("updateBy", SecurityUtils.getUserId(), metaObject);
            }
        } else if (metaObject.getOriginalObject() instanceof BaseFormPo) {
            strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            strictUpdateFill(metaObject, "updateBy", String.class, nickname);
        }
    }
}
