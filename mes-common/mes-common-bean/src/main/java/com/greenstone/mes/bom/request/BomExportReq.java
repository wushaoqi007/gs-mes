package com.greenstone.mes.bom.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BomExportReq {

    @NotNull(message = "组件信息不能为空")
    private Long componentId;

}
