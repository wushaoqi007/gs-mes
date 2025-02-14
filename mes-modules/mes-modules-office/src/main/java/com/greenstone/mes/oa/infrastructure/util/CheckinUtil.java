package com.greenstone.mes.oa.infrastructure.util;

public class CheckinUtil {

    /**
     * 整取，超越限制向上取整，否则向下取整
     *
     * @param number 数量
     * @param unit   单位数量
     * @param limit  限制数量
     * @return 结果数量
     */
    public static long round(long number, long unit, long limit) {
        long n = number / unit;
        long r = number % unit;
        return r > limit ? unit * (n + 1) : unit * n;
    }

}
