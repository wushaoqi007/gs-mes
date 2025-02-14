package com.greenstone.mes.oa.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DormUpdateCmd {
    @NotEmpty(message = "请填写宿舍编号")
    private String dormNo;
    @NotEmpty(message = "请填写所在城市")
    private String city;
    @NotEmpty(message = "请填写地址")
    private String address;
    @NotEmpty(message = "请填写房间号")
    private String roomNo;
    @Min(value = 1, message = "床位数不能少于1")
    @NotNull(message = "请填写床位数")
    private Integer bedNumber;

    private Long manageBy;
}
