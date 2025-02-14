package com.greenstone.mes.system.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillParam  {

    private Long id;

    private String billType;

    private String paramKey;

    private String paramValue;

}
