package com.greenstone.mes.bom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialNumberDto {

    private String whCode;

    private String whName;

    private Long number;

}
