package cn.createlight.cswitch;

import cn.createlight.cswitch.config.ConfigManager;
import cn.createlight.cswitch.config.LanguageConfigKey;
import cn.createlight.cswitch.room.RoomConfigKey;
import cn.createlight.cswitch.untils.MetricsLite;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.createlight.cswitch.room.Room;
import cn.createlight.cswitch.room.RoomManager;

import java.io.File;
import java.util.*;


public class CSwitch extends PluginBase implements Listener {
    //注意length为竖轴 width为横轴
/*
TODO
    1.C2048
    *颜色记忆
    按照四种颜色闪光的顺序重复点击一遍，尽量做到无错误
    *建筑记忆
*/
    public static Config setupRoomTipConfig, gameTipConfig, gameRuleConfig, commandConfig, gameTypePrefixConfig;
    public static final String PLUGIN_NAME = "CSwitch";
    public static final String PLUGIN_No = "2";
    public static final String PREFIX = "§a=§l§6" + PLUGIN_NAME + "§r§a=";
    public static final String GAME_NAME = "我的游戏机!";
    public LinkedHashMap<String, LinkedHashMap<String, String>> setters = new LinkedHashMap<>(); // 房间设置者

    public static LinkedHashMap<CSwitchGameType, String> gameTypePrefix = new LinkedHashMap<>();

    public static String pluginConfigFolderPath;
    public static String roomConfigFolderPath;

    private static CSwitch instance;

    public static CSwitch getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;

