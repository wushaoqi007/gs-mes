package com.greenstone.mes.system.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableFieldVo {
    private String label;

    private String value;

    private int source;

    private boolean show;
}
