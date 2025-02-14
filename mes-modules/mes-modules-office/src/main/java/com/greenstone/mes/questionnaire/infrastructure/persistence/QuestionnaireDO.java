package com.greenstone.mes.questionnaire.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * @author wushaoqi
 * @date 2024-05-29-9:35
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "questionnaire")
public class QuestionnaireDO extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String userName;
    private Long userId;
    private String wxUserId;
    private String email;
    private String dept;
    private String employeeNo;
    private String phone;
    private String dataJson;

}
