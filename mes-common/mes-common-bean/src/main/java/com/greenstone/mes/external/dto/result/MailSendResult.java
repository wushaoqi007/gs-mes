package com.greenstone.mes.external.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailSendResult {

    /**
     * 邮件请求id
     */
    private Long mailId;
    /**
     * 业务key
     */
    private String businessKey;
    /**
     * 序列号
     */
    private String serialNo;
    /**
     * 邮件状态
     */
    private Integer status;
    /**
     * 当前发送次数
     */
    private Integer retryTimes;
    /**
     * 最大重试次数
     */
    private Integer maxRetryTimes;
    /**
     * 错误信息
     */
    private String errorMsg;

}
