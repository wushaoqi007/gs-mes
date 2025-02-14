package com.greenstone.mes.system.dto.query;

import lombok.*;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2022-10-31-8:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SysCustomTableListReq {

    @NotEmpty(message = "system.custom.table.tableName")
    private String tableName;

}
