package com.greenstone.mes.oa.dto.result;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DormExportResult {
    @Excel(name = "宿舍编号")
    private String dormNo;

    @Excel(name = "房间号")
    private String roomNo;

    @Excel(name = "宿舍地址")
    private String address;

    @Excel(name = "床位号")
    private Integer bedNo;

    @Excel(name = "入住员工")
    private String employeeName;

    @Excel(name = "员工编号")
    private String employeeNo;

    @Excel(name = "所属部门")
    private String deptName;

    @Excel(name = "职务")
    private String duty;

    @Excel(name = "省份")
    private String province;

    @Excel(name = "联系方式")
    private String contact;

    @Excel(name = "紧急联系人电话")
    private String urgentTel;

    @Excel(name = "宿舍类型")
    private String dormType;

    @Excel(name = "入住时间", dateFormat = "yyyy-MM-dd HH")
    private LocalDateTime inTime;

    @Excel(name = "宿管负责人")
    private String manager;

}
