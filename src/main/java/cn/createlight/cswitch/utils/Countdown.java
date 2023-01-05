package cn.createlight.cswitch.utils;


import cn.createlight.cswitch.config.ConfigManager;
import cn.createlight.cswitch.config.LanguageConfigKey;

public class Countdown {

    private static final String tip = ConfigManager.getConfig(ConfigManager.ConfigName.GAME_TIP).getString(LanguageConfigKey.GAME_PREPARE.toConfigKey());

    public static String countDown(int number) {
        switch (number) {
            case 10:
                return getNumber10();
            case 5:
                return getNumber5();
            case 4:
                return getNumber4();
            case 3:
                return getNumber3();
            case 2:
                return getNumber2();
            case 1:
                return getNumber1();
            default:
                return tip.replace("{TIME}", String.valueOf(number));
        }
    }

    private static String getNumber10() {
        return "§a          ▇  ▇▇▇▇▇▇\n" +
                "§a          ▇  ▇      ▇\n" +
                "§a          ▇  ▇      ▇\n" +
                "§a          ▇  ▇      ▇\n" +
                "§a          ▇  ▇▇▇▇▇▇\n";
    }

    private static String getNumber5() {
        return "§c▇▇▇▇▇▇\n" +
                "§c▇           \n" +
                "§c▇▇▇▇▇▇\n" +
                "§c           ▇\n" +
                "§c▇▇▇▇▇▇\n";
    }

    private static String getNumber4() {
        return "§c▇      ▇\n" +
                "§c▇       ▇\n" +
                "§c▇▇▇▇▇\n" +
                "§c       ▇\n" +
                "§c       ▇\n";
    }

    private static String getNumber3() {
        return "§e▇▇▇▇▇▇\n" +
                "§e           ▇\n" +
                "§e▇▇▇▇▇▇\n" +
                "§e           ▇\n" +
                "§e▇▇▇▇▇▇\n";
    }

    private static String getNumber2() {
        return "§e▇▇▇▇▇▇\n" +
                "§e           ▇\n" +
                "§e▇▇▇▇▇▇\n" +
                "§e▇           \n" +
                "§e▇▇▇▇▇▇\n";
    }

    private static String getNumber1() {
        return "§6          ▇\n" +
                "§6          ▇\n" +
                "§6          ▇\n" +
                "§6          ▇\n" +
                "§6          ▇\n";
    }
}

