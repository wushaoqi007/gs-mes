package com.greenstone.mes.material.application.dto.result;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-04-03-11:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorksheetCheckCountR {
    @Excel(name = "姓名")
    private String inspectors;
    @Excel(name = "日期")
    private String checkTime;
    @Excel(name = "零件数量")
    private Integer partNum;
    @Excel(name = "图纸数量")
    private Integer paperNum;

}
