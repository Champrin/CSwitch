package cn.createlight.cswitch.room;

import cn.createlight.cswitch.CSwitch;
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
    private static LinkedHashMap<String, LinkedHashMap<String, Object>> roomsConfig = new LinkedHashMap<>(); // 房间Config
    private static LinkedHashMap<String, Room> rooms = new LinkedHashMap<>(); // id->Room实例
    private static LinkedHashMap<String, ArrayList<BlockEntitySign>> blockSigns = new LinkedHashMap<>(); // 每个房间的木牌（ArrayList[0]：加入游戏木牌 ArrayList[1]：查看游戏规则木牌）

    public static Config getRoomConfigFile(String roomID) {
        return new Config(CSwitch.roomConfigFolderPath + roomID + ".yml", Config.YAML);
    }

    public static LinkedHashMap<String, Object> getRoomConfig(String roomID) {
        return roomsConfig.get(roomID);
    }

    public static void addRoomConfig(String roomID, Map<String, Object> allConfig) {
        roomsConfig.put(roomID, new LinkedHashMap<>(allConfig));
    }

    /**
     * 判断房间的配置文件是否存在
     *
     * @param roomID 房间ID
     * @return 是否存在
     */
    public static boolean isExistRoomConfig(String roomID) {
        return roomsConfig.containsKey(roomID);
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
        }
        removeSigns(roomID);
        roomsConfig.remove(roomID);
    }

    public static void setRoomData(String name) {
        Room game = new Room(name);
        rooms.put(name, game);
        addSigns(name);
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
        LinkedHashMap<String, Object> data = roomsConfig.get(roomID);
        Level level = plugin.getServer().getLevelByName((String) data.get("room_world"));

        String[] p1 = ((String) data.get("button_pos")).split("\\+");
        String[] p2 = ((String) data.get("rule_pos")).split("\\+");

        BlockEntitySign joinSign = getBlockEntitySign(
                level,
                (int) Math.floor(Integer.parseInt(p1[0])),
                (int) Math.floor(Integer.parseInt(p1[1])),
                (int) Math.floor(Integer.parseInt(p1[2])));
        joinSign.setText(
                CSwitch.PREFIX,
                (String) data.get("game_type"),
                "§a点击加入游戏",
                "§f房间ID:" + roomID);

        BlockEntitySign ruleSign = getBlockEntitySign(
                level,
                (int) Math.floor(Integer.parseInt(p2[0])),
                (int) Math.floor(Integer.parseInt(p2[1])),
                (int) Math.floor(Integer.parseInt(p2[2])));
        ruleSign.setText(
                CSwitch.PREFIX,
                (String) data.get("game_type"),
                "§a点击查看游戏介绍",
                "§f房间ID:" + roomID);

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
        BlockEntitySign sign = blockSigns.get(roomID).get(0);
        Room room = rooms.get(roomID);
        if (!room.isStarted) {
            //TODO room.gameType.toString()
            //TODO text内容配置文件化
            sign.setText(
                    CSwitch.PREFIX,
                    room.gameType.toName(),
                    "§a点击加入游戏",
                    "§f房间ID:" + roomID);
        } else {
            sign.setText(
                    CSwitch.PREFIX,
                    room.gameType.toName(),
                    "§f" + room.gamePlayer.getName() + "§a正在游戏",
                    "§f房间ID:" + roomID);
        }
    }
}
