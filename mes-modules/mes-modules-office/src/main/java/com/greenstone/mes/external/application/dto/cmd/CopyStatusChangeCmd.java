package com.greenstone.mes.external.application.dto.cmd;

import com.greenstone.mes.external.infrastructure.enums.CopyHandleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyStatusChangeCmd {

    @NotEmpty
    private List<String> copyIds;

    @NotNull
    private CopyHandleStatus handleStatus;

}
