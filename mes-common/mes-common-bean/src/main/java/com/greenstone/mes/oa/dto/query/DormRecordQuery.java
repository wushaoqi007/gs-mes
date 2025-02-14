package com.greenstone.mes.oa.dto.query;

import com.greenstone.mes.oa.enums.DormCityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DormRecordQuery {

    private DormCityType cityType;
    private String dormNo;
    private String employeeName;

}
