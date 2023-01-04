package cn.createlight.cswitch.config;

import cn.createlight.cswitch.CSwitch;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.LinkedHashMap;

public class ConfigManager {
    public static String pluginConfigFolderPath; //TODO
    public static String roomConfigFolderPath;
    private static LinkedHashMap<String, Config> configs = new LinkedHashMap<>();

    public static void loadConfig(String fileName) {
        if (!new File(pluginConfigFolderPath + "/" + fileName + ".yml").exists()) {
            CSwitch.getInstance().saveResource(fileName + ".yml", false);
        }
        configs.put(fileName, new Config(pluginConfigFolderPath + "/" + fileName + ".yml", Config.YAML));
    }

    public static void addConfig(String fileName, Config config) {
        configs.put(fileName, config);
    }

    public static Config getConfig(String fileName) {
        return configs.getOrDefault(fileName, null);
    }
}

