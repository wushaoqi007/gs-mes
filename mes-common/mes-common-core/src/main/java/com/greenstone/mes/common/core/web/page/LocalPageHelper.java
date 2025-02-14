package com.greenstone.mes.common.core.web.page;

/**
 * @author gu_renkai
 * @date 2023/2/13 13:59
 */

public class LocalPageHelper {

    private static final ThreadLocal<LocalPage<?>> LOCAL_PAGES = new ThreadLocal<>();

    public static void setLocalPage(LocalPage<?> page) {
        LOCAL_PAGES.set(page);
    }

    public static LocalPage<?> getLocalPage() {
        return LOCAL_PAGES.get();
    }

    public static void clearLocalPage() {
        LOCAL_PAGES.remove();
    }
}
