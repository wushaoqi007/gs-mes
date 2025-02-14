package com.greenstone.mes.questionnaire.application.dto.cqe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2024-05-29-9:35
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class QuestionnaireReplyAddCmd {

    @NotEmpty(message = "问卷记录id不为空")
    private String questionnaireId;

    @NotEmpty(message = "回复内容不为空")
    private String content;
}
