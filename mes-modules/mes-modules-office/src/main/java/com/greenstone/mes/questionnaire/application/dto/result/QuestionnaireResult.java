package com.greenstone.mes.questionnaire.application.dto.result;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class QuestionnaireResult {

    @Excel(name = "序号")
    private Integer index;
    @Excel(name = "投稿人")
    private String userName;
    @Excel(name = "现状：请描述在您的日常工作中发现的问题或者异常？")
    private String situation;
    @Excel(name = "合理化建议")
    private String suggest;
    @Excel(name = "跟踪反馈意见")
    private String reply;
    @Excel(name = "落实情况")
    private String result;
    @Excel(name = "情况备注")
    private String remark;
    @Excel(name = "建议奖励")
    private String suggestReward;
    @Excel(name = "改善奖金")
    private String improveBonus;


}
