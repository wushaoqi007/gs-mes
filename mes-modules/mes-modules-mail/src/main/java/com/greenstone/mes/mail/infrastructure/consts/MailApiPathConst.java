package com.greenstone.mes.mail.infrastructure.consts;

public interface MailApiPathConst {

    interface MailBox {
        String CREATE = "/api/v1/add/mailbox";

        String DELETE = "/api/v1/delete/mailbox";

        String EDIT = "/api/v1/edit/mailbox";

        String GET = "/api/v1/get/mailbox/{id}";

        String GET_MAILBOXES = "/api/v1/get/mailbox/all/{domain}";
    }

    interface Domain {

        String GET = "/api/v1/get/domain/{id}";

    }

}
