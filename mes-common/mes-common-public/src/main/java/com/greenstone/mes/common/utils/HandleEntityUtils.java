package com.greenstone.mes.common.utils;

import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理实体格式的工具类
 *
 * @author wushaoqi
 * @date 2022-08-23-10:35
 */
@Slf4j
public class HandleEntityUtils {
    /**
     * 处理时长
     *
     * @param str 时长字符串，例：0.5小时、1天
     * @return Long(秒)
     */
    public static Long handleLengthStr(String str) {
        Long handledTime = null;
        if (str.contains("天")) {
            handledTime = Double.valueOf(str.substring(0, str.indexOf("天"))).longValue() * 60 * 60 * 24;
        }
        if (str.contains("小时")) {
            handledTime = Double.valueOf(str.substring(0, str.indexOf("小时"))).longValue() * 60 * 60;
        }
        return handledTime;
    }


    /**
     * 处理日期字符串
     * *
     *
     * @param str     字符串，例如：2022/8/1 上午、2022/8/4 17:00等
     * @return 时间戳
     */
    public static Long handleTimeStr(String str) {
        Long handledTime = null;
        try {
            // 正则表达式匹配中文,例如：2022/8/1 上午
            Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher m = p.matcher(str);
            StringBuilder timeStr = new StringBuilder();
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            // 包含中文
            if (m.find()) {
                char[] chars = str.toCharArray();
                for (char aChar : chars) {
                    // 通过正则取出数字或/
                    if ((aChar + "").matches("[0-9]|/")) {
                        timeStr.append(aChar);
                    }
                }
                Date parseDate = format.parse(timeStr.toString());
                handledTime = parseDate.getTime() / 1000;
                return handledTime;
            } else if (str.length() < 11) {
                format = new SimpleDateFormat("yyyy/MM/dd");
            } else if (str.length() < 17) {
                format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            } else {
                format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            }
            Date parseDate = format.parse(str);
            handledTime = parseDate.getTime() / 1000;
        } catch (ParseException e) {
            log.error("时间格式不正确");
            throw new ServiceException("时间格式不正确: " + str);
        }
        return handledTime;
    }

}
