package cn.createlight.cswitch;

import cn.createlight.cswitch.config.ConfigManager;
import cn.createlight.cswitch.config.LanguageConfigKey;
import cn.createlight.cswitch.room.*;
import cn.createlight.cswitch.utils.MetricsLite;
import cn.createlight.cswitch.utils.StringUtils;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.*;


public class CSwitch extends PluginBase {
    // 注意length为竖轴 width为横轴

    public static final String PLUGIN_NAME = "CSwitch";
    public static final String PLUGIN_NO = "2";
    public static final String PREFIX = "§a=§l§6" + PLUGIN_NAME + "§r§a=";
    public static final String GAME_NAME = "我的游戏机!";

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
            gameTypePrefix.put(gameType, ConfigManager.getConfig(ConfigManager.ConfigName.GAME_TYPE_PREFIX).getString(gameType.toName()));
        }
    }

    @Override
    public void onEnable() {
        long start = new Date().getTime();
        pluginConfigFolderPath = this.getDataFolder().getPath();
        roomConfigFolderPath = pluginConfigFolderPath + "/room/";

        this.getLogger().info(PREFIX + "  §d加载中。。。§e|作者：Champrin");
        this.getLogger().info(PREFIX + "  §e ==> Champrin的第§c" + PLUGIN_NO + "§e款插件/小游戏 " + GAME_NAME + "！");
        this.getServer().getPluginManager().registerEvents(new RoomSetupListeners(), this);
        this.getServer().getPluginManager().registerEvents(new RoomGlobalListeners(), this);
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

        ConfigManager.loadConfig(ConfigManager.ConfigName.COMMAND);
        ConfigManager.loadConfig(ConfigManager.ConfigName.CONFIG);
        ConfigManager.loadConfig(ConfigManager.ConfigName.GAME_RULE);
        ConfigManager.loadConfig(ConfigManager.ConfigName.GAME_TIP);
        ConfigManager.loadConfig(ConfigManager.ConfigName.GAME_TYPE_PREFIX);
        ConfigManager.loadConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP);

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
    private void sendSetupRoomTip(String gameType, CommandSender sender) {
        sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_FIRST_TIP.toConfigKey()));

        List<String> tipList = ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getStringList(gameType);
        for (String tip : tipList) {
            sender.sendMessage(tip);
        }

        sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.SETUP_ROOM_TIP).getString(LanguageConfigKey.SETUP_LAST_TIP.toConfigKey()));
    }


    private String getGameRule(String gameType) {
        return StringUtils.combineStringList(
                ConfigManager.getConfig(ConfigManager.ConfigName.GAME_RULE)
                        .getStringList(gameType)
        );
    }

    private void sendCommandHelp(CommandSender sender) {
        // 生成所有游戏类型的自定义别名组成的字符串
        StringBuilder gameList = new StringBuilder();
        int cnt = 0;
        for (CSwitchGameType gameType : CSwitchGameType.values()) {
            gameList.append("[").append(gameType.ordinal()).append("]").append(":").append(gameTypePrefix.get(gameType)).append(",");
            ++cnt;
            if (cnt % 3 == 0) {
                gameList.append("\n\t");
            }
        }
        // 组合自定义指令提示信息，并发送
        String command = StringUtils.combineStringList(
                ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND)
                        .getStringList(LanguageConfigKey.OP_HELP_COMMAND.toConfigKey())
        );
        sender.sendMessage(command.toString().replace("{GAME_LIST}", gameList.toString()));
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("cs".equals(command.getName())) {
            if (args.length < 1) {
                sendCommandHelp(sender);
            } else {
                switch (args[0]) {
                    case "set":
                        if (sender instanceof Player) {
                            if (args.length < 2) {
                                sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.NOT_ENOUGH_PARAMETER.toConfigKey()));
                                break;
                            }
                            if (!RoomManager.isExistRoomConfig(args[1])) {
                                sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.NOT_EXIST_ROOM.toConfigKey()));
                                break;
                            }
                            Room room = RoomManager.getRoom(args[1]);
                            if (room != null) {
                                if (room.isStarted || room.gamePlayer != null) {
                                    sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.NOT_FREE_ROOM.toConfigKey()));
                                    break;
                                }
                            }
                            LinkedHashMap<String, String> list = new LinkedHashMap<>();
                            list.put("gameName", (String) RoomManager.getRoomData(args[1]).get("game_type"));
                            list.put("room_name", args[1]);
                            list.put("step", String.valueOf(1));
                            RoomSetupListeners.addSetter(sender.getName(), list);
                            sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.SETUP_ROOM.toConfigKey().replace("{ROOM_ID}", args[1])));
                            sendSetupRoomTip((String) RoomManager.getRoomData(args[1]).get("game_type"), sender);

                            RoomManager.removeSigns(args[1]);
                        } else {
                            sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.COMMAND_USE_IN_TERMINAL.toConfigKey()));
                        }
                        break;
                    case "add":
                        if (args.length < 3) {
                            sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.NOT_ENOUGH_PARAMETER.toConfigKey()));
                            break;
                        }
                        if (RoomManager.isExistRoomConfig(args[1])) {
                            sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.EXIST_ROOM.toConfigKey()));
                            break;
                        }
                        if (Integer.parseInt(args[2]) >= CSwitchGameType.values().length) {
                            sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.WRONG_INPUT_SERIAL_NUMBER.toConfigKey()));
                            break;
                        }

                        CSwitchGameType gameType = CSwitchGameType.values()[Integer.parseInt(args[2])];

                        Config roomConfig = new Config(roomConfigFolderPath + args[1] + ".yml", Config.YAML);
                        roomConfig.set(RoomConfigKey.GAME_TYPE.toConfigKey(), gameType.toString());
                        roomConfig.set(RoomConfigKey.SETUP_FINISH.toConfigKey(), false);
                        roomConfig.set(RoomConfigKey.AUTO_BUILD_FRAME.toConfigKey(), true);
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
                        sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.SUCCESSFUL_CREATE_ROOM.toConfigKey()).replace("{ROOM_ID}", args[1]));
                        break;
                    case "del":
                        if (args.length < 2) {
                            sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.NOT_ENOUGH_PARAMETER.toConfigKey()));
                            break;
                        }
                        if (!RoomManager.isExistRoomConfig(args[1])) {
                            sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.NOT_EXIST_ROOM.toConfigKey()));
                            break;
                        }

                        boolean isDeleted = new File(roomConfigFolderPath + args[1] + ".yml").delete();
                        if (isDeleted) {
                            RoomManager.deleteRoom(args[1]);
                            RoomSetupListeners.removeSetter(sender.getName());
                            sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.SUCCESSFUL_DELETE_ROOM.toConfigKey()).replace("{ROOM_ID}", args[1]));
                        } else {
                            sender.sendMessage(ConfigManager.getConfig(ConfigManager.ConfigName.COMMAND).getString(LanguageConfigKey.FAIL_DELETE_ROOM.toConfigKey()).replace("{ROOM_ID}", args[1]));
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

