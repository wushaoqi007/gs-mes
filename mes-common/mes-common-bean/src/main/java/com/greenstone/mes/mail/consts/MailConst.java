package com.greenstone.mes.mail.consts;

public interface MailConst {

    interface SendStatus {
        int SUCCESS = 1;

        int FAILED = 2;

        int WAIT_RETRY = 3;
    }

    interface MailBoxType {
        /**
         * 个人邮箱
         */
        String PERSONAL = "personal";
        /**
         * 公共邮箱
         */
        String PUBLIC = "public";
        /**
         * 给外部人员使用的邮箱
         */
        String EXTERNAL = "external";

    }

}
