package net.createlight.champrin.cswitch.room;

public enum RoomConfig {
    ROOM_NAME,
    GAME_TYPE,
    SETUP_STEP,
    STATE,
    ARENA,
    ROOM_WORLD,
    START_TIME,
    GAME_TIME,

    TIMES,

    POINT1,
    POINT2,
    DIRECTION,
    AREA,

    RULE_POS,
    BUTTON_POS,

    LENGTH,
    WIDTH;

    /**
     * 将枚举元素的命名转为字符串，
     * 并转为小写，替换“_”，以便到配置文件中获取数据
     *
     * @return 转为配置文件中的key值
     */
    public String toConfigKey() {
        return name().toLowerCase().replace("_", "-");
    }

    public static boolean contains(String value) {
        for (RoomConfig roomConfig : values()) {
            if (roomConfig.name().equals(value))
                return true;
        }
        return false;
    }
}
