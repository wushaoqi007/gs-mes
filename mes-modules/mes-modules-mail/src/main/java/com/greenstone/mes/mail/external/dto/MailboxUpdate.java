package com.greenstone.mes.mail.external.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailboxUpdate {

    private List<String> items;

    private Attr attr;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attr {
        private String active;

        @JSONField(name = "force_pw_update")
        private String forcePwUpdate;

        private String name;

        private String password;

        private String password2;

        private String quota;

        @JSONField(name = "sender_acl")
        private List<String> senderAcl;

        @JSONField(name = "sogo_access")
        private String sogoAccess;

        private List<String> tags;

    }
}
