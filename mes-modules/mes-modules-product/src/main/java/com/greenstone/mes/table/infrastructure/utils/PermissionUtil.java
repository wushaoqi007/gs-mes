package com.greenstone.mes.table.infrastructure.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.domain.Condition;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.core.TableThreadLocal;
import com.greenstone.mes.table.infrastructure.constant.TableConst;

import java.lang.reflect.Field;
import java.util.List;

public class PermissionUtil {

    public static <E extends TableEntity> void checkPermission(E e, String action) {
        if (!hasPermission(e, action)) {
            throw new RuntimeException("操作失败：权限不足。");
        }
        if (TableConst.Rights.VIEW.equals(action) || TableConst.Rights.EXPORT.equals(action)) {
            TableThreadLocal.getTableMeta().setDataScopeSql(dataScopeSql());
        }
    }

    public static <E extends TableEntity> boolean editable(E e, String action) {
        return hasPermission(e, action);
    }

    public static String dataScopeSql() {
        // 管理员能够查看所有数据
        if (SecurityUtils.getLoginUser().isAdmin()) {
            return null;
        }

        UserPermissionResult permission = getPermission();
        // 没有权限会在权限检查时拦截，这里不做处理
        if (permission == null) {
            return null;
        }
        // 拥有功能管理权限能够查看所有数据
        if (permission.getRights().contains(TableConst.Rights.MANAGE)) {
            return null;
        }
        return genDataScopeSql(permission);
    }

    public static <E extends TableEntity> boolean hasManagePermission(E e, String right) {
        // 管理员拥有所有权限
        if (SecurityUtils.getLoginUser().isAdmin()) {
            return true;
        }
        UserPermissionResult permission = getPermission();
        // 没有权限不能操作
        if (permission == null || permission.getRights() == null) {
            return false;
        }
        // 拥有功能管理权限可以操作
        if (permission.getRights().contains(TableConst.Rights.MANAGE)) {
            return true;
        }
        return true;
    }

    public static <E extends TableEntity> boolean hasPermission(E e, String right) {
        // 管理员拥有所有权限
        if (SecurityUtils.getLoginUser().isAdmin()) {
            return true;
        }

        UserPermissionResult permission = getPermission();
        // 没有权限不能操作
        if (permission == null || permission.getRights() == null) {
            return false;
        }
        // 拥有功能管理权限可以操作
        if (permission.getRights().contains(TableConst.Rights.MANAGE)) {
            return true;
        }

        // 如果是普通用户更新，数据不能被锁定
        if (TableConst.Rights.UPDATE.equals(right) && e.getLocked()) {
            return false;
        }
        // 拥有普通操作权限且数和数据权限可以操作
        return permission.getRights().contains(right) && (!TableConst.Rights.UPDATE.equals(right) || hasUpdateDataPermission(e, permission));
    }

    private static UserPermissionResult getPermission() {
        Long functionId = TableThreadLocal.getTableMeta().getFunctionId();
        return SecurityUtils.getLoginUser().getPermissions().stream().filter(p -> p.getFunctionId().equals(functionId)).findFirst().orElse(null);
    }

    private static <E extends TableEntity> boolean hasUpdateDataPermission(E e, UserPermissionResult permission) {
        if (permission.getUpdateFilter() != null) {
            for (Condition condition : permission.getUpdateFilter()) {
                if (!conditionIsMatch(e, condition)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static <E extends TableEntity> boolean conditionIsMatch(E e, Condition condition) {
        Object value = ReflectUtil.getFieldValue(e, condition.getField());
        Object cndValue = condition.getValue().equals("myself") ? SecurityUtils.getUserId() : condition.getValue();
        return switch (condition.getCnd()) {
            case "eq" -> cndValue.equals(value);
            default -> throw new RuntimeException("不支持的权限条件：" + condition.getCnd());
        };
    }

    private static String genDataScopeSql(UserPermissionResult permissionResult) {
        List<Condition> viewFilter = permissionResult.getViewFilter();
        if (CollUtil.isEmpty(viewFilter)) {
            return null;
        }
        StringBuilder sql = new StringBuilder();
        for (Condition condition : viewFilter) {
            if ("myself".equals(condition.getValue())) {
                condition.setValue(String.valueOf(SecurityUtils.getUserId()));
            }
            sql.append(getCndSymbol(condition));
        }
        return sql.toString();
    }

    private static String getCndSymbol(Condition cnd) {
        return switch (cnd.getCnd()) {
            case "eq" -> getColumnName(cnd.getField()) + " = '" + cnd.getValue() + "'";
            case "ne" -> getColumnName(cnd.getField()) + " != " + cnd.getValue() + "'";
            case "lt" -> getColumnName(cnd.getField()) + " < " + cnd.getValue() + "'";
            case "le" -> getColumnName(cnd.getField()) + " <= " + cnd.getValue() + "'";
            case "gt" -> getColumnName(cnd.getField()) + " > " + cnd.getValue() + "'";
            case "ge" -> getColumnName(cnd.getField()) + " >= " + cnd.getValue() + "'";
            case "in" -> {
                StringBuilder sql = new StringBuilder();
                List<String> values = JSONArray.parseArray(cnd.getValue(), String.class);
                sql.append(" (");
                for (int i = 0; i < values.size(); i++) {
                    if (i != 0) {
                        sql.append(" or ");
                    }
                    sql.append(getColumnName(cnd.getField())).append(" like '%").append(values.get(i)).append("%'");
                }
                sql.append(") ");
                yield sql.toString();
            }
            default -> throw new RuntimeException("不支持的条件符号：" + cnd.getCnd());
        };
    }

    private static String getColumnName(String fieldName) {
        TableThreadLocal.ActionMeta<?, ?> tableMeta = TableThreadLocal.getTableMeta();
        Class<?> poClass = tableMeta.getPoClass();
        Field field = ReflectUtil.getField(poClass, fieldName);
        TableField declaredAnnotation = field.getDeclaredAnnotation(TableField.class);
        return declaredAnnotation != null && StrUtil.isNotEmpty(declaredAnnotation.value()) ? declaredAnnotation.value() :
                StrUtil.toUnderlineCase(fieldName);
    }
}