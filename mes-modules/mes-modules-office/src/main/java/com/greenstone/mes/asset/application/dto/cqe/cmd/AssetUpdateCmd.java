package com.greenstone.mes.asset.application.dto.cqe.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:57
 */
@Data
public class AssetUpdateCmd {

    @NotEmpty(message = "请选择资产")
    private String barCode;

    @NotEmpty(message = "请选择资产类型")
    private String typeCode;

    @NotEmpty(message = "请填写资产名称")
    private String name;

    private String sn;

    private String specification;

    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "请选择购入日期")
    private LocalDate purchasedDate;

    private String unit;

    private String location;

    private Long employeeId;

    private String remark;

}
