package com.greenstone.mes.mail.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("mail_box")
public class MailBox extends BaseEntity {
    @TableId
    private String email;
    private String name;
    private Long quota;
    private Long quotaUsed;
    private Long percentInUse;
    private String type;
    private Long userId;
    private String nickName;
    private String employeeNo;
    private String wxUserId;
    private String wxCpId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationTime;
    @TableLogic
    private Boolean deleted;
    private String deleteReason;
}
