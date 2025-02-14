package com.greenstone.mes.system.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gu_renkai
 * @date 2023/3/3 10:02
 */
@Getter
@AllArgsConstructor
public enum MenuType {
    MODULE(1, "模块"),
    MENU(2, "菜单"),
    DIRECTORY(3, "文件夹"),
    GROUP(4, "分组"),
    TAB(5, "标签"),
    ;
    @EnumValue
    private int type;

    private String name;


}
