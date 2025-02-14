package com.greenstone.mes.common.datascope.aspect;

public class DataScopeSwitch {

    private static final ThreadLocal<Boolean> SCOPE_ENABLE_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<Integer> SCOPE_TIMES_LOCAL = new ThreadLocal<>();

    public static void close() {
        SCOPE_ENABLE_LOCAL.set(Boolean.FALSE);
        SCOPE_TIMES_LOCAL.remove();
    }

    public static void open() {
        SCOPE_ENABLE_LOCAL.set(Boolean.TRUE);
    }

    public static void openOnce(boolean pageable) {
        SCOPE_ENABLE_LOCAL.set(Boolean.TRUE);
        if (pageable) {
            SCOPE_TIMES_LOCAL.set(2);
        } else {
            SCOPE_TIMES_LOCAL.set(1);
        }
    }

    public static void openOnce() {
        SCOPE_ENABLE_LOCAL.set(Boolean.TRUE);
        SCOPE_TIMES_LOCAL.set(1);
    }

    protected static boolean isClose() {
        return SCOPE_ENABLE_LOCAL.get() == null || !SCOPE_ENABLE_LOCAL.get();
    }

    protected static void remove() {
        SCOPE_ENABLE_LOCAL.remove();
        SCOPE_TIMES_LOCAL.remove();
    }

    protected static void useOnce() {
        if (SCOPE_TIMES_LOCAL.get() != null) {
            SCOPE_TIMES_LOCAL.set(SCOPE_TIMES_LOCAL.get() - 1);
            if (SCOPE_TIMES_LOCAL.get() < 0) {
                DataScopeSwitch.close();
            }
        }
    }

}
