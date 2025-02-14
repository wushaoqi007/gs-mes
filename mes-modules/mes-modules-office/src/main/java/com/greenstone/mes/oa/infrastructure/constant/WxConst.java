package com.greenstone.mes.oa.infrastructure.constant;

public class WxConst {

    /**
     * 通讯录变更的变更类型
     * <a href="https://developer.work.weixin.qq.com/document/path/90970">成员变更通知</a>
     * <a href="https://developer.work.weixin.qq.com/document/path/90971">部门变更通知</a>
     */
    public static class ContactChangeType {
        /**
         * 创建用户
         */
        public static final String CREATE_USER = "create_user";
        /**
         * 更新用户
         */
        public static final String UPDATE_USER = "update_user";
        /**
         * 删除用户
         */
        public static final String DELETE_USER = "delete_user";
        /**
         * 创建部门
         */
        public static final String CREATE_PARTY = "create_party";
        /**
         * 更新部门
         */
        public static final String UPDATE_PARTY = "update_party";
        /**
         * 删除部门
         */
        public static final String DELETE_PARTY = "delete_party";
    }

}
