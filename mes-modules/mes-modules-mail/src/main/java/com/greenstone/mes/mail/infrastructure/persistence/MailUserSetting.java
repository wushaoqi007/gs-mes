package com.greenstone.mes.mail.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("mail_user_setting")
public class MailUserSetting extends BaseEntity {
    @TableId
    private String email;
    private String appPassword;
    private Integer deleteMailsBeforeDays;
}
