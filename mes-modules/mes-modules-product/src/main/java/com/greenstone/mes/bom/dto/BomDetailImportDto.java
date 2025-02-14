package com.greenstone.mes.bom.dto;

import com.greenstone.mes.bom.enums.DetailAddStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BomDetailImportDto {

    private DetailAddStrategy addStrategy;

    private String code;

    private String version;

    private Integer paperNumber;

}
