package com.greenstone.mes.system.consts;

public interface SysConst {

    Long PERM_ROOT_ID = 0L;

    Long NAVIGATION_ROOT_ID = 0L;

    String MENU_ROOT_ID = "0";

    interface MenuType {
        int MODULE = 1;
        int FOLDER = 2;
        int GROUP = 3;
        int SYS_MENU = 4;
        int SYS_EDITABLE_MENU = 5;
        // 嵌套路由
        int NEST_ROUTER = 6;
    }

    /**
     * 操作权限类型
     */
    interface PermType {
        /**
         * 模块
         */
        String MODULE = "MODULE";
        /**
         * 功能
         */
        String FUNCTION = "FUNCTION";
        /**
         * 操作
         */
        String ACTION = "ACTION";
    }

    interface FieldSource {
        int SYSTEM = 1;
        int CUSTOM = 2;
    }

}
