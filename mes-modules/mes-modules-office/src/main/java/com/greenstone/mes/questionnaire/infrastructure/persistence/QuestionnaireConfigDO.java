package com.greenstone.mes.questionnaire.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "questionnaire_config")
public class QuestionnaireConfigDO {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String formKey;
    private String fieldName;
    private String alias;
}
