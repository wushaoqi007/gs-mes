package com.greenstone.mes.system.application.dto.cmd;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.infrastructure.enums.NavigationCategory;
import com.greenstone.mes.system.infrastructure.enums.NavigationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NavigationAddCmd {

    private Long id;

    private Long parentId;

    @NotBlank(message = "请填写名称")
    private String name;

    @NotBlank(message = "请选择导航分类")
    private String category;

    private String navigationType;

    private Boolean active;

    private Boolean visible;

    private Boolean cacheable;

    private Boolean openInNewtab;

    private Boolean showNavigation;

    private String icon;

    private Long functionId;

    private String link;

    private String queryParam;


    public void validate() {
        this.validateNavigation();
    }

    private void validateNavigation() {
        NavigationCategory.getNameByType(this.category);
        if (this.category.equals(NavigationCategory.NAVIGATION.getType()) && StrUtil.isBlank(this.navigationType)) {
            throw new ServiceException("导航分类为导航时，导航类型不能为空");
        }
        if (StrUtil.isNotBlank(this.navigationType)) {
            NavigationType.getNameByType(this.navigationType);
            if (this.navigationType.equals(NavigationType.FUNCTION.getType()) && this.functionId == null) {
                throw new ServiceException("导航类型为功能时，功能id不能为空");
            }
            if (this.navigationType.equals(NavigationType.EXTERNAL.getType()) && StrUtil.isBlank(this.link)) {
                throw new ServiceException("导航类型为外链时，外链地址不能为空");
            }
            if (this.navigationType.equals(NavigationType.EXTERNAL.getType()) && this.showNavigation) {
                throw new ServiceException("导航类型为外链时，外链不能选择显示导航");
            }
        }
        // 分类为分组和分类时没有图标
        if ((this.category.equals(NavigationCategory.MODULE.getType()) || this.category.equals(NavigationCategory.NAVIGATION.getType())) && StrUtil.isBlank(this.icon)) {
            throw new ServiceException("图标不能为空");
        }
    }

}
