package com.greenstone.mes.mail.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("mail_send_log")
public class MailSendLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long requestId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private Integer retryTimes;
    private Integer maxRetryTimes;
    private Integer size;
    private Integer status;

}
