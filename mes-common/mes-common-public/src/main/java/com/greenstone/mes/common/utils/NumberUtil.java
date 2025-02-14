package com.greenstone.mes.common.utils;

import java.text.DecimalFormat;

/**
 * @author gu_renkai
 * @date 2023/2/6 11:26
 */

public class NumberUtil {

    private static final DecimalFormat numberFormat = new DecimalFormat("0000");
    private static final DecimalFormat numberFormat2 = new DecimalFormat("00");
    private static final DecimalFormat numberFormat3 = new DecimalFormat("000");

    public static String serialFormat(Long num) {
        if (num < 1000) {
            return numberFormat.format(num);
        } else {
            return String.valueOf(num);
        }
    }

    public static String contractFormat(Long num) {
        if (num < 10) {
            return numberFormat2.format(num);
        } else {
            return String.valueOf(num);
        }
    }

    public static String serialFormat2(Long num) {
        if (num < 100) {
            return numberFormat3.format(num);
        } else {
            return String.valueOf(num);
        }
    }
}
