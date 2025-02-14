package com.greenstone.mes.mq.consts;

public interface MqConst {

    String GROUP = "default_group";

    interface Group {
        String OFFICE = "office";
        String PRODUCT = "product";
        String SYSTEM = "system";
        String WORKFLOW = "workflow";
        String WXCP = "wxcp";
        String MAIL = "mail";
        String TABLE = "table";
    }

    interface TopicPrefix {
        // 企业微信回调
        String WXCP_CALLBACK_TOPIC = "wx_callback-";

        // 企业微信发送消息
        String WXCP_MSGSEND_TOPIC = "wx_msgsend-";
    }

    interface TagSuffix {
        String MEAL_TAG = "meal_report";

        String APPROVAL = "approval";

        String CONTACT = "contact";
    }

    interface Topic {
        String FLOW_APPROVAL_CHANGE = "flow_approval_change";

        String WX_CALLBACK_MEAL_REPORT = TopicPrefix.WXCP_CALLBACK_TOPIC + TagSuffix.MEAL_TAG;

        String WX_CALLBACK_APPROVAL = TopicPrefix.WXCP_CALLBACK_TOPIC + TagSuffix.APPROVAL;

        String WX_CALLBACK_CONTACT = TopicPrefix.WXCP_CALLBACK_TOPIC + TagSuffix.CONTACT;

        String WX_MSGSEND = TopicPrefix.WXCP_MSGSEND_TOPIC;

        String MAIL_SEND_RESULT = "mail_send_result";

        String USER_CREATE = "user_create";

        String USER_DELETE = "user_delete";

        String USER_EMPLOYNO_ADDED = "user_employno_added";

        String MAIL_CREATE = "mail_create";
    }


}
