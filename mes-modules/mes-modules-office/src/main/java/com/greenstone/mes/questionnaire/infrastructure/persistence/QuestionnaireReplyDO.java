package com.greenstone.mes.questionnaire.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-05-29-9:35
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "questionnaire_reply")
public class QuestionnaireReplyDO extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String questionnaireId;
    private String content;
    private Long replyById;
    private String replyBy;
    private LocalDateTime replyTime;
    @TableLogic
    private Integer deleted;

}
