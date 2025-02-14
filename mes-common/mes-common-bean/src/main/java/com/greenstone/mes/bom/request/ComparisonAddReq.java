package com.greenstone.mes.bom.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ComparisonAddReq {

    @NotNull(message = "导入记录不能为空")
    private Long importRecordId;

    @NotEmpty(message = "比对结果不能为空")
    @Valid
    private List<ComparisonAddReq.Result> results;

    @Data
    public static class Result {

        @NotNull(message = "导入明细不能为空")
        private Long importDetailId;

        @NotEmpty(message = "比对结果不能为空")
        private String result;

        @NotNull(message = "扫描数量不能为空")
        private Integer scanNumber;
    }

}
