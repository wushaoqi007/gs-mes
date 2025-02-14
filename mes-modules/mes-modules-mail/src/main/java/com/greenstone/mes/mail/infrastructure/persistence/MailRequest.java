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
@TableName("mail_request")
public class MailRequest {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String businessKey;
    private String serialNo;
    private Integer status;
    private String subject;
    private String sendTo;
    private String copyTo;
    private LocalDateTime receiveTime;
    private LocalDateTime endTime;
    private Integer retryTimes;
    private Integer maxRetryTimes;
    private String mailJson;

}
