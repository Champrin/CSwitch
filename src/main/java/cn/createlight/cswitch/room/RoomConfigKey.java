package cn.createlight.cswitch.room;

public enum RoomConfigKey {
    ROOM_ID,            // 房间ID [String]
    GAME_TYPE,          // 游戏类型 [String] 由CSwitchGameType.toString()而来
    SETUP_STEP,         // 设置房间到达哪一个阶段 [Integer]
    SETUP_FINISH,       // 房间是否完成设置，即代表这个房间是可用于进行游戏的 [Boolean]

    ROOM_WORLD,         // 房间游戏区域所在世界 [String]
    ARENA_POINT1,       // 房间游戏区域位置1，规定为左下角 [String]
    ARENA_POINT2,       // 房间游戏区域位置2，规定为右上角 [String]
    JOIN_POINT,         // 加入房间游戏的木牌位置 [String]
    RULE_POINT,         // 查看房间游戏规则的木牌位置 [String]
    BUILD_FINISH,       // 房间游戏区域是否完成建设 [Boolean]
    AUTO_BUILD_FRAME,   // 是否自动建设房间游戏区域外框架 [Boolean]

    LENGTH,         // 房间游戏区域的高度 [Integer]
    WIDTH,          // 房间游戏区域的宽度 [Integer]
    AREA,           // 房间游戏区域的面积 [Integer]
    DIRECTION,      // 房间游戏区域的方向（朝向） [String] 由Direction.toString()而来

    PREPARE_TIME,   // 房间游戏准备开始倒计时 [Integer]
    GAME_TIME,      // 房间进行游戏的总时间 [Integer]

    ADDITION;       // 附加数据 [List<Object>]

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
        for (RoomConfigKey roomConfig : values()) {
            if (roomConfig.name().equals(value))
                return true;
        }
        return false;
    }
}
