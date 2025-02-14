package com.greenstone.mes.questionnaire.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-05-29-9:35
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class QuestionnaireReply {

    private String id;
    private String questionnaireId;
    private String content;
    private Long replyById;
    private String replyBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime replyTime;
}
