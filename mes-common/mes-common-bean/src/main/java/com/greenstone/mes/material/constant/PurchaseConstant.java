package com.greenstone.mes.material.constant;

public interface PurchaseConstant {

    /**
     * 采购单状态
     */
    interface PurchaseOrderStatus {
        // 待确认
        String TO_CONFIRM = "1";
        // 待收件
        String CONFIRMED = "2";
        // 收件中
        String RECEIVING = "3";
        // 已收件
        String RECEIVED = "4";
        // 已废弃
        String ABANDON = "5";
        // 变更中
        String CHANGING = "6";
    }

    /**
     * 采购单零件进度
     */
    interface PurchasePartStatus {
        //待确认
        String TO_CONFIRM = "1";
        //待收件
        String TO_RECEIVED = "2";
        //收件中
        String RECEIVING = "3";
        //已收件
        String RECEIVED = "4";
        //废弃
        String ABANDON = "7";
        // 变更中
        String CHANGING = "8";
    }

}
