package cn.createlight.cswitch;

public enum CSwitchGameType {
    LIGHTS_OUT,         // 关灯
    ONE_TO_ONE,         // 一一对应
    JIGSAW,             // 拼图
    REMOVE_ALL,         // 方块消消乐
    N_PUZZLE,           // 数字华容道
    CRAZY_CLICK,        // 疯狂点击
    AVOID_WHITE_BLOCK,  // 别踩白块
    SUDOKU,             // 数独
    QUICK_REACTION,     // 快速反应
    HANOI_TOWER,        // 汉诺塔
    CARD_MEMORY,        // 记忆翻牌
    THE_2048,           // 2048
    MAKE_A_LINE,        // 宾果消消乐
    GREEDY_SNAKE,       // 贪吃蛇
    TETRIS,             // 俄罗斯方块
    //COLOR_MEMORY,       //颜色记忆
    //BUILD_MEMORY,       //建筑记忆
    ;

    public String toName() {
        return name().replace("_", "-");
    }
}
