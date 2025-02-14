package com.greenstone.mes.form.domain;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表单数据实例基类
 *
 * @author gu_renkai
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseFormDataEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private String formId;
    private String dataJson;
    private Long submitById;
    private String submitBy;
    private LocalDateTime submitTime;
    private String appliedByName;
    private LocalDateTime appliedTime;

    private String createBy;
}
