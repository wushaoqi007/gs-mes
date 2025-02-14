package com.greenstone.mes.common.datascope.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.greenstone.mes.common.datascope.annotation.DataScope;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.api.domain.SysRole;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Slf4j
@Component
public class DataScopeHandler implements DataPermissionHandler {

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 默认的部门字段
     */
    private static final String DEFAULT_DEPT_FIELD = "dept_id";

    /**
     * 默认的用户字段
     */
    private static final String DEFAULT_USER_FIELD = "create_by";

    /**
     * 管理员角色的 role_key
     */
    private static final String ADMIN_ROLE_KEY = "admin";

    /**
     * 默认支持单次的数据权限
     */
    @Value("${mybatis-plus.dataScope.oncePolicy: true}")
    private boolean dataScopeOncePolicy;

    /**
     * 通过ThreadLocal记录权限相关的属性值
     */
    private final ThreadLocal<DataScopeParam> SCOPE_THREAD_LOCAL = new ThreadLocal<>();


    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        DataScopeSwitch.useOnce();
        if (DataScopeSwitch.isClose()) {
            return where;
        }
        DataScopeParam dataScopeParam = SCOPE_THREAD_LOCAL.get();
        if (dataScopeParam == null || dataScopeParam.isAdmin()) {
            return where;
        }
        Expression expression = scopeExpression(dataScopeParam);
        return where == null ? (expression == null ? null : new Parenthesis(expression)) :
                (expression == null ? where : new AndExpression(where, new Parenthesis(expression)));
    }

    @SuppressWarnings("unchecked")
    private Expression scopeExpression(DataScopeParam dataScopeParam) {
        List<Expression> expressions = new ArrayList<>();
        // 适用指定角色
        if (StrUtil.isNotEmpty(dataScopeParam.getSuitRoleKeys())) {
            List<String> suitRoleKeys = Arrays.stream(dataScopeParam.getSuitRoleKeys().split(",")).toList();
            List<SysRole> suitRoles = dataScopeParam.getRoles().stream().filter(r -> suitRoleKeys.contains(r.getRoleKey())).collect(Collectors.toList());
            // 接口指定了适用角色，但登录人没有分配适用角色，不查询任何数据
            if (CollUtil.isEmpty(suitRoles)) {
                return new HexValue("1=0");
            }
            dataScopeParam.setRoles(suitRoles);
        }
        for (SysRole role : dataScopeParam.getRoles()) {
            if (DATA_SCOPE_ALL.equals(role.getDataScope())) {
                expressions = Collections.EMPTY_LIST;
                break;
            }
            Expression expression = switch (role.getDataScope()) {
                // 自定义数据权限
                case DATA_SCOPE_CUSTOM -> {
                    String field = StrUtil.isEmpty(dataScopeParam.getDeptField()) ? DEFAULT_DEPT_FIELD : dataScopeParam.getDeptField();
                    String column = StrUtil.isEmpty(dataScopeParam.getDeptAlias()) ? field : dataScopeParam.getDeptAlias() + "." + field;
                    String sqlValue = StrUtil.format("{} in (SELECT dept_id FROM mes_system.sys_role_dept WHERE role_id = {})",
                            column, role.getRoleId());
                    yield new HexValue(sqlValue);
                }
                // 部门数据权限
                case DATA_SCOPE_DEPT -> {
                    String field = StrUtil.isEmpty(dataScopeParam.getDeptField()) ? DEFAULT_DEPT_FIELD : dataScopeParam.getDeptField();
                    String column = StrUtil.isEmpty(dataScopeParam.getDeptAlias()) ? field : dataScopeParam.getDeptAlias() + "." + field;
                    Expression eqExpression = new LongValue(SecurityUtils.getLoginUser().getUser().getDeptId());
                    yield new EqualsTo(new Column(column), eqExpression);
                }
                // 部门及以下数据权限
                case DATA_SCOPE_DEPT_AND_CHILD -> {
                    String field = StrUtil.isEmpty(dataScopeParam.getDeptField()) ? DEFAULT_DEPT_FIELD : dataScopeParam.getDeptField();
                    String column = StrUtil.isEmpty(dataScopeParam.getDeptAlias()) ? field : dataScopeParam.getDeptAlias() + "." + field;
                    String sqlValue = StrUtil.format("{} in (SELECT dept_id FROM mes_system.sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ))",
                            column, SecurityUtils.getLoginUser().getUser().getDeptId(), SecurityUtils.getLoginUser().getUser().getDeptId());
                    yield new HexValue(sqlValue);
                }
                // 本人数据权限
                case DATA_SCOPE_SELF -> {
                    String field = StrUtil.isEmpty(dataScopeParam.getUserField()) ? DEFAULT_USER_FIELD : dataScopeParam.getUserField();
                    String column = StrUtil.isEmpty(dataScopeParam.getUserAlias()) ? field : dataScopeParam.getUserAlias() + "." + field;
                    Expression eqExpression = new LongValue(SecurityUtils.getUserId());
                    yield new EqualsTo(new Column(column), eqExpression);
                }
                default -> null;
            };
            if (expression != null) {
                expressions.add(expression);
            }
        }

        return switch (expressions.size()) {
            case 0 -> null;
            case 1 -> expressions.get(0);
            case 2 -> new OrExpression(expressions.get(0), expressions.get(1));
            default -> {
                OrExpression orExpression = new OrExpression(expressions.get(0), expressions.get(1));
                for (int i = 2; i < expressions.size(); i++) {
                    orExpression.withRightExpression(expressions.get(i));
                }
                yield orExpression;
            }
        };
    }


    @Before("@annotation(dataScope)")
    public void doBefore(DataScope dataScope) {
        if (dataScope != null) {
//            // 获取用户角色对应的数据权限
//            User loginUser = SecurityUtils.getLoginUser().getUser();
//            if (loginUser.getRole() == null) {
//                return;
//            }
//            boolean isAdmin = loginUser.getRoles().stream().anyMatch(role -> role.getRoleKey().equals(ADMIN_ROLE_KEY));
//            DataScopeParam scopeParam = DataScopeParam.builder().admin(isAdmin).roles(loginUser.getRoles())
//                    .deptAlias(dataScope.deptAlias()).deptField(dataScope.deptField())
//                    .userAlias(dataScope.userAlias()).userField(dataScope.userField())
//                    .suitRoleKeys(dataScope.suitRoleKeys()).build();
//            SCOPE_THREAD_LOCAL.set(scopeParam);
//            if (dataScopeOncePolicy) {
//                DataScopeSwitch.openOnce(dataScope.pageable());
//            } else {
//                DataScopeSwitch.open();
//            }
        }
    }

    @After("@annotation(dataScope)")
    public void clearThreadLocal(DataScope dataScope) {
        SCOPE_THREAD_LOCAL.remove();
        DataScopeSwitch.remove();
    }

    /**
     * ThreadLocal存储对象
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class DataScopeParam {
        /**
         * 部门表的别名
         */
        private String deptAlias;

        /**
         * 用户表别名
         */
        private String userAlias;

        /**
         * 部门字段
         */
        private String deptField;

        /**
         * 用户字段
         */
        private String userField;

        /**
         * 适用角色【数组：逗号','分隔】
         */
        private String suitRoleKeys;

        /**
         * 是否是管理员
         */
        private boolean admin;

        /**
         * 角色信息
         */
        private List<SysRole> roles;

        /**
         * 是否分页
         */
        private boolean pagination;
    }
}
