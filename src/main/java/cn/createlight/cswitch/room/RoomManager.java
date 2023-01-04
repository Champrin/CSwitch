package cn.createlight.cswitch.room;

import cn.createlight.cswitch.CSwitch;
import cn.createlight.cswitch.CSwitchGameType;
import cn.createlight.cswitch.config.LanguageConfigKey;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;

import java.util.*;

public class RoomManager {
    public static CSwitch plugin = CSwitch.getInstance();
    private static LinkedHashMap<String, LinkedHashMap<String, Object>> roomsData = new LinkedHashMap<>(); // 房间Data
    private static LinkedHashMap<String, Config> roomsConfig = new LinkedHashMap<>(); // 房间Data
    private static LinkedHashMap<String, Room> rooms = new LinkedHashMap<>(); // id->Room实例
    private static LinkedHashMap<String, ArrayList<BlockEntitySign>> blockSigns = new LinkedHashMap<>(); // 每个房间的木牌（ArrayList[0]：加入游戏木牌 ArrayList[1]：查看游戏规则木牌）

    public static Config getRoomConfigFile(String roomID) {
        return new Config(CSwitch.roomConfigFolderPath + roomID + ".yml", Config.YAML);
    }

    public static LinkedHashMap<String, Object> getRoomData(String roomID) {
        return roomsData.get(roomID);
    }

    public static Config getRoomConfig(String roomID) {
        return roomsConfig.get(roomID);
    }

    public static void addRoomConfig(String roomID, Config config) {
        roomsConfig.put(roomID, config);
        roomsData.put(roomID, new LinkedHashMap<>(config.getAll()));
    }

    /**
     * 判断房间的配置文件是否存在
     *
     * @param roomID 房间ID
     * @return 是否存在
     */
    public static boolean isExistRoomConfig(String roomID) {
        return roomsData.containsKey(roomID);
    }

    /**
     * 获取玩家进入的房间
     *
     * @param player 玩家Player
     * @return 房间Room
     */
    public static Room getPlayerRoom(Player player) {
        for (Map.Entry<String, Room> map : rooms.entrySet()) {
            Room room = map.getValue();
            if (room.gamePlayer == player) {
                return room;
            }
        }
        return null;
    }

    /**
     * 判断房间是否准备好
     *
     * @param roomID 房间ID
     * @return 是否准备好
     */
    public static boolean isReadyRoom(String roomID) {
        return rooms.containsKey(roomID);
    }

    public static Room getRoom(String roomID) {
        return rooms.getOrDefault(roomID, null);
    }

    public static void serverStop() {
        //给每个房间结算结果
        if (!rooms.isEmpty()) {
            for (Map.Entry<String, Room> map : rooms.entrySet()) {
                map.getValue().serverStop();
            }
        }
    }

    public static void deleteRoom(String roomID) {
        if (rooms.containsKey(roomID)) {
            rooms.get(roomID).stopGame();
            rooms.remove(roomID);
            removeSigns(roomID);
        }
        roomsData.remove(roomID);
    }

    public static void addAvailableRoom(String roomID) {
        Room game = new Room(roomID);
        rooms.put(roomID, game);
        addSigns(roomID);
        plugin.getServer().getPluginManager().registerEvents(game, plugin);
    }

    public static Set<Map.Entry<String, ArrayList<BlockEntitySign>>> getBlockSignsEntrySet() {
        return blockSigns.entrySet();
    }

