package net.createlight.champrin.cswitch.untils;

public class Countdown {

    public static String countDown(int number) {
        return switch (number) {
            case 10 -> """
                    §a          ▇  ▇▇▇▇▇▇
                    §a          ▇  ▇      ▇
                    §a          ▇  ▇      ▇
                    §a          ▇  ▇      ▇
                    §a          ▇  ▇▇▇▇▇▇
                    """;
            case 5 -> """
                    §c▇▇▇▇▇▇
                    §c▇          \s
                    §c▇▇▇▇▇▇
                    §c           ▇
                    §c▇▇▇▇▇▇
                    """;
            case 4 -> """
                    §c▇      ▇
                    §c▇       ▇
                    §c▇▇▇▇▇
                    §c       ▇
                    §c       ▇
                    """;
            case 3 -> """
                    §e▇▇▇▇▇▇
                    §e           ▇
                    §e▇▇▇▇▇▇
                    §e           ▇
                    §e▇▇▇▇▇▇
                    """;
            case 2 -> """
                    §e▇▇▇▇▇▇
                    §e           ▇
                    §e▇▇▇▇▇▇
                    §e▇          \s
                    §e▇▇▇▇▇▇
                    """;
            case 1 -> """
                    §6          ▇
                    §6          ▇
                    §6          ▇
                    §6          ▇
                    §6          ▇
                    """;
            default -> "§6" + number + "\n§e游戏即将开始...";
        };
    }
}
