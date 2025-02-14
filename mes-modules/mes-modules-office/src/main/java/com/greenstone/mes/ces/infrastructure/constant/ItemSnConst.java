package com.greenstone.mes.ces.infrastructure.constant;


public interface ItemSnConst {

    String ITEM_SN_TYPE_PREFIX = "item_type_";

    static String getItemSnType(String itemType) {
        return ITEM_SN_TYPE_PREFIX + itemType;
    }

}