    /**
     * 添加有关房间的所有木牌
     *
     * @param roomID 房间ID
     */
    public static void addSigns(String roomID) {
        LinkedHashMap<String, Object> data = roomsData.get(roomID);
        Level level = plugin.getServer().getLevelByName((String) data.get(RoomConfigKey.ROOM_WORLD.toConfigKey()));

        String[] joinSignPoint = ((String) data.get(RoomConfigKey.JOIN_POINT.toConfigKey())).split(",");
        String[] ruleSignPoint = ((String) data.get(RoomConfigKey.RULE_POINT.toConfigKey())).split(",");

        Config gameTipConfig = CSwitch.gameTipConfig;

        String gameType = CSwitchGameType.valueOf(
                (String) data.get(RoomConfigKey.GAME_TYPE.toConfigKey())
        ).toName();

        BlockEntitySign joinSign = getBlockEntitySign(
                level,
                (int) Math.floor(Integer.parseInt(joinSignPoint[0])),
                (int) Math.floor(Integer.parseInt(joinSignPoint[1])),
                (int) Math.floor(Integer.parseInt(joinSignPoint[2])));
        joinSign.setText(
                gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_FREE_LINE1.toConfigKey())
                        .replace("{PREFIX}", CSwitch.PREFIX)
                        .replace("{GAME_TYPE}", gameType)
                        .replace("{ROOM_ID}", roomID),
                gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_FREE_LINE2.toConfigKey())
                        .replace("{PREFIX}", CSwitch.PREFIX)
                        .replace("{GAME_TYPE}", gameType)
                        .replace("{ROOM_ID}", roomID),
                gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_FREE_LINE3.toConfigKey())
                        .replace("{PREFIX}", CSwitch.PREFIX)
                        .replace("{GAME_TYPE}", gameType)
                        .replace("{ROOM_ID}", roomID),
                gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_FREE_LINE4.toConfigKey())
                        .replace("{PREFIX}", CSwitch.PREFIX)
                        .replace("{GAME_TYPE}", gameType)
                        .replace("{ROOM_ID}", roomID)
        );

        BlockEntitySign ruleSign = getBlockEntitySign(
                level,
                (int) Math.floor(Integer.parseInt(ruleSignPoint[0])),
                (int) Math.floor(Integer.parseInt(ruleSignPoint[1])),
                (int) Math.floor(Integer.parseInt(ruleSignPoint[2])));
        ruleSign.setText(
                gameTipConfig.getString(LanguageConfigKey.SIGN_RULE_LINE1.toConfigKey())
                        .replace("{PREFIX}", CSwitch.PREFIX)
                        .replace("{GAME_TYPE}", gameType)
                        .replace("{ROOM_ID}", roomID),
                gameTipConfig.getString(LanguageConfigKey.SIGN_RULE_LINE2.toConfigKey())
                        .replace("{PREFIX}", CSwitch.PREFIX)
                        .replace("{GAME_TYPE}", gameType)
                        .replace("{ROOM_ID}", roomID),
                gameTipConfig.getString(LanguageConfigKey.SIGN_RULE_LINE3.toConfigKey())
                        .replace("{PREFIX}", CSwitch.PREFIX)
                        .replace("{GAME_TYPE}", gameType)
                        .replace("{ROOM_ID}", roomID),
                gameTipConfig.getString(LanguageConfigKey.SIGN_RULE_LINE4.toConfigKey())
                        .replace("{PREFIX}", CSwitch.PREFIX)
                        .replace("{GAME_TYPE}", gameType)
                        .replace("{ROOM_ID}", roomID)
        );

        blockSigns.put(roomID, new ArrayList<>(Arrays.asList(joinSign, ruleSign)));
    }

    private static BlockEntitySign getBlockEntitySign(Level level, int x, int y, int z) {
        Vector3 v3 = new Vector3(x, y, z);
        BlockEntity blockEntity = level.getBlockEntity(v3);
        BlockEntitySign sign;
        if (blockEntity instanceof BlockEntitySign) {
            sign = (BlockEntitySign) blockEntity;
        } else {
            sign = new BlockEntitySign(level.getChunk(x >> 4, z >> 4), BlockEntity.getDefaultCompound(v3, BlockEntity.SIGN));
        }
        return sign;
    }

    /**
     * 移除有关房间的所有木牌
     *
     * @param roomID 房间ID
     */
    public static void removeSigns(String roomID) {
        if (!blockSigns.containsKey(roomID)) return;
        for (BlockEntitySign sign : blockSigns.get(roomID)) {
            sign.level.setBlock(sign, Block.get(0, 0));
        }
        blockSigns.remove(roomID);
    }

    /**
     * 改变木牌的显示：是否有玩家在游戏的状态
     *
     * @param roomID 房间ID
     */
    public static void changeSignText(String roomID) {
        Room room = rooms.get(roomID);
        Config gameTipConfig = CSwitch.gameTipConfig;

        String gameType = room.gameType.toName();

        BlockEntitySign sign = blockSigns.get(roomID).get(0);
        if (!room.isStarted) {
            sign.setText(
                    gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_FREE_LINE1.toConfigKey())
                            .replace("{PREFIX}", CSwitch.PREFIX)
                            .replace("{GAME_TYPE}", gameType)
                            .replace("{ROOM_ID}", roomID),
                    gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_FREE_LINE2.toConfigKey())
                            .replace("{PREFIX}", CSwitch.PREFIX)
                            .replace("{GAME_TYPE}", gameType)
                            .replace("{ROOM_ID}", roomID),
                    gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_FREE_LINE3.toConfigKey())
                            .replace("{PREFIX}", CSwitch.PREFIX)
                            .replace("{GAME_TYPE}", gameType)
                            .replace("{ROOM_ID}", roomID),
                    gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_FREE_LINE4.toConfigKey())
                            .replace("{PREFIX}", CSwitch.PREFIX)
                            .replace("{GAME_TYPE}", gameType)
                            .replace("{ROOM_ID}", roomID)
            );
        } else {
            sign.setText(
                    gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_NOT_FREE_LINE1.toConfigKey())
                            .replace("{PREFIX}", CSwitch.PREFIX)
                            .replace("{GAME_TYPE}", gameType)
                            .replace("{PLAYER}", room.gamePlayer.getName())
                            .replace("{ROOM_ID}", roomID),
                    gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_NOT_FREE_LINE2.toConfigKey())
                            .replace("{PREFIX}", CSwitch.PREFIX)
                            .replace("{GAME_TYPE}", gameType)
                            .replace("{PLAYER}", room.gamePlayer.getName())
                            .replace("{ROOM_ID}", roomID),
                    gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_NOT_FREE_LINE3.toConfigKey())
                            .replace("{PREFIX}", CSwitch.PREFIX)
                            .replace("{GAME_TYPE}", gameType)
                            .replace("{PLAYER}", room.gamePlayer.getName())
                            .replace("{ROOM_ID}", roomID),
                    gameTipConfig.getString(LanguageConfigKey.SIGN_JOIN_NOT_FREE_LINE4.toConfigKey())
                            .replace("{PREFIX}", CSwitch.PREFIX)
                            .replace("{GAME_TYPE}", gameType)
                            .replace("{PLAYER}", room.gamePlayer.getName())
                            .replace("{ROOM_ID}", roomID)
            );
        }
    }
}
