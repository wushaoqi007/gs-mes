package com.greenstone.mes.asset.application.dto.cqe.cmd;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:57
 */
@Data
public class AssetImportCmd {

    @Excel(name = "工号")
    private String employeeNo;

    @Excel(name = "文件号")
    private String fileNumber;

    @Excel(name = "固定资产编号")
    private String barCode;

    @Excel(name = "类型")
    private String typeName;

    @Excel(name = "名称")
    private String name;

    @Excel(name = "配置")
    private String specification;

    @Excel(name = "位置")
    private String location;

    @Excel(name = "备注")
    private String note;

}
