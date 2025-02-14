package com.greenstone.mes.external.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/3/2 10:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("flow_process_instance")
public class ProcessInstanceDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 463348033979838306L;

    @TableId
    private String processInstanceId;
    private ProcessStatus processStatus;
    private String serialNo;
    private String formId;
    private String formName;
    private String serviceName;
    private Long appliedBy;
    private String appliedByName;
    private LocalDateTime appliedTime;


}
