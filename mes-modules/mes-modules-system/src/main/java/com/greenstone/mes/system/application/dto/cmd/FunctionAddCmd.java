package com.greenstone.mes.system.application.dto.cmd;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.infrastructure.enums.FunctionSourceType;
import com.greenstone.mes.system.infrastructure.enums.FunctionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FunctionAddCmd {

    private Long id;

    @NotBlank(message = "请填写功能名称")
    private String name;

    @NotBlank(message = "请选择功能类型")
    private String type;

    @NotBlank(message = "请选择来源类型")
    private String source;

    private String component;

    private String formComponent;

    private Boolean usingProcess;

    private String templateId;


    public void validate() {
        this.validateFunction();
    }

    private void validateFunction() {
        FunctionType.getNameByType(this.type);
        FunctionSourceType.getNameByType(this.source);
        if (this.source.equals(FunctionSourceType.PREDEFINE.getType()) && StrUtil.isBlank(this.component)) {
            throw new ServiceException("预定义的组件不能为空");
        }
        if (this.type.equals(FunctionType.TABLE.getType()) && this.source.equals(FunctionSourceType.PREDEFINE.getType()) && StrUtil.isBlank(this.formComponent)) {
            throw new ServiceException("表格的的预定义表单组件不能为空");
        }
        if (usingProcess && StrUtil.isBlank(this.templateId)) {
            throw new ServiceException("启用流程模板id不能为空");
        }
    }

}
