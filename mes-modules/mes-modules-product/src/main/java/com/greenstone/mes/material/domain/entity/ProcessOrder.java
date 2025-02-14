package com.greenstone.mes.material.domain.entity;

import com.greenstone.mes.material.enums.PartBuyReason;
import com.greenstone.mes.material.domain.types.ProcessOrderId;
import com.greenstone.mes.material.domain.types.ProcessPartId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/1 10:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessOrder {

    private ProcessOrderId id;

    @NotEmpty(message = "加工单编码不能为空")
    private String code;

    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @NotNull(message = "公司不能为空")
    private Integer company;

    @NotNull(message = "购买数量不能为空")
    private Integer number;

    @Valid
    @NotEmpty
    private List<ProcessComponent> components;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessComponent {
        private Long id;

        @NotEmpty(message = "组件号不能为空")
        private String code;

        @NotEmpty(message = "组件名称不能为空")
        private String name;

        @Valid
        @NotEmpty
        List<ProcessPart> parts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessPart {
        private ProcessPartId id;

        private Material material;

        @NotNull(message = "零件数量不能为空")
        private Integer number;

        @NotNull(message = "购买原因不能为空")
        private PartBuyReason reason;

        @NotNull(message = "打印日期不能为空")
        private Date printDate;

    }

    public void calcTotalNumber(){
        int partNumber = 0;
        for (ProcessComponent component : components) {
            partNumber += component.getParts().stream().mapToInt(ProcessOrder.ProcessPart::getNumber).sum();
        }
        this.number = partNumber;
    }

}
