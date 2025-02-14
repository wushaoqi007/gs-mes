package com.greenstone.mes.system.domain.entity;

public class ColumnOptions {

    /**
     * 单次操作后是否清除
     */
    private boolean clearable;
    /**
     * 是否进行数据绑定
     */
    private boolean dataBind;

    /**
     * 数据类型校验
     */
    private boolean dataTypeCheck;

    /**
     * 数据类型校验消息
     */
    private String dataTypeMessage;
    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 默认值类型
     */
    private String defaultValueType;

    /**
     * 描述
     */
    private String description;

    /**
     * 禁用
     */
    private boolean disabled;

    /**
     * 隐藏
     */
    private boolean hidden;

    /**
     * 是否打印
     */
    private boolean isPrint;

    /**
     * 是否需要权限
     */
    private boolean permission;

    /**
     * 提示语
     */
    private String placeholder;

    /**
     * 正则校验
     */
    private boolean patternCheck;

    /**
     * 正则
     */
    private String pattern;
    /**
     * 正则校验消息
     */
    private String patternMessage;

    /**
     * 是否必填
     */
    private boolean required;
    /**
     * 必填校验消息
     */
    private String requiredMessage;

    /**
     * 加密显示
     */
    private boolean showPassword;
    /**
     * 宽度
     */
    private String width;
}
