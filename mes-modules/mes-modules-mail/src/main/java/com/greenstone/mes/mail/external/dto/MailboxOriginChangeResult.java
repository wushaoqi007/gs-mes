package com.greenstone.mes.mail.external.dto;

import com.alibaba.fastjson2.JSONArray;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.greenstone.mes.external.dto.result.MailboxChangeResult;
import lombok.Data;

import java.util.List;

@Data
public class MailboxOriginChangeResult {

    private JSONArray log;

    private List<String> msg;

    private String type;


    @JsonIgnore
    public boolean isSuccess() {
        return "success".equals(type);
    }

    @JsonIgnore
    public String getResult() {
        String result = switch (msg.get(0)) {
            case "object_exists" -> "邮箱已存在：";
            case "mailbox_added" -> "邮箱创建成功：";
            case "mailbox_removed" -> "邮箱删除成功：";
            case "mailbox_modified" -> "邮箱修改成功：";
            case "access_denied" -> "访问被拒绝";
            case "password_complexity" -> "密码最少6位";
            case "mailbox_quota_left_exceeded" -> "邮箱容量超出最大配额";
            default -> "未知结果：" + msg.get(0);
        };
        return msg.size() > 1 ? result + msg.get(1) : result;
    }

    @JsonIgnore
    public String getMailAddress() {
        return msg.size() > 1 ? msg.get(1) : null;
    }

    public MailboxChangeResult buildChangeResult() {
        return MailboxChangeResult.builder()
                .type(this.getType())
                .success(this.isSuccess())
                .message(this.getResult()).build();
    }

}
