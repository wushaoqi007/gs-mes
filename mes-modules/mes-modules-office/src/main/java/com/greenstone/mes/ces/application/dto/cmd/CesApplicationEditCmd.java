package com.greenstone.mes.ces.application.dto.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:11
 */

@Data
public class CesApplicationEditCmd {


    private boolean commit;

    private String id;
    @NotNull(message = "请选择申请单")
    private String serialNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "请选择期望到货日期")
    private LocalDate expectReceiveDate;
    private String remark;
    @NotEmpty(message = "请添加申请的物品")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotEmpty(message = "请填写物品名称")
        private String itemName;
        @NotNull(message = "请填写物品数量")
        private Long itemNum;
        private String purchaseLink;
        private String specification;
        private String picturePath;
        private Double unitPrice;
        private Double estimatedCost;
        private Long readyNum;
        @NotEmpty(message = "请填写物品编码")
        private String itemCode;
        private String unit;
        private Long purchasedNum;
        private Long providedNum;
    }

}
