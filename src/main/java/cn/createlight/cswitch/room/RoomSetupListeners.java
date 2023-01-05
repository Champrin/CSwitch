package cn.createlight.cswitch.room;

import cn.createlight.cswitch.config.ConfigManager;
import cn.createlight.cswitch.config.LanguageConfigKey;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.Config;

import java.util.LinkedHashMap;

public class RoomSetupListeners implements Listener {

    private static LinkedHashMap<String, LinkedHashMap<String, String>> setters = new LinkedHashMap<>(); // 房间设置者

    public static void addSetter(String setterName, LinkedHashMap<String, String> data) {
        setters.put(setterName, data);
    }

    public static void removeSetter(String setterName) {
        if (!setters.containsKey(setterName)) return;
        setters.get(setterName).clear();
        setters.remove(setterName);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (setters.containsKey(player.getName())) {
            event.setCancelled(true);

            Block block = event.getBlock();

            String roomName = setters.get(player.getName()).get("room_name");
            Config roomConfig = RoomManager.getRoomConfig(roomName);

            int x = (int) Math.round(Math.floor(block.x));
            int y = (int) Math.round(Math.floor(block.y));
            int z = (int) Math.round(Math.floor(block.z));
            String xyz = x + "," + y + "," + z;

            int step = Integer.parseInt(setters.get(player.getName()).get("step"));

            switch (step) {
                case 1:
                    roomConfig.set(RoomConfigKey.ARENA_POINT1.toConfigKey(), xyz);

                    setters.get(player.getName()).put(RoomConfigKey.ARENA_POINT1.toConfigKey(), xyz);

                    switch (setters.get(player.getName()).get("gameName")) {
                        case "CrazyClick":
                            roomConfig.set(RoomConfigKey.ARENA_POINT2.toConfigKey(), xyz);
                            roomConfig.set(RoomConfigKey.DIRECTION.toConfigKey(), Room.Direction.X_PLUS.toString());
                            roomConfig.set(RoomConfigKey.AREA.toConfigKey(), 1);
                            roomConfig.set(RoomConfigKey.LENGTH.toConfigKey(), 1);
                            roomConfig.set(RoomConfigKey.WIDTH.toConfigKey(), 1);

                            setters.get(player.getName()).put("step", String.valueOf(step + 2));
                            player.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_RULE_POINT.toConfigKey()));
                            break;
                        case "Sudoku":
                            setters.get(player.getName()).put("step", String.valueOf(step + 1));
                            player.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_STEP2_TIP_SUDOKU.toConfigKey()));
                            break;
                        default:
                            setters.get(player.getName()).put("step", String.valueOf(step + 1));
                            player.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_ARENA_POINT2.toConfigKey()));
                            break;
                    }
                    break;
                case 2:
                    roomConfig.set(RoomConfigKey.ARENA_POINT2.toConfigKey(), xyz);

                    String[] pos1 = setters.get(player.getName()).get(RoomConfigKey.ARENA_POINT1.toConfigKey()).split(",");
                    String[] pos2 = xyz.split(",");

                    Room.Direction direction;
                    int width;
                    //TODO 设定左下角为点一 右上角为点二
                    // x+/x- z+/z- 为朝向不同
                    if ("Sudoku".equals(setters.get(player.getName()).get("gameName"))) {
                        if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) < Integer.parseInt(pos2[0])) {
                            direction = Room.Direction.X_PLUS;
                            roomConfig.set(RoomConfigKey.ARENA_POINT1.toConfigKey(), (Integer.parseInt(pos1[0]) - 6) + "," + (Integer.parseInt(pos1[1]) + 12) + "," + Integer.parseInt(pos1[2]));
                            roomConfig.set(RoomConfigKey.ARENA_POINT2.toConfigKey(), (Integer.parseInt(pos1[0]) + 6) + "," + (Integer.parseInt(pos1[1])) + "," + Integer.parseInt(pos1[2]));
                        } else if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) > Integer.parseInt(pos2[0])) {
                            direction = Room.Direction.X_MINUS;
                            roomConfig.set(RoomConfigKey.ARENA_POINT1.toConfigKey(), (Integer.parseInt(pos1[0]) + 6) + "," + (Integer.parseInt(pos1[1]) + 12) + "," + Integer.parseInt(pos1[2]));
                            roomConfig.set(RoomConfigKey.ARENA_POINT2.toConfigKey(), (Integer.parseInt(pos1[0]) - 6) + "," + (Integer.parseInt(pos1[1])) + "," + Integer.parseInt(pos1[2]));
                        } else if (pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) < Integer.parseInt(pos2[2])) {
                            direction = Room.Direction.Z_PLUS;
                            roomConfig.set(RoomConfigKey.ARENA_POINT1.toConfigKey(), Integer.parseInt(pos1[0]) + "," + (Integer.parseInt(pos1[1]) + 12) + "," + (Integer.parseInt(pos1[2]) - 6));
                            roomConfig.set(RoomConfigKey.ARENA_POINT2.toConfigKey(), Integer.parseInt(pos1[0]) + "," + (Integer.parseInt(pos1[1])) + "," + (Integer.parseInt(pos1[2]) + 6));
                        } else { //(pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) > Integer.parseInt(pos2[2]) == true
                            direction = Room.Direction.Z_MINUS;
                            roomConfig.set(RoomConfigKey.ARENA_POINT1.toConfigKey(), Integer.parseInt(pos1[0]) + "," + (Integer.parseInt(pos1[1]) + 12) + "," + (Integer.parseInt(pos1[2]) + 6));
                            roomConfig.set(RoomConfigKey.ARENA_POINT2.toConfigKey(), Integer.parseInt(pos1[0]) + "," + (Integer.parseInt(pos1[1])) + "," + (Integer.parseInt(pos1[2]) - 6));
                        }
                        width = 0;
                    } else {
                        if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) < Integer.parseInt(pos2[0])) {
                            direction = Room.Direction.X_PLUS;
                            width = Integer.parseInt(pos2[0]) - Integer.parseInt(pos1[0]) + 1;
                        } else if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) > Integer.parseInt(pos2[0])) {
                            direction = Room.Direction.X_MINUS;
                            width = Integer.parseInt(pos1[0]) - Integer.parseInt(pos2[0]) + 1;
                        } else if (pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) < Integer.parseInt(pos2[2])) {
                            direction = Room.Direction.Z_PLUS;
                            width = Integer.parseInt(pos2[2]) - Integer.parseInt(pos1[2]) + 1;
                        } else { //(pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) > Integer.parseInt(pos2[2]) == true
                            direction = Room.Direction.Z_MINUS;
                            width = Integer.parseInt(pos1[2]) - Integer.parseInt(pos2[2]) + 1;
                        }
                    }
                    int length = Integer.parseInt(pos2[1]) - Integer.parseInt(pos1[1]) + 1;
                    int area = length * width;
                    roomConfig.set(RoomConfigKey.DIRECTION.toConfigKey(), direction.toString());
                    roomConfig.set(RoomConfigKey.AREA.toConfigKey(), area);
                    roomConfig.set(RoomConfigKey.LENGTH.toConfigKey(), length);
                    roomConfig.set(RoomConfigKey.WIDTH.toConfigKey(), width);

                    setters.get(player.getName()).put("step", String.valueOf(step + 1));
                    player.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_RULE_POINT.toConfigKey()));
                    break;
                case 3:
                    if (block.getId() == BlockID.SIGN_POST || block.getId() == BlockID.WALL_SIGN) {
                        roomConfig.set(RoomConfigKey.RULE_POINT.toConfigKey(), xyz);

                        setters.get(player.getName()).put("step", String.valueOf(step + 1));
                        player.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_JOIN_POINT.toConfigKey()));
                    } else {
                        player.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_BREAK_SIGN.toConfigKey()));
                    }
                    break;
                case 4:
                    if (block.getId() == BlockID.SIGN_POST || block.getId() == BlockID.WALL_SIGN) {
                        roomConfig.set(RoomConfigKey.JOIN_POINT.toConfigKey(), xyz);
                        roomConfig.set(RoomConfigKey.SETUP_FINISH.toConfigKey(), true);
                        roomConfig.set(RoomConfigKey.ROOM_WORLD.toConfigKey(), block.level.getName());

                        RoomManager.addRoomConfig(roomName, roomConfig);
                        RoomManager.addAvailableRoom(roomName);

                        setters.remove(player.getName());
                        player.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_FINISH.toConfigKey()));
                    } else {
                        player.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_BREAK_SIGN.toConfigKey()));
                    }
                    break;
            }
            roomConfig.save();
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onQuit(PlayerQuitEvent event) {
        setters.remove(event.getPlayer().getName());
    }
}
