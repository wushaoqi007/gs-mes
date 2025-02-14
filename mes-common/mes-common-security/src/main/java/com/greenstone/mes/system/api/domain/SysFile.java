package com.greenstone.mes.system.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件信息
 *
 * @author ruoyi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysFile {

    private Long id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件地址
     */
    private String url;

    /**
     * 原始名称
     */
    private String originalName;

}
