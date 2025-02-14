package com.greenstone.mes.wxcp.infrastructure.constant;

public interface WxcpConst {

    /**
     * 通讯录变更的变更类型
     * <a href="https://developer.work.weixin.qq.com/document/path/90970">成员变更通知</a>
     * <a href="https://developer.work.weixin.qq.com/document/path/90971">部门变更通知</a>
     */
    interface ContactChangeType {
        /**
         * 创建用户
         */
        String CREATE_USER = "create_user";
        /**
         * 更新用户
         */
        String UPDATE_USER = "update_user";
        /**
         * 删除用户
         */
        String DELETE_USER = "delete_user";
        /**
         * 创建部门
         */
        String CREATE_PARTY = "create_party";
        /**
         * 更新部门
         */
        String UPDATE_PARTY = "update_party";
        /**
         * 删除部门
         */
        String DELETE_PARTY = "delete_party";
    }

}
