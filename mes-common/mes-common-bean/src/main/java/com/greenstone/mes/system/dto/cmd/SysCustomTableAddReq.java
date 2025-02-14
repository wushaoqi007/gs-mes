package com.greenstone.mes.system.dto.cmd;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 自定义列新增
 *
 * @author wushaoqi
 * @date 2022-10-31-8:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SysCustomTableAddReq {

    @NotEmpty(message = "system.custom.table.tableName")
    private String tableName;

    @NotEmpty
    @Valid
    private List<ColumnInfo> columnList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ColumnInfo {

        @NotEmpty(message = "system.custom.table.columnName")
        private String columnName;

        @NotEmpty(message = "system.custom.table.columnNameCn")
        private String columnNameCn;

        private Integer width;

        private Boolean isShow;

        private Boolean isFilter;

        private Boolean isNecessary;

        private Integer sort;
    }
}
