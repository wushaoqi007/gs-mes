package com.greenstone.mes.oa.dto.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.oa.enums.DormMemberOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DormMemberOperationCmd {
    /**
     * 宿舍编号
     */
    @NotEmpty(message = "请选择宿舍")
    private String dormNo;
    /**
     * 床位号
     */
    @NotNull(message = "请选择床位号")
    private Integer bedNo;
    /**
     * 员工id
     */
    @NotNull(message = "请选择员工")
    private Long employeeId;
    /**
     * 动作
     */
    @NotNull(message = "请选择操作类型")
    private DormMemberOperation operation;
    /**
     * 时间
     */
    @NotNull(message = "请选择时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH")
    private LocalDateTime time;
    /**
     * 本人电话
     */
    private String telephone;
    /**
     * 紧急电话
     */
    private String urgentTel;
}
