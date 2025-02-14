package com.greenstone.mes.questionnaire.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-05-29-9:35
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Questionnaire {

    private String id;
    private String userName;
    private Long userId;
    private String wxUserId;
    private String email;
    private String dept;
    private String employeeNo;
    private String phone;
    private String dataJson;

    private List<QuestionnaireReply> replies;

}
