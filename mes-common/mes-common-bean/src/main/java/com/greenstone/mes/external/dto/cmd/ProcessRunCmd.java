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
public class ProcessRunCmd {

    private String processInstanceId;

    private String serialNo;

    private String comment;

    Map<String, Object> variables;

}
