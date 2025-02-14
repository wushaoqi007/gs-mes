package com.greenstone.mes.mail.domain.repository;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.greenstone.mes.mail.consts.MailConst;
import com.greenstone.mes.mail.domain.entity.MailSimple;
import com.greenstone.mes.mail.infrastructure.mapper.MailRequestMapper;
import com.greenstone.mes.mail.infrastructure.mapper.MailSendLogMapper;
import com.greenstone.mes.mail.infrastructure.persistence.MailRequest;
import com.greenstone.mes.mail.infrastructure.persistence.MailSendLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailRequestRepository {

    private final MailRequestMapper mailRequestMapper;
    private final MailSendLogMapper mailSendLogMapper;

    public MailRequest saveSendRecord(MailSimple mailSimple, LocalDateTime startTime, LocalDateTime endTime, boolean success, int size) {
        MailRequest mailRequest = saveMailRequest(mailSimple, startTime, endTime, success);
        saveMailSendLog(mailRequest, startTime, endTime, size);
        return mailRequest;
    }

    private MailRequest saveMailRequest(MailSimple mailSimple, LocalDateTime startTime, LocalDateTime endTime, boolean success) {
        MailRequest mailRequest;
        // 如果是第一次发送，则插入发送请求数据
        if (mailSimple.isNewMail()) {
            mailRequest = buildNewMailRequest(mailSimple, startTime, endTime, success);
            mailRequestMapper.insert(mailRequest);
        } else {
            // 如果是重试操作，则更新数据
            MailRequest mailRequestUpdate = buildUpdateMailRequest(mailSimple, endTime, success);
            mailRequestMapper.updateById(mailRequestUpdate);
            mailRequest = mailRequestMapper.selectById(mailSimple.getId());
        }
        return mailRequest;
    }

    private void saveMailSendLog(MailRequest mailRequest, LocalDateTime startTime, LocalDateTime endTime, int size) {
        MailSendLog mailSendLog = buildMailSendLog(mailRequest, startTime, endTime, size);
        mailSendLogMapper.insert(mailSendLog);
    }

    private MailRequest buildNewMailRequest(MailSimple mailSimple, LocalDateTime startTime, LocalDateTime endTime, boolean success) {
        return MailRequest.builder().businessKey(mailSimple.getBusinessKey())
                .serialNo(mailSimple.getSerialNo())
                .status(getMailStatus(mailSimple, success))
                .subject(mailSimple.getSubject())
                .sendTo(JSON.toJSONString(mailSimple.getTo()))
                .copyTo(JSON.toJSONString(mailSimple.getCc()))
                .receiveTime(startTime)
                .endTime(endTime)
                .retryTimes(mailSimple.getRetryTimes())
                .maxRetryTimes(mailSimple.getMaxRetryTimes())
                .mailJson(JSON.toJSONString(mailSimple)).build();
    }

    private MailRequest buildUpdateMailRequest(MailSimple mailSimple, LocalDateTime endTime, boolean success) {
        return MailRequest.builder().id(mailSimple.getId())
                .status(getMailStatus(mailSimple, success))
                .endTime(endTime)
                .retryTimes(mailSimple.getRetryTimes()).build();
    }

    private MailSendLog buildMailSendLog(MailRequest mailRequest, LocalDateTime startTime, LocalDateTime endTime, int size) {
        return MailSendLog.builder().requestId(mailRequest.getId())
                .retryTimes(mailRequest.getRetryTimes())
                .maxRetryTimes(mailRequest.getMaxRetryTimes())
                .startTime(startTime)
                .endTime(endTime)
                .duration(LocalDateTimeUtil.between(startTime, endTime).getSeconds())
                .size(size)
                .status(mailRequest.getStatus()).build();
    }

    private int getMailStatus(MailSimple mailSimple, boolean success) {
        int status;
        if (success) {
            status = MailConst.SendStatus.SUCCESS;
        } else if (mailSimple.isEnd()) {
            status = MailConst.SendStatus.FAILED;
        } else {
            status = MailConst.SendStatus.WAIT_RETRY;
        }
        return status;
    }

}
