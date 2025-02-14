package com.greenstone.mes.common.datascope;

/**
 * @author gu_renkai
 * @date 2023/2/13 13:59
 */

public class DynamicTableName {

    private static final ThreadLocal<String> LOCAL_TABLE_NAME = new ThreadLocal<>();

    public static void setTableName(String page) {
        LOCAL_TABLE_NAME.set(page);
    }

    public static String getTableName() {
        return LOCAL_TABLE_NAME.get();
    }

    public static void clearTableName() {
        LOCAL_TABLE_NAME.remove();
    }
}
