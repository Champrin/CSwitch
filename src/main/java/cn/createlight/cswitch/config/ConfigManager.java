package cn.createlight.cswitch.config;

import cn.createlight.cswitch.CSwitch;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.LinkedHashMap;

public class ConfigManager {
    public enum ConfigName {
        COMMAND,
        CONFIG,
        GAME_RULE,
        GAME_TIP,
        GAME_TYPE_PREFIX,
        SETUP_ROOM_TIP,
        ;

        public String toFileName() {
            return name().toLowerCase().replace("_", "-");
        }
    }

    public static String pluginConfigFolderPath;
    public static String roomConfigFolderPath;
    private static LinkedHashMap<String, Config> configs = new LinkedHashMap<>();

    public static void InitParas(String pluginFolderPath){
        pluginConfigFolderPath = pluginFolderPath;
        roomConfigFolderPath = pluginFolderPath + "/room"; //TODO / /
    }

    public static void loadConfig(ConfigName configName) {
        String fileName = configName.toFileName();
        if (!new File(pluginConfigFolderPath + "/" + fileName + ".yml").exists()) {
            CSwitch.getInstance().saveResource(fileName + ".yml", false);
        }
        configs.put(fileName, new Config(pluginConfigFolderPath + "/" + fileName + ".yml", Config.YAML));
    }

    public static void addConfig(String fileName, Config config) {
        configs.put(fileName, config);
    }

    public static Config getConfig(ConfigName configName) {
        return configs.getOrDefault(configName.toFileName(), null);
    }
}

