package com.greenstone.mes.system.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author gu_renkai
 * @date 2023/2/10 8:43
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SerialNoNextCmd {

    @NotEmpty
    private String type;

    @NotEmpty
    private String prefix;

}
