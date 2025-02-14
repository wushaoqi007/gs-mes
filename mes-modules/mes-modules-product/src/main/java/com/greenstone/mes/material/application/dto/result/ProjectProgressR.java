package com.greenstone.mes.material.application.dto.result;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.greenstone.mes.material.domain.converter.ProjectProgressStepConverter;
import lombok.*;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-26-15:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@ColumnWidth(25)
public class ProjectProgressR {

    @ExcelProperty({"当前项目"})
    private String projectCode;

    @ExcelIgnore
    private List<ProgressStat> statList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @ColumnWidth(25)
    public static class ProgressStat {

        @ExcelProperty(value = {"步骤"}, converter = ProjectProgressStepConverter.class)
        private Integer step;
        @ExcelProperty({"零件数量"})
        private Double partTotal;
        @ExcelProperty({"零件比例"})
        private String partRate;
        @ExcelProperty({"图纸数量"})
        private Double paperTotal;
        @ExcelProperty({"图纸比例"})
        private String paperRate;

    }
}
