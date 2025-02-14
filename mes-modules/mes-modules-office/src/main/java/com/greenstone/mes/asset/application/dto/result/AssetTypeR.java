package com.greenstone.mes.asset.application.dto.result;

import lombok.Data;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:31
 */
@Data
public class AssetTypeR {

    private String cateCode;

    private String cateName;

    private List<AssetAttr> attrList;

    @Data
    public static class AssetAttr {

        private String attrCode;

        private String attrName;

        private int source;

        private boolean required;
    }

}
