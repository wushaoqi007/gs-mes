package com.greenstone.mes.mail.domain.helper;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PinyinHelper {

    private int getEmployeeNumber(String employeeNo) {
        return Integer.parseInt(employeeNo.substring(1));
    }

    public static String getNamePinyin(String name) {
        if (name.length() < 2) {
            throw new RuntimeException("姓名至少包含2个汉字");
        }
        StringBuilder namePinyin = new StringBuilder();
        int firstNameIndex = 0;
        if (SURNAME_PINYIN_MAP.containsKey(name.substring(0, 2))) {
            if (name.length() < 3) {
                throw new RuntimeException("复姓不能只有姓");
            }
            firstNameIndex = 2;
            namePinyin.append(SURNAME_PINYIN_MAP.get(name.substring(0, 2)));
        } else if (SURNAME_PINYIN_MAP.containsKey(name.substring(0, 1))) {
            firstNameIndex = 1;
            namePinyin.append(SURNAME_PINYIN_MAP.get(name.substring(0, 1)));
        }
        for (int i = firstNameIndex; i < name.length(); i++) {
            namePinyin.append(getPinyin(name.charAt(i))[0]);
        }
        return namePinyin.toString();
    }

    private static String[] getPinyin(char ch) {
        try {
            if (ch >= '\u4e00' && ch <= '\u9fff') {
                return net.sourceforge.pinyin4j.PinyinHelper.toHanyuPinyinStringArray(ch, outputFormat);
            } else {
                return new String[]{String.valueOf(ch)};
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            throw new IllegalStateException(e);
        }
    }


    private static final HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();

    private static final Map<String, String> SURNAME_PINYIN_MAP = new HashMap<>();

    static {
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);


        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("pinyin.properties")) {
            Properties properties = new Properties();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties.load(inputStreamReader);
            inputStreamReader.close();
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                SURNAME_PINYIN_MAP.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
