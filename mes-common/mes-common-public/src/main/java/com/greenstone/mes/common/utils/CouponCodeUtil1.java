package com.greenstone.mes.common.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CouponCodeUtil1 {

    private static final char[] FIX_STR = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '2', '3', '4', '5', '6', '7', '8', '9'};

    private static final String STR = new String(FIX_STR);

    public static String fFToStr(long i) {
        StringBuilder sb = new StringBuilder();
        //55进制
        while (i >= 55) {
            long a = i % 55;
            i /= 55;
            sb.append(FIX_STR[Math.toIntExact(a)]);
        }
        sb.append(FIX_STR[Math.toIntExact(i)]);
        return sb.reverse().toString();
    }

    public static long strToFF(String str) {
        long r = 0L;
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1, j = 0; i >= 0; i--, j++) {
            r += STR.indexOf(chars[i]) * Math.pow(55, j);
        }
        //todo 此处有坑，会有精度丢失问题，有时候转换出来的long值貌似会和原值不一样。。
        return r;
    }

    /**
     * 产生券码的方法
     *
     * @return
     */
    public static String generateCouponCode() {
        return fFToStr(IdUtil.getSnowflakeNextId());
    }

    public static void main(String[] args) {
        System.out.println("---------------- 分割线 -----------------------");
        Set<String> hashSet = new HashSet<>(10);
        for (int i = 0; i < 10; i++) {
            //产生的券码
            String key = generateCouponCode();
            if (hashSet.contains(key)) {
                System.out.println(key);
                System.out.println("我重复了-------------====================-----------------");
            } else {
                System.out.println(key);
                hashSet.add(key);
            }
        }
        System.out.println("---------------- 分割线 -----------------------");
        /*long nextId = SnowFlakeUtils.nextId();
        System.out.println(nextId);
        //转成55进制后的字符（即我们需要的券码）
        System.out.println(fFToStr(nextId));
        //55进制字符转换成10进制的值 todo 此处有坑，会有精度丢失问题，有时候转换出来的long值貌似会和原值不一样。。
        System.out.println(strToFF(fFToStr(nextId)));
        //理论上说，一个id为20位的10进制数值，用55进制不会超过12位字符。
        System.out.println(Math.pow(10, 20) >= Math.pow(55, 12));*/
    }

}