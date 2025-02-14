package com.greenstone.mes.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CouponCodeUtil {


    private static final char[] FIX_STR = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static final String STR = new String(FIX_STR);

    private static String strToSplitHex16(String uuid) {
        StringBuilder shortBuffer = new StringBuilder();
        //我们这里想要保证券码为11位，所以32位uuid加了一位随机数，再分成11等份；（如果是8位券码，则32位uuid分成8等份进行计算即可）
        for (int i = 0; i < 11; i++) {
            String str = uuid.substring(i * 3, i * 3 + 3);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(FIX_STR[x % FIX_STR.length]);
        }
        return shortBuffer.toString();
    }

    private static String strToSplitHex16V2(String uuid) {
        StringBuilder shortBuffer = new StringBuilder();
        //8位券码，则32位uuid分成8等份进行计算即可
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(FIX_STR[x % FIX_STR.length]);
        }
        return shortBuffer.toString();
    }

    /**
     * 产生优惠券编码的方法
     *
     * @return
     */
    public static String generateCouponCode() {
        String uuid = IdUtil.fastSimpleUUID() + new Random().nextInt(10);
        return strToSplitHex16(uuid);
    }

    public static String generateCouponCodeV2() {
        String uuid = IdUtil.fastSimpleUUID();
        return strToSplitHex16V2(uuid);
    }

    public static void main(String[] args) {
        String today = DateUtil.format(new Date(), new SimpleDateFormat("yyMMdd"));
        System.out.println("---------------- 分割线 -----------------------");
        /*基于uuid生成：因为基于雪花id产生的券码，被猜的可能性较大*/
        Set<String> hashSet = new HashSet<>(100);
        for (int i = 0; i < 10000; i++) {
            //产生的券码
            String key = today + "1" + generateCouponCodeV2();
            if (hashSet.contains(key)) {
                System.out.println(key);
                System.out.println("我重复了-------------====================-----------------");
            } else {
                System.out.println(key);
                hashSet.add(key);
            }
        }
        System.out.println("---------------- 分割线 -----------------------");


    }

}
