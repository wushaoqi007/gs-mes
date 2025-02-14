package com.greenstone.mes.questionnaire.application.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2024-05-29-9:35
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class QuestionnaireReplyE {

    private String id;
    private String userName;
    private Long userId;
    private String wxUserId;
    private String email;
    private String dept;
    private String employeeNo;
    private String phone;
    private String dataJson;

    private String content;

}
