package xyz.caibin.cswitch.untils;

public class Countdown {

    public String countDown(int number) {
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
                return "§6" + number + "\n§e游戏即将开始...";
        }
    }

    private String getNumber10() {
        return  "§a          ▇  ▇▇▇▇▇▇\n" +
                "§a          ▇  ▇      ▇\n" +
                "§a          ▇  ▇      ▇\n" +
                "§a          ▇  ▇      ▇\n" +
                "§a          ▇  ▇▇▇▇▇▇\n";
    }

    private String getNumber5() {
        return  "§c▇▇▇▇▇▇\n" +
                "§c▇           \n" +
                "§c▇▇▇▇▇▇\n" +
                "§c           ▇\n" +
                "§c▇▇▇▇▇▇\n";
    }

    private String getNumber4() {
        return  "§c▇      ▇\n" +
                "§c▇       ▇\n" +
                "§c▇▇▇▇▇\n" +
                "§c       ▇\n" +
                "§c       ▇\n";
    }

    private String getNumber3() {
        return  "§e▇▇▇▇▇▇\n" +
                "§e           ▇\n" +
                "§e▇▇▇▇▇▇\n" +
                "§e           ▇\n" +
                "§e▇▇▇▇▇▇\n";
    }

    private String getNumber2() {
        return  "§e▇▇▇▇▇▇\n" +
                "§e           ▇\n" +
                "§e▇▇▇▇▇▇\n" +
                "§e▇           \n" +
                "§e▇▇▇▇▇▇\n";
    }

    private String getNumber1() {
        return  "§6          ▇\n" +
                "§6          ▇\n" +
                "§6          ▇\n" +
                "§6          ▇\n" +
                "§6          ▇\n";
    }
}
