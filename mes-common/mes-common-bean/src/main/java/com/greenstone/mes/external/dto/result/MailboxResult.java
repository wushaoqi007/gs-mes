package com.greenstone.mes.external.dto.result;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class MailboxResult {

    private String username;

    private Integer active;

    @JSONField(name = "active_int")
    private Integer activeInt;

    private String domain;

    private String relayhost;

    private String name;

    @JSONField(name = "local_part")
    private String localPart;

    /**
     * 单位：byte 如果计算GB 需要 / 1024 / 1024 / 1024
     */
    private Long quota;

    private Integer messages;

    @JSONField(name = "quota_used")
    private Long quotaUsed;

    @JSONField(name = "percent_in_use")
    private String percentInUse;

    private String created;

    private String modified;

    @JSONField(name = "percent_class")
    private String percentClass;

    @JSONField(name = "last_imap_login")
    private String lastImapLogin;

    @JSONField(name = "last_smtp_login")
    private String lastSmtpLogin;

    @JSONField(name = "last_pop3_login")
    private String lastPop3Login;

    @JSONField(name = "max_new_quota")
    private Long maxNewQuota;

    @JSONField(name = "spam_aliases")
    private String spamAliases;

    @JSONField(name = "pushover_active")
    private String pushoverActive;

    private String rl;

    @JSONField(name = "rl_scope")
    private String rlScope;

    @JSONField(name = "is_relayed")
    private String isRelayed;

    private List<MailAttr> attributes;

    @Data
    public static class MailAttr {
        @JSONField(name = "force_pw_update")
        private String forcePwUpdate;

        @JSONField(name = "tls_enforce_in")
        private String tlsEnforceIn;

        @JSONField(name = "tls_enforce_out")
        private String tlsEnforceOut;

        @JSONField(name = "sogo_access")
        private String sogoAccess;

        @JSONField(name = "imap_access")
        private String imapAccess;

        @JSONField(name = "pop3_access")
        private String pop3Access;

        @JSONField(name = "smtp_access")
        private String smtpAccess;

        @JSONField(name = "sieve_access")
        private String sieveAccess;

        private String relayhost;

        @JSONField(name = "passwd_update")
        private String passwdUpdate;

        @JSONField(name = "mailbox_format")
        private String mailboxFormat;

        @JSONField(name = "quarantine_notification")
        private String quarantineNotification;

        @JSONField(name = "quarantine_category")
        private String quarantineCategory;
    }


    public Long getMibQuota() {
        return this.quota / 1024 / 1024;
    }

    public Long getMibQuotaUsed() {
        return this.quotaUsed / 1024 / 1024;
    }

    public Long getCalcPercentInUse() {
        return this.quota == 0 ? this.quotaUsed * 100 / this.maxNewQuota : this.quotaUsed * 100 / this.quota;
    }
}
