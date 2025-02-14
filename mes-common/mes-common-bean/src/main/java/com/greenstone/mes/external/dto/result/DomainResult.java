package com.greenstone.mes.external.dto.result;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class DomainResult {

    private String active;

    @JSONField(name = "aliases_in_domain")
    private Integer aliasesInDomain;

    @JSONField(name = "aliases_left")
    private Integer aliasesLeft;

    private String backupmx;

    @JSONField(name = "bytes_total")
    private String bytesTotal;

    @JSONField(name = "def_new_mailbox_quota")
    private Long defNewMailboxQuota;

    @JSONField(name = "def_quota_for_mbox")
    private Long defQuotaForMbox;

    private String description;

    @JSONField(name = "domain_name")
    private String domainName;

    private String gal;

    @JSONField(name = "max_new_mailbox_quota")
    private Long maxNewMailboxQuota;

    @JSONField(name = "max_num_aliases_for_domain")
    private Integer maxNumAliasesForDomain;

    @JSONField(name = "max_num_mboxes_for_domain")
    private Integer maxNumMboxesForDomain;

    @JSONField(name = "max_quota_for_domain")
    private Long maxQuotaForDomain;

    @JSONField(name = "max_quota_for_mbox")
    private Long maxQuotaForMbox;

    @JSONField(name = "mboxes_in_domain")
    private Integer mboxesInDomain;

    @JSONField(name = "mboxes_left")
    private Integer mboxesLeft;

    @JSONField(name = "msgs_total")
    private String msgsTotal;

    @JSONField(name = "quota_used_in_domain")
    private String quotaUsedInDomain;

    @JSONField(name = "relay_all_recipients")
    private String relayAllRecipients;

    private String relayhost;

    private Boolean rl;

    public Long getMaxMibQuotaForMbox() {
        return this.maxQuotaForMbox / 1024 / 1024;
    }

}
