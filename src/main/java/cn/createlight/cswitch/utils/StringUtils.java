package cn.createlight.cswitch.utils;

import cn.createlight.cswitch.config.ConfigManager;
import cn.createlight.cswitch.config.LanguageConfigKey;

import java.util.List;

public class StringUtils {

    public static String combineStringList(List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringList) {
            stringBuilder.append(str).append("\n");
        }
        return stringBuilder.toString();
    }

}
