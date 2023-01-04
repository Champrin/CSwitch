package cn.createlight.cswitch;

import cn.createlight.cswitch.untils.MetricsLite;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
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
    public Config config, language;
    public static final String PLUGIN_NAME = "CSwitch";
    public static final String PLUGIN_No = "2";
    public static final String PREFIX = "§a=§l§6" + PLUGIN_NAME + "§r§a=";
    public static final String GAME_NAME = "我的游戏机!";
    public LinkedHashMap<String, LinkedHashMap<String, String>> setters = new LinkedHashMap<>();//房间设置者

    public LinkedHashMap<Integer, String> Game = new LinkedHashMap<>();

    public static String pluginConfigFolderPath;
    public static String roomConfigFolderPath;

    private static CSwitch instance;

    public static CSwitch getInstance() {
        return instance;
    }

    public enum GameType {
        LIGHTS_OUT, // 关灯
        ONE_TO_ONE, // 一一对应
        JIGSAW, // 拼图
        REMOVE_ALL, // 方块消消乐
        N_PUZZLE, // 数字华容道
        CRAZY_CLICK, // 疯狂点击
        AVOID_WHITE_BLOCK, // 别踩白块
        SUDOKU, // 数独
        QUICK_REACTION, // 快速反应
        HANOI_TOWER, // 汉诺塔
        CARD_MEMORY, // 记忆翻牌
        THE_2048, // 2048
        MAKE_A_LINE, // 宾果消消乐
        GREEDY_SNAKE, // 贪吃蛇
        TETRIS; // 俄罗斯方块

        public String toName() {
            return name().replace("_", " ");
        }
    }

    @Override
    public void onLoad() {
        instance = this;
        pluginConfigFolderPath = this.getDataFolder().getPath();
        roomConfigFolderPath = this.getDataFolder().getPath() + "/room/";
    }

    @Override
    public void onEnable() {
        long start = new Date().getTime();
        this.getLogger().info(PREFIX + "  §d加载中。。。§e|作者：Champrin");
        this.getLogger().info(PREFIX + "  §e ==> Champrin的第§c" + PLUGIN_No + "§e款插件/小游戏 " + GAME_NAME + "！");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.LoadConfig();
        this.LoadRoomConfig();
        new MetricsLite(this, 6865);
        this.getLogger().info(PREFIX + "  §d已加载完毕。。。");
        this.getLogger().info(PREFIX + "  §e加载耗时" + (new Date().getTime() - start) + "毫秒");
    }

    @Override
    public void onDisable() {
        RoomManager.serverStop();
    }

    public void LoadConfig() {
        this.getLogger().info("-配置文件加载中...");

        File file = new File(roomConfigFolderPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                this.getServer().getLogger().info("文件夹创建失败");
            }
        }
    }

    public void LoadRoomConfig() {
        this.getLogger().info("-房间信息加载中...");
        File file = new File(roomConfigFolderPath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File FILE : files) {
                if (FILE.isFile()) {
                    Config room = new Config(FILE, Config.YAML);
                    String FileName = FILE.getName().substring(0, FILE.getName().lastIndexOf("."));
                    RoomManager.addRoomConfig(FileName, new LinkedHashMap<>(room.getAll()));
                    if ("true".equals(room.get("state"))) {
                        RoomManager.setRoomData(FileName);
                        this.getLogger().info("   房间§b" + FileName + "§r加载完成");
                    }

                }
            }
        }
        this.getLogger().info("-房间信息加载完毕...");
    }


    public void SetRoomTip(String type, CommandSender sender) {
        //TODO
        Config settingRoomTip = new Config("FILE", Config.YAML);

        sender.sendMessage(settingRoomTip.getString("FirstTip"));

        List<String> tipList = settingRoomTip.getStringList(type);
        for (String tip : tipList) {
            sender.sendMessage(tip);
        }

        sender.sendMessage(settingRoomTip.getString("LastTip"));
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlock();
        if (setters.containsKey(p.getName())) {
            event.setCancelled(true);

            String room_name = setters.get(p.getName()).get("room_name");
            Config room = RoomManager.getRoomConfigFile(room_name);

            int x = (int) Math.round(Math.floor(block.x));
            int y = (int) Math.round(Math.floor(block.y));
            int z = (int) Math.round(Math.floor(block.z));
            String xyz = x + "+" + y + "+" + z;

            int step = Integer.parseInt(setters.get(p.getName()).get("step"));
            switch (setters.get(p.getName()).get("gameName")) {
                case "CrazyClick":
                    switch (step) {
                        case 1:
                            room.set("pos1", xyz);
                            room.set("pos2", xyz);
                            room.set("direction", "x+");
                            room.set("area", 1);//面积
                            room.save();
                            p.sendMessage(">>  请设置游戏介绍木牌");
                            setters.get(p.getName()).put("step", String.valueOf(step + 1));
                            break;
                        case 2:
                            if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
                                room.set("rule_pos", xyz);
                                room.save();
                                p.sendMessage(">>  请设置加入游戏木牌");
                                setters.get(p.getName()).put("step", String.valueOf(step + 1));
                            } else {
                                setters.get(p.getName()).put("step", "2");
                                p.sendMessage(">>  请破坏木牌");
                            }
                            break;
                        case 3:
                            if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
                                room.set("button_pos", xyz);
                                room.set("state", "true");
                                room.set("room_world", block.level.getName());
                                room.save();
                                RoomManager.addRoomConfig(room_name, (LinkedHashMap<String, Object>) room.getAll());
                                RoomManager.setRoomData(room_name);
                                setters.remove(p.getName());
                                p.sendMessage(">>  房间设置已完成");
                            } else {
                                setters.get(p.getName()).put("step", "3");
                                p.sendMessage(">>  请破坏木牌");
                            }
                            break;
                    }
                    break;
                case "Sudoku":
                    switch (step) {
                        case 1:
                            setters.get(p.getName()).put("pos1", xyz);
                            p.sendMessage(">>  请在刚刚破坏的方块的右边,再破坏一个方块,用于判断位置,保证两个方块在一条直线");
                            setters.get(p.getName()).put("step", String.valueOf(step + 1));
                            break;
                        case 2:
                            p.sendMessage(">>  请设置游戏介绍木牌");

                            String[] pos1 = setters.get(p.getName()).get("pos1").split("\\+");
                            String[] pos2 = xyz.split("\\+");

                            String d = "";
                            if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) < Integer.parseInt(pos2[0]))//从pos1开始运作
                            {
                                d = "x+";
                                room.set("pos1", (Integer.parseInt(pos1[0]) - 6) + "+" + (Integer.parseInt(pos1[1]) + 12) + "+" + Integer.parseInt(pos1[2]));
                                room.set("pos2", (Integer.parseInt(pos1[0]) + 6) + "+" + (Integer.parseInt(pos1[1])) + "+" + Integer.parseInt(pos1[2]));
                            } else if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) > Integer.parseInt(pos2[0])) {
                                d = "x-";
                                room.set("pos1", (Integer.parseInt(pos1[0]) + 6) + "+" + (Integer.parseInt(pos1[1]) + 12) + "+" + Integer.parseInt(pos1[2]));
                                room.set("pos2", (Integer.parseInt(pos1[0]) - 6) + "+" + (Integer.parseInt(pos1[1])) + "+" + Integer.parseInt(pos1[2]));
                            } else if (pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) < Integer.parseInt(pos2[2])) {
                                d = "z+";
                                room.set("pos1", Integer.parseInt(pos1[0]) + "+" + (Integer.parseInt(pos1[1]) + 12) + "+" + (Integer.parseInt(pos1[2]) - 6));
                                room.set("pos2", Integer.parseInt(pos1[0]) + "+" + (Integer.parseInt(pos1[1])) + "+" + (Integer.parseInt(pos1[2]) + 6));
                            } else if (pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) > Integer.parseInt(pos2[2])) {
                                d = "z-";
                                room.set("pos1", Integer.parseInt(pos1[0]) + "+" + (Integer.parseInt(pos1[1]) + 12) + "+" + (Integer.parseInt(pos1[2]) + 6));
                                room.set("pos2", Integer.parseInt(pos1[0]) + "+" + (Integer.parseInt(pos1[1])) + "+" + (Integer.parseInt(pos1[2]) - 6));
                            }
                            room.set("direction", d);
                            room.set("area", 0);//面积
                            room.save();
                            setters.get(p.getName()).put("step", String.valueOf(step + 1));
                            break;
                        case 3:
                            if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
                                room.set("rule_pos", xyz);
                                room.save();
                                p.sendMessage(">>  请设置加入游戏木牌");
                                setters.get(p.getName()).put("step", String.valueOf(step + 1));
                            } else {
                                setters.get(p.getName()).put("step", "3");
                                p.sendMessage(">>  请破坏木牌");
                            }
                            break;
                        case 4:
                            if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
                                room.set("button_pos", xyz);
                                room.set("state", "true");
                                room.set("room_world", block.level.getName());
                                room.save();
                                RoomManager.addRoomConfig(room_name, (LinkedHashMap<String, Object>) room.getAll());
                                RoomManager.setRoomData(room_name);
                                setters.remove(p.getName());
                                p.sendMessage(">>  房间设置已完成");
                            } else {
                                setters.get(p.getName()).put("step", "4");
                                p.sendMessage(">>  请破坏木牌");
                            }
                            break;
                    }
                    break;
                default:
                    switch (step) {
                        case 1:
                            setters.get(p.getName()).put("pos1", xyz);
                            room.set("pos1", xyz);
                            room.save();
                            p.sendMessage(">>  请设置点2");
                            setters.get(p.getName()).put("step", String.valueOf(step + 1));
                            break;
                        case 2:
                            room.set("pos2", xyz);
                            p.sendMessage(">>  请设置游戏介绍木牌");

                            String[] pos1 = setters.get(p.getName()).get("pos1").split("\\+");
                            String[] pos2 = xyz.split("\\+");

                            String d = null;
                            int width = 0;

                            // x+/x- z+/z- 为朝向不同
                            if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) < Integer.parseInt(pos2[0]))//从pos1开始运作
                            {
                                d = "x+";
                                width = Math.abs(Math.max(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0])) - Math.min(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0]))) + 1;
                            } else if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) > Integer.parseInt(pos2[0])) {
                                d = "x-";
                                width = Math.abs(Math.max(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0])) - Math.min(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0]))) + 1;
                            } else if (pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) < Integer.parseInt(pos2[2])) {
                                d = "z+";
                                width = Math.abs(Math.max(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2])) - Math.min(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2]))) + 1;
                            } else if (pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) > Integer.parseInt(pos2[2])) {
                                d = "z-";
                                width = Math.abs(Math.max(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2])) - Math.min(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2]))) + 1;
                            }
                            int length = Math.abs(Math.min(Integer.parseInt(pos1[1]), Integer.parseInt(pos2[1])) - Math.max(Integer.parseInt(pos1[1]), Integer.parseInt(pos2[1]))) + 1;
                            int area = length * width;
                            room.set("direction", d);
                            room.set("area", area);//面积
                            room.set("length", length);
                            room.set("width", width);
                            room.save();
                            setters.get(p.getName()).put("step", String.valueOf(step + 1));
                            break;
                        case 3:
                            if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
                                room.set("rule_pos", xyz);
                                room.save();
                                p.sendMessage(">>  请设置加入游戏木牌");
                                setters.get(p.getName()).put("step", String.valueOf(step + 1));
                            } else {
                                setters.get(p.getName()).put("step", "3");
                                p.sendMessage(">>  请破坏木牌");
                            }
                            break;
                        case 4:
                            if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
                                room.set("button_pos", xyz);
                                room.set("state", "true"); //设置完毕的状态
                                room.set("room_world", block.level.getName());
                                room.save();
                                RoomManager.addRoomConfig(room_name, (LinkedHashMap<String, Object>) room.getAll());
                                RoomManager.setRoomData(room_name);
                                setters.remove(p.getName());
                                p.sendMessage(">>  房间设置已完成");
                            } else {
                                setters.get(p.getName()).put("step", "4");
                                p.sendMessage(">>  请破坏木牌");
                            }
                            break;
                    }
                    break;
            }
        } else if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
            BlockEntity tile = event.getBlock().level.getBlockEntity(block);
            if (tile instanceof BlockEntitySign) {
                String text = ((BlockEntitySign) tile).getText()[2];
                if (text.equals("§a点击加入游戏") || text.equals("§a点击查看游戏介绍")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onJoin(PlayerQuitEvent event) {
        setters.remove(event.getPlayer().getName());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onChat(PlayerChatEvent event) {
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
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
            BlockEntity sign = event.getBlock().level.getBlockEntity(block);
            if (sign instanceof BlockEntitySign) {
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
                    String gameType = ((BlockEntitySign) sign).getText()[1];
                    String text = getRulePageTitle(gameType) + "§7§r---§c§l游戏玩法" + "\n" + getGameRule(gameType);
                    FormWindowSimple window = new FormWindowSimple(gameType + "§6游戏介绍", text);
                    player.showFormWindow(window);
                }
            }
        }
    }

    public static String getRulePageTitle(String game_type) {
        return "§f" + game_type + "§7>>§6§l" + getChineseName(game_type) + "";
    }

    private String getGameRule(String gameType) {
        //TODO
        Config gameRuleConfig = new Config("FILE", Config.YAML);

        StringBuilder gameRule = new StringBuilder();

        List<String> tipList = gameRuleConfig.getStringList(gameType);
        for (String tip : tipList) {
            gameRule.append(tip).append("\n");
        }

        return gameRule.toString();
    }

    public void Op_HelpMessage(CommandSender sender) {
        //TODO
    }

    public String getGameFile() {
        StringBuilder gameFile = new StringBuilder("    ");
        int a = 0;
        for (Map.Entry<Integer, String> map : Game.entrySet()) {
            gameFile.append(map.getKey()).append(":").append(getChineseName(map.getValue())).append(",");
            a = a + 1;
            if (a % 4 == 0) {
                gameFile.append("\n").append("    ");
            }
        }
        return gameFile.toString();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("cs".equals(command.getName())) {
            if (args.length < 1) {
                this.Op_HelpMessage(sender);
            } else {
                switch (args[0]) {
                    case "set":
                        if (sender instanceof Player) {
                            if (args.length < 2) {
                                sender.sendMessage(">  参数不足");
                                break;
                            }
                            if (!RoomManager.isExistRoomConfig(args[1])) {
                                sender.sendMessage(">  房间不存在");
                                break;
                            }
                            Room room = RoomManager.getRoom(args[1]);
                            if (room != null) {
                                if (room.isStarted || room.gamePlayer != null) {
                                    sender.sendMessage(">  房间正在游戏中");
                                    break;
                                }
                            }
                            LinkedHashMap<String, String> list = new LinkedHashMap<>();
                            list.put("gameName", (String) RoomManager.getRoomConfig(args[1]).get("game_type"));
                            list.put("room_name", args[1]);
                            list.put("step", String.valueOf(1));
                            setters.put(sender.getName(), list);
                            sender.sendMessage(">  房间" + args[1] + "正在设置");
                            this.SetRoomTip((String) RoomManager.getRoomConfig(args[1]).get("game_type"), sender);

                            RoomManager.removeSigns(args[1]);
                        } else {
                            sender.sendMessage(">  请在游戏中运行");
                        }
                        break;
                    case "add":
                        if (args.length < 3) {
                            sender.sendMessage(">  参数不足");
                            break;
                        }
                        if (RoomManager.isExistRoomConfig(args[1])) {
                            sender.sendMessage(">  房间已存在");
                            break;
                        }
                        if (!this.Game.containsKey(Integer.parseInt(args[2]))) {
                            sender.sendMessage(">  游戏类型输入错误");
                            break;
                        }
                        Config a = new Config(roomConfigFolderPath + args[1] + ".yml", Config.YAML);
                        a.set("game_type", Game.get(Integer.parseInt(args[2])));
                        a.set("state", "false");
                        a.set("arena", false);
                        a.set("room_world", " ");
                        a.set("start_time", "5");

                        switch (Game.get(Integer.parseInt(args[2]))) {
                            case "AvoidWhiteBlock":
                                a.set("times", 15);
                                break;
                            case "CrazyClick":
                                a.set("game_time", 20);
                                break;
                            case "BeFaster":
                                a.set("game_time", 60);
                                break;
                        }
                        a.save();
                        RoomManager.addRoomConfig(args[1], (LinkedHashMap<String, Object>) a.getAll());
                        sender.sendMessage(">  房间" + args[1] + "成功创建");
                        break;
                    case "del":
                        if (args.length < 2) {
                            sender.sendMessage(">  参数不足");
                            break;
                        }
                        if (!RoomManager.isExistRoomConfig(args[1])) {
                            sender.sendMessage(">  房间不存在");
                            break;
                        }
                        boolean file = new File(roomConfigFolderPath + args[1] + ".yml").delete();
                        if (file) {
                            RoomManager.deleteRoom(args[1]);
                            this.setters.remove(sender.getName());
                            sender.sendMessage(">  房间" + args[1] + "已成功删除");
                        } else {
                            sender.sendMessage(">  房间" + args[1] + "删除失败");
                        }
                        break;
                    case "help":
                    default:
                        this.Op_HelpMessage(sender);
                        break;
                }
            }
        }
//        else if ("csrank".equals(command.getName())) {
//            if (sender instanceof Player) {
//                FormWindowSimple window = new FormWindowSimple("CSwitch排行榜", getRank());
//                ((Player) sender).showFormWindow(window);
//            } else {
//                sender.sendMessage(">  请在游戏中运行");
//            }
//        }
        return true;
    }

//    public String getRank() {
//        StringBuilder rank = new StringBuilder("注:玩家名字前的数字代表耗时或分数\n   记录显示为000-player是暂无记录 \n");
//        Map<String, Object> c = config.getAll();
//        for (String m : c.keySet()) {
//            String gameName = m;
//            ArrayList<String> a = (ArrayList<String>) config.getList(gameName);
//            gameName = getChineseName(gameName);
//            for (int i = 0; i < 3; i++) {
//                rank.append("\n").append("§l§6").append(gameName).append(":§r§f").append("第§l§c").append(i + 1).append("§r§f名:§f").append(a.get(i));
//            }
//            rank.append("\n");
//        }
//        return rank.toString();
//    }

    public static String getChineseName(String gameName) {
        switch (gameName) {
            case "LightsOut":
                return "关灯";
            case "OneToOne":
                return "一一对应";
            case "Jigsaw":
                return "拼图";
            case "RemoveAll":
                return "方块消消乐";
            case "OnOneLine":
                return "宾果消消乐";
            case "BlockPlay_4":
                return "4X4方块华容道";
            case "BlockPlay_3":
                return "3X3方块华容道";
            case "CrazyClick":
                return "疯狂点击";
            case "Sudoku":
                return "数独";
            case "C2048":
                return "2048";
            case "AvoidWhiteBlock":
                return "别踩白块";
            case "HanoiTower":
                return "汉诺塔游戏";
            case "BeFaster":
                return "快速反应";
            case "CardMemory":
                return "颜色记忆";
            default:
                return null;
        }
    }
//
//    public void checkRank(String gameName, int spendTime, String gamer) {
//        try {
//            ArrayList<String> a = new ArrayList<>((Collection<? extends String>) config.get(gameName));
//            for (int i = 0; i < 3; i++) {
//                String[] in = a.get(i).split("-");
//                if (in[0].equals("000") || Integer.parseInt(in[0]) > spendTime) {
//                    a.set(i, spendTime + "-" + gamer);
//                    break;
//                }
//            }
//            config.set(gameName, a);
//            config.save();
//        } catch (Exception e) {
//        }
//    }
}

