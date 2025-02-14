package com.greenstone.mes.table.interfaces.rest.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ItemDelete {

    @NotEmpty(message = "请指定要删除的数据")
    private List<Long> ids;

}