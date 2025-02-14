package com.greenstone.mes.external.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2023/2/24 15:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessStartCmd {

    @NotEmpty(message = "表单id不能为空")
    private String formId;

    @NotEmpty(message = "请指定服务名称")
    private String serviceName;

    @NotEmpty(message = "表单名称不能为空")
    private String formName;

    @NotEmpty(message = "单据编号不能为空")
    private String serialNo;

    private String comment;

    private List<String> assigneeList;

    private Map<String, Object> variables;

}
