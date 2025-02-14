package com.greenstone.mes.material.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialQualityInspectionInStockReq {

    @NotNull(message = "仓库ID不为空")
    private Long warehouseId;

    private String sponsor;

    @Length(max = 200, message = "最大长度200")
    private String remark;

    private Long materialId;

    private String materialCode;

    private String materialVersion;

    /**
     * 该数量为入库数量，如果一批中入库数量为10，NG数量为2.则10个都会一起去NG库。质检结果记录NG数量为2
     */
    @NotNull(message = "material.stock.lack.material.number")
    @Min(value = 1, message = "common.attribute.validation.amount.mix")
    @Max(value = 999999, message = "common.attribute.validation.amount.max")
    private Long number;

    @NotEmpty(message = "material.quality.inspection.projectCode")
    private String projectCode;

    @NotEmpty(message = "material.quality.inspection.partOrderCode")
    private String partOrderCode;

    @NotEmpty(message = "material.quality.inspection.componentCode")
    private String componentCode;

    /**
     * 是否NG
     */
    @NotNull(message = "是否NG不为空")
    private boolean ng;

    /**
     * NG数量
     */
    private Long ngNumber;

    /**
     * NG大类
     */
    private String ngType;

    /**
     * NG小类
     */
    private String ngSubClass;

    /**
     * NG说明备注
     */
    private String explain;

    /**
     * 附件
     */
    private List<String> file;

    private List<MultipartFile> multipartFile;

}