        for (CSwitchGameType gameType : CSwitchGameType.values()) {
            gameTypePrefix.put(gameType, gameTypePrefixConfig.getString(gameType.toName()));
        }
    }

    @Override
    public void onEnable() {
        long start = new Date().getTime();
        pluginConfigFolderPath = this.getDataFolder().getPath();
        roomConfigFolderPath = pluginConfigFolderPath + "/room/";

        this.getLogger().info(PREFIX + "  §d加载中。。。§e|作者：Champrin");
        this.getLogger().info(PREFIX + "  §e ==> Champrin的第§c" + PLUGIN_No + "§e款插件/小游戏 " + GAME_NAME + "！");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.LoadPluginConfig();
        this.LoadRoomConfig();
        new MetricsLite(this, 6865);
        this.getLogger().info(PREFIX + "  §d已加载完毕。。。");
        this.getLogger().info(PREFIX + "  §e加载耗时" + (new Date().getTime() - start) + "毫秒");
    }

    @Override
    public void onDisable() {
        RoomManager.serverStop();
    }

    public void LoadPluginConfig() {
        this.getLogger().info("-插件配置文件加载中...");

        ConfigManager.loadConfig("setupRoomTip");
        ConfigManager.loadConfig("command");
        ConfigManager.loadConfig("gameTip");
        ConfigManager.loadConfig("gameRule");
        ConfigManager.loadConfig("gameTypePrefix");

        this.getLogger().info("-插件配置文件加载完毕。");
    }

    public void LoadRoomConfig() {
        this.getLogger().info("-房间信息加载中...");
        // 加载房间配置文件所在文件夹
        File folder = new File(roomConfigFolderPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                this.getServer().getLogger().info("文件夹创建失败");
            }
        }
        // 加载所有的房间配置文件
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Config roomConfig = new Config(file, Config.YAML);
                    String roomID = roomConfig.getString(RoomConfigKey.ROOM_ID.toConfigKey());
                    RoomManager.addRoomConfig(roomID, roomConfig);
                    if (roomConfig.getBoolean(RoomConfigKey.SETUP_FINISH.toConfigKey())) {
                        RoomManager.addAvailableRoom(roomID);
                        this.getLogger().info("   房间§b" + roomID + "§r准备完成");
                    }
                }
            }
        }
        this.getLogger().info("-房间信息加载完毕。");
    }

    /**
     * 发送设置房间时的提示信息
     *
     * @param gameType 游戏类型
     * @param sender   接收提示信息者
     */
    public void sendSetupRoomTip(String gameType, CommandSender sender) {
        sender.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_FIRST_TIP.toConfigKey()));

        List<String> tipList = setupRoomTipConfig.getStringList(gameType);
        for (String tip : tipList) {
            sender.sendMessage(tip);
        }

        sender.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_LAST_TIP.toConfigKey()));
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
                            player.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_RULE_POINT.toConfigKey()));
                            break;
                        case "Sudoku":
                            setters.get(player.getName()).put("step", String.valueOf(step + 1));
                            player.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_STEP2_TIP_SUDOKU.toConfigKey()));
                            break;
                        default:
                            setters.get(player.getName()).put("step", String.valueOf(step + 1));
                            player.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_ARENA_POINT2.toConfigKey()));
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
                    player.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_RULE_POINT.toConfigKey()));
                    break;
                case 3:
                    if (block.getId() == BlockID.SIGN_POST || block.getId() == BlockID.WALL_SIGN) {
                        roomConfig.set(RoomConfigKey.RULE_POINT.toConfigKey(), xyz);

                        setters.get(player.getName()).put("step", String.valueOf(step + 1));
                        player.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_JOIN_POINT.toConfigKey()));
                    } else {
                        player.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_BREAK_SIGN.toConfigKey()));
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
                        player.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_FINISH.toConfigKey()));
                    } else {
                        player.sendMessage(setupRoomTipConfig.getString(LanguageConfigKey.SETUP_BREAK_SIGN.toConfigKey()));
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

    @EventHandler
    @SuppressWarnings("unused")
    public void onChat(PlayerChatEvent event) {
        //TODO 换一种方式
        Player player = event.getPlayer();
        Room room = RoomManager.getPlayerRoom(player);
        if (room == null) return;
        if (event.getMessage().contains("@hub")) {
            event.setCancelled(true);
            player.sendMessage(">  你已退出游戏！");
            room.stopGame();
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onTouch(PlayerInteractEvent event) {
        Block block = event.getBlock();
        if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
            BlockEntity sign = event.getBlock().level.getBlockEntity(block);
            if (sign instanceof BlockEntitySign) {
                Player player = event.getPlayer();
                //TODO 重新写逻辑
                if (((BlockEntitySign) sign).getText()[2].equals("§a点击加入游戏")) {
                    event.setCancelled(true);
                    int x = (int) Math.round(Math.floor(block.x));
                    int y = (int) Math.round(Math.floor(block.y));
                    int z = (int) Math.round(Math.floor(block.z));

                    for (Map.Entry<String, ArrayList<BlockEntitySign>> map : RoomManager.getBlockSignsEntrySet()) {
                        if (map.getValue().get(0) == sign) {
                            Room room = RoomManager.getRoom(map.getKey());
                            if (room != null) {
                                room.joinToRoom(player);
                            }
                            break;
                        }
                    }
                } else if (((BlockEntitySign) sign).getText()[2].equals("§a点击查看游戏介绍")) {
                    event.setCancelled(true);
                    FormWindowSimple window = new FormWindowSimple(
                            gameRuleConfig.getString(LanguageConfigKey.RULE_FORM_WINDOW_TITLE.toConfigKey()),
                            gameRuleConfig.getString(LanguageConfigKey.RULE_FORM_WINDOW_CONTENT.toConfigKey())
                    );
                    player.showFormWindow(window);
                }
            }
        }
    }

    private String getGameRule(String gameType) {
        StringBuilder gameRule = new StringBuilder();
        List<String> tipList = gameRuleConfig.getStringList(gameType);
        for (String tip : tipList) {
            gameRule.append(tip).append("\n");
        }

        return gameRule.toString();
    }

    public void sendCommandHelp(CommandSender sender) {
        // 生成所有游戏类型的自定义别名组成的字符串
        StringBuilder gameList = new StringBuilder();
        int cnt = 0;
        for (CSwitchGameType gameType : CSwitchGameType.values()) {
            gameTypePrefix.put(gameType, gameTypePrefixConfig.getString(gameType.toName()));
            gameList.append("[").append(gameType.ordinal()).append("]").append(":").append(gameTypePrefix.get(gameType)).append(",");
            ++cnt;
            if (cnt % 3 == 0) {
                gameList.append("\n\t");
            }
        }
        // 组合自定义指令提示信息，并发送
        List<String> tipList = commandConfig.getStringList(LanguageConfigKey.OP_HELP_COMMAND.toConfigKey());
        StringBuilder commandHelp = new StringBuilder();
        for (String tip : tipList) {
            commandHelp.append(tip).append("\n");
        }
        sender.sendMessage(commandHelp.toString().replace("{GAME_LIST}", gameList.toString()));
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("cs".equals(command.getName())) {
            if (args.length < 1) {
                this.sendCommandHelp(sender);
            } else {
                switch (args[0]) {
                    case "set":
                        if (sender instanceof Player) {
                            if (args.length < 2) {
                                sender.sendMessage(commandConfig.getString(LanguageConfigKey.NOT_ENOUGH_PARAMETER.toConfigKey()));
                                break;
                            }
                            if (!RoomManager.isExistRoomConfig(args[1])) {
                                sender.sendMessage(commandConfig.getString(LanguageConfigKey.NOT_EXIST_ROOM.toConfigKey()));
                                break;
                            }
                            Room room = RoomManager.getRoom(args[1]);
                            if (room != null) {
                                if (room.isStarted || room.gamePlayer != null) {
                                    sender.sendMessage(commandConfig.getString(LanguageConfigKey.NOT_FREE_ROOM.toConfigKey()));
                                    break;
                                }
                            }
                            LinkedHashMap<String, String> list = new LinkedHashMap<>();
                            list.put("gameName", (String) RoomManager.getRoomData(args[1]).get("game_type"));
                            list.put("room_name", args[1]);
                            list.put("step", String.valueOf(1));
                            setters.put(sender.getName(), list);
                            sender.sendMessage(commandConfig.getString(LanguageConfigKey.SETUP_ROOM.toConfigKey().replace("{ROOM_ID}", args[1])));
                            this.sendSetupRoomTip((String) RoomManager.getRoomData(args[1]).get("game_type"), sender);

                            RoomManager.removeSigns(args[1]);
                        } else {
                            sender.sendMessage(commandConfig.getString(LanguageConfigKey.COMMAND_USE_IN_TERMINAL.toConfigKey()));
                        }
                        break;
                    case "add":
                        if (args.length < 3) {
                            sender.sendMessage(commandConfig.getString(LanguageConfigKey.NOT_ENOUGH_PARAMETER.toConfigKey()));
                            break;
                        }
                        if (RoomManager.isExistRoomConfig(args[1])) {
                            sender.sendMessage(commandConfig.getString(LanguageConfigKey.EXIST_ROOM.toConfigKey()));
                            break;
                        }
                        if (Integer.parseInt(args[2]) >= CSwitchGameType.values().length) {
                            sender.sendMessage(commandConfig.getString(LanguageConfigKey.WRONG_INPUT_SERIAL_NUMBER.toConfigKey()));
                            break;
                        }

                        CSwitchGameType gameType = CSwitchGameType.values()[Integer.parseInt(args[2])];

                        Config roomConfig = new Config(roomConfigFolderPath + args[1] + ".yml", Config.YAML);
                        roomConfig.set(RoomConfigKey.GAME_TYPE.toConfigKey(), gameType.toString());
                        roomConfig.set(RoomConfigKey.SETUP_FINISH.toConfigKey(), false);
                        roomConfig.set(RoomConfigKey.PREPARE_TIME.toConfigKey(), 5);

                        switch (gameType) {
                            case AVOID_WHITE_BLOCK:
                                roomConfig.set(RoomConfigKey.ADDITION.toConfigKey(), Collections.singletonList(15));
                                break;
                            case CRAZY_CLICK:
                                roomConfig.set(RoomConfigKey.GAME_TIME.toConfigKey(), 20);
                                break;
                            case QUICK_REACTION:
                                roomConfig.set(RoomConfigKey.GAME_TIME.toConfigKey(), 60);
                                break;
                        }
                        roomConfig.save();

                        RoomManager.addRoomConfig(args[1], roomConfig);
                        sender.sendMessage(commandConfig.getString(LanguageConfigKey.SUCCESSFUL_CREATE_ROOM.toConfigKey()).replace("{ROOM_ID}", args[1]));
                        break;
                    case "del":
                        if (args.length < 2) {
                            sender.sendMessage(commandConfig.getString(LanguageConfigKey.NOT_ENOUGH_PARAMETER.toConfigKey()));
                            break;
                        }
                        if (!RoomManager.isExistRoomConfig(args[1])) {
                            sender.sendMessage(commandConfig.getString(LanguageConfigKey.NOT_EXIST_ROOM.toConfigKey()));
                            break;
                        }

                        boolean isDeleted = new File(roomConfigFolderPath + args[1] + ".yml").delete();
                        if (isDeleted) {
                            RoomManager.deleteRoom(args[1]);
                            this.setters.remove(sender.getName());
                            sender.sendMessage(commandConfig.getString(LanguageConfigKey.SUCCESSFUL_DELETE_ROOM.toConfigKey()).replace("{ROOM_ID}", args[1]));
                        } else {
                            sender.sendMessage(commandConfig.getString(LanguageConfigKey.FAIL_DELETE_ROOM.toConfigKey()).replace("{ROOM_ID}", args[1]));
                        }
                        break;
                    case "help":
                    default:
                        this.sendCommandHelp(sender);
                        break;
                }
            }
        }
        return true;
    }
}

