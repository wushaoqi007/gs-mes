package com.greenstone.mes.material.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * 表格图片
 * @author wushaoqi
 * @date 2022-12-23-8:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelPictureDto {

    private String name;

    /**
     * 图片
     */
    private BufferedImage bufferImg;

    /**
     * 图片流
     */
    private ByteArrayOutputStream byteArrayOut;

    /**
     * 图片高度（像素）
     */
    private int height;

    private int width;

    /**
     * 图片类型
     */
    private String type;
}
