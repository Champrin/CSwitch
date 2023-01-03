package net.createlight.champrin.cswitch;

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
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.createlight.champrin.cswitch.untils.MetricsLite;

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
    public final String PLUGIN_NAME = "CSwitch";
    public final String PLUGIN_No = "2";
    public final String PREFIX = "§a=§l§6" + PLUGIN_NAME + "§r§a=";
    public final String GAME_NAME = "我的游戏机!";
    public LinkedHashMap<String, LinkedHashMap<String, Object>> roomsConfig = new LinkedHashMap<>();//房间Config
    public LinkedHashMap<String, Room> rooms = new LinkedHashMap<>();//id->Room实例
    public LinkedHashMap<String, LinkedHashMap<String, String>> setters = new LinkedHashMap<>();//房间设置者
    public LinkedHashMap<Integer, String> Game = new LinkedHashMap<>();

    private static CSwitch instance;

    public static CSwitch getInstance() {
        return instance;
    }


    @Override
    public void onLoad() {
        Game.put(1, "LightsOut");//关灯
        Game.put(2, "OneToOne");
        Game.put(3, "Jigsaw");//五子棋
        Game.put(4, "RemoveAll");//方块消消乐
        Game.put(5, "BlockPlay_4");//15字游戏
        Game.put(6, "BlockPlay_3");//8字游戏
        Game.put(7, "CrazyClick");//疯狂点击
        Game.put(8, "AvoidWhiteBlock");//别踩白块
        Game.put(9, "Sudoku");//数独
        Game.put(10, "BeFaster");//快速反应
        Game.put(11, "HanoiTower");//汉诺塔游戏
        Game.put(12, "CardMemory");//记忆翻牌
        Game.put(13, "BeFaster");//记忆翻牌
        //Game.put(12, "C2048");//2048
        //Game.put(13, "OnOneLine");//宾果消消乐
        instance = this;
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
        //给每个房间结算结果
        if (!rooms.isEmpty()) {
            for (Map.Entry<String, Room> map : rooms.entrySet()) {
                map.getValue().serverStop();
            }
        }
    }

    public void LoadConfig() {
        this.getLogger().info("-配置文件加载中...");

        if (!new File(this.getDataFolder() + "/config.yml").exists()) {
            this.saveResource("config.yml", false);
        }
        this.config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);
        if (config.get("BlockPlay_4") == null) {
            config.set("BlockPlay_4", new ArrayList<>(Arrays.asList("000-player", "000-player", "000-player")));
        }
        if (config.get("BlockPlay_3") == null) {
            config.set("BlockPlay_3", new ArrayList<>(Arrays.asList("000-player", "000-player", "000-player")));
        }
        if (config.get("CrazyClick") == null) {
            config.set("CrazyClick", new ArrayList<>(Arrays.asList("000-player", "000-player", "000-player")));
        }
        if (config.get("Sudoku") == null) {
            config.set("Sudoku", new ArrayList<>(Arrays.asList("000-player", "000-player", "000-player")));
        }
        if (config.get("AvoidWhiteBlock") == null) {
            config.set("AvoidWhiteBlock", new ArrayList<>(Arrays.asList("000-player", "000-player", "000-player")));
        }
        if (config.get("BeFaster") == null) {
            config.set("BeFaster", new ArrayList<>(Arrays.asList("000-player", "000-player", "000-player")));
        }
        if (config.get("HanoiTower") == null) {
            config.set("HanoiTower", new ArrayList<>(Arrays.asList("000-player", "000-player", "000-player")));
        }
        if (config.get("CardMemory") == null) {
            config.set("CardMemory", new ArrayList<>(Arrays.asList("000-player", "000-player", "000-player")));
        }
        if (config.get("BeFaster") == null) {
            config.set("BeFaster", new ArrayList<>(Arrays.asList("000-player", "000-player", "000-player")));
        }
        config.save();

        if (!new File(this.getDataFolder() + "/language.yml").exists()) {
            this.saveResource("language.yml", false);
        }
        this.language = new Config(this.getDataFolder() + "/language.yml", Config.YAML);

        File file = new File(this.getDataFolder() + "/Room/");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                this.getServer().getLogger().info("文件夹创建失败");
            }
        }
    }

    public void LoadRoomConfig() {
        this.getLogger().info("-房间信息加载中...");
        File file = new File(this.getDataFolder() + "/Room/");
        File[] files = file.listFiles();
        if (files != null) {
            for (File FILE : files) {
                if (FILE.isFile()) {
                    Config room = new Config(FILE, Config.YAML);
                    String FileName = FILE.getName().substring(0, FILE.getName().lastIndexOf("."));
                    roomsConfig.put(FileName, new LinkedHashMap<>(room.getAll()));
                    if ("true".equals(room.get("state"))) {
                        this.setRoomData(FileName);
                        this.getLogger().info("   房间§b" + FileName + "§r加载完成");
                    }

                }
            }
        }
        this.getLogger().info("-房间信息加载完毕...");
    }

    public void setRoomData(String name) {
        Room game = new Room(name, this);
        rooms.put(name, game);
        setFloatingText(name);
        this.getServer().getPluginManager().registerEvents(game, this);
    }

    public Config getRoomData(String room_name) {
        config = new Config(this.getDataFolder() + "/Room/" + room_name + ".yml", Config.YAML);
        return config;
    }

    public boolean RoomExist(String name) {
        return roomsConfig.containsKey(name);
    }

    public Room getPlayerRoom(Player p) {
        for (Map.Entry<String, Room> map : this.rooms.entrySet()) {
            Room room = map.getValue();
            if (room.gamePlayer == p) {
                return room;
            }
        }
        return null;
    }

    public boolean isRoomSet(String name) //判断房间是否存在
    {
        return rooms.containsKey(name);
    }

    public Room getRoom(String room_name) {
        return rooms.getOrDefault(room_name, null);
    }


    public void DelFloatingText(String room_name) {
        for (BlockEntitySign map : FT.get(room_name)) {
            map.level.setBlock(map, Block.get(0, 0));
        }
        FT.remove(room_name);
    }

    public String getFtTitle(String game_type) {
        return "§f" + game_type + "§7>>§6§l" + getChineseName(game_type) + "";
    }

    public String getGameRule(String game_type) {
        String gt = "";
        switch (game_type) {
            case "LightsOut":
                gt = "§a胜利条件: §f使所有方块都是绿色羊毛\n";
                gt = gt + "§b玩法: §f点击黑色羊毛，其上下左右的黑色\n" +
                        "      §f羊毛都会变成绿色而周围的绿色羊\n" +
                        "      §f毛会重新变为黑色";
                break;
            case "OneToOne":
                gt = "§b玩法: §f将手中的颜料，与游戏区域内的原料一一对应\n" +
                        "      §f对应正确得一分，错误扣1分，直至所有方块都\n" +
                        "      §f变为黑色时游戏结束";
                break;
            case "Jigsaw":
                gt = "§a胜利条件: §f使游戏区域内的方块变为与模板一模一样的图案\n";
                gt = gt + "§b玩法: §f将手中的方块正确点击游戏区域,要做到\n" +
                        "      §f手中的方块与模板的同一位置的方块一样\n";
                break;
            case "RemoveAll":
                gt = "§a胜利条件: §f消除所有方块\n";
                gt = gt + "§b玩法: §f点击一个方块可以消除\n" +
                        "      §f当这个方块周围连着同样的\n" +
                        "      §f方块时,会被一起消除";
                break;
            case "OnOneLine":
                gt = "§a胜利条件: §f尽可能的消完方块\n";
                gt = gt + "§b玩法: §f点击一个方块与另外一个方块交换\n" +
                        "      §f当两个方块的连线有相同方块时，会消除所有同颜色的方块\n" +
                        "§c注意: §a当你认为你已经不能再进行下一步时,请切换“门”物品以结束\n" +
                        "      §a游戏！此游戏排行榜以分统计！";
                break;
            case "CrazyClick":
                gt = gt + "§b玩法: §f测试手的手速！\n" +
                        "      §f游戏开始后，尽你的可能快速点击游戏区\n" +
                        "      §f域内的\"钻石块\"\n";
                break;
            case "Sudoku":
                gt = "§a胜利条件: §f各种颜色的羊毛在每一行、每一列和每一宫中都只出现一次\n";
                gt = gt + "§b玩法: §f给出一定的已知羊毛颜色和解题条件\n" +
                        "      §f利用逻辑和推理,在其他的空格上填入羊毛\n" +
                        "      §f点击方块可以删除答案\n";
                break;
            case "C2048":
                gt = "§a胜利条件: §f最终拼出绿色羊毛方块！\n";
                gt = gt + "§b玩法: §f用物品栏的物品，实现上下左右操作\n" +
                        "      §f你需要控制所有方块向同一个方向运动，两个相同的方块撞在一起之后会生成\n" +
                        "      §f下一级的方块，每次操作之后会随机生成一个初始方块\n";
                break;
            case "AvoidWhiteBlock":
                gt = "§a胜利条件: §f将所有黑块“踩齐”\n";
                gt = gt + "§b玩法: §f踩黑块,不能踩白块\n";
                break;
            case "HanoiTower":
                gt = "§a胜利条件: §f将左边第一列所有方块移动到右边第一列\n";
                gt = gt + "§b玩法: §f每次只能移动一个盘子。\n" +
                        "      §f以左边第一列为主要，下面的方块不能放在这个方块上面的方块的上面！\n";
                break;
            case "BeFaster":
                gt = "§b玩法: §f类似打地鼠，在一定的时间内，尽可能的快速点击带颜色的方块\n";
                break;
            case "CardMemory":
                gt = "§b玩法: §f类似连连看，寻找相同色块的方块，使前后两次点击的色块相同\n";
                break;
            case "BlockPlay_4":
            case "BlockPlay_3":
                gt = "§a胜利条件: §f将方块的顺序移为跟模板一样的顺序\n";
                gt = gt + "§b玩法: §f点击你想移动的方块，会与玻璃方块互相交换\n" +
                        "      §f交换,最后移至跟模板一样即可\n";
                break;
        }
        return gt;
    }

    public void SetRoomTip(String type, CommandSender sender) {
        sender.sendMessage(">>>  边框自行设置且不能使用羊毛 §a一些需要模板的游戏,请自行建造！");
        switch (type) {
            case "LightsOut":
            case "AvoidWhiteBlock":
            case "BeFaster":
                sender.sendMessage(">>  §l§c!!!设置要求：§r请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2");
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a大于3x3大小§r的§c竖直平面§r的方块设置点2");
                break;
            case "CardMemory":
                sender.sendMessage(">>  §l§c!!!设置要求：§r请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2");
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a大于3x3大小§r的§c竖直平面§r的方块设置点2");
                sender.sendMessage("    §c并保证区域面积为偶数！");
                break;
            case "C2048":
                sender.sendMessage(">>  §l§c!!!设置要求：§r请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2");
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a大于4x4大小§r的§c竖直平面§r的方块设置点2");
                sender.sendMessage("    §c建议设置4x4");
                break;
            case "OneToOne":
                sender.sendMessage(">>  §l§c!!!设置要求：§r请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2");
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a大于3x3§r大小的§c竖直平面§r的方块来设置点2");
                break;
            case "Jigsaw":
                sender.sendMessage(">>  §l§c!!!设置要求：§r请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2");
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a3x3§r大小的§c竖直平面§r的方块设置点2");
                break;
            case "RemoveAll":
            case "OnOneLine":
                sender.sendMessage(">>  §l§c!!!设置要求：§r请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2");
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a大于6x6§r大小的§c竖直平面§r的方块设置点2");
                break;
            case "BlockPlay_4":
                sender.sendMessage(">>  §l§c!!!设置要求：§r请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2");
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a4x4大小§r的§c竖直平面§r的方块设置点2");
                break;
            case "HanoiTower":
                sender.sendMessage(">>  §l§c!!!设置要求：§r请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2");
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a高为3 宽为5大小§r的§c竖直平面§r的方块设置点2");
                break;
            case "CrazyClick":
                sender.sendMessage(">>  请破坏一个方块设置游戏区域");
                break;
            case "Sudoku":
                sender.sendMessage(">>  请破坏一个方块,并保证该方块的左右6个单位的方块都无其他方块");
                sender.sendMessage(">>  边框自动生成");
                break;
        }
        sender.sendMessage(">>  §l§c如不按照要求来设置游戏区域范围，程序运行错误导致服务器崩溃造成的后果自负");
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlock();
        if (setters.containsKey(p.getName())) {
            event.setCancelled(true);

            String room_name = setters.get(p.getName()).get("room_name");
            Config room = this.getRoomData(room_name);

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
                                roomsConfig.put(room_name, (LinkedHashMap<String, Object>) room.getAll());
                                setRoomData(room_name);
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
                                roomsConfig.put(room_name, (LinkedHashMap<String, Object>) room.getAll());
                                setRoomData(room_name);
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
                                room.set("state", "true");
                                room.set("room_world", block.level.getName());
                                room.save();
                                roomsConfig.put(room_name, (LinkedHashMap<String, Object>) room.getAll());
                                setRoomData(room_name);
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
    public void onJoin(PlayerJoinEvent event) {
        setters.remove(event.getPlayer().getName());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (getPlayerRoom(player) == null) return;
        if (event.getMessage().contains("@hub")) {
            event.setCancelled(true);
            player.sendMessage(">  你已退出游戏！");
            this.getPlayerRoom(player).stopGame();
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onTouch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getId() == Block.SIGN_POST || block.getId() == Block.WALL_SIGN) {
            BlockEntity tile = event.getBlock().level.getBlockEntity(block);
            if (tile instanceof BlockEntitySign) {
                if (((BlockEntitySign) tile).getText()[2].equals("§a点击加入游戏")) {
                    event.setCancelled();
                    int x = (int) Math.round(Math.floor(block.x));
                    int y = (int) Math.round(Math.floor(block.y));
                    int z = (int) Math.round(Math.floor(block.z));
                    String xyz = x + "+" + y + "+" + z;
                    for (Map.Entry<String, LinkedHashMap<String, Object>> map : this.roomsConfig.entrySet()) {
                        if (xyz.equals(map.getValue().get("button_pos"))) {
                            if (this.getRoom(map.getKey()) != null) {
                                this.getRoom(map.getKey()).joinToRoom(player);
                            }
                            break;
                        }
                    }
                } else if (((BlockEntitySign) tile).getText()[2].equals("§a点击查看游戏介绍")) {
                    event.setCancelled();
                    String game_type = ((BlockEntitySign) tile).getText()[1];
                    String text = getFtTitle(game_type) + "§7§r---§c§l游戏玩法" + "\n" + getGameRule(game_type);
                    FormWindowSimple window = new FormWindowSimple(game_type + "§6游戏介绍", text);
                    player.showFormWindow(window);
                }
            }
        }
    }

    public void Op_HelpMessage(CommandSender sender) {
        sender.sendMessage(">  §b==========§r" + PREFIX + "§r§b==========§r");
        sender.sendMessage(">  /cs add [房间名] [游戏序号]------ §d创建新房间");
        sender.sendMessage(">  /cs set [房间名] ------ §d设置房间");
        sender.sendMessage(">  /cs del [房间名] ------ §d删除房间");
        sender.sendMessage(">  游戏类型:\n" + getGameFile());
        sender.sendMessage(">  [游戏序号]为 游戏类型前的数字,请正确填写");
    }

    public String getGameFile() {
        String gameFile = "    ";
        int a = 0;
        for (Map.Entry<Integer, String> map : Game.entrySet()) {
            gameFile = gameFile + map.getKey() + ":" + getChineseName(map.getValue()) + ",";
            a = a + 1;
            if (a % 4 == 0) {
                gameFile = gameFile + "\n" + "    ";
            }
        }
        return gameFile;
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
                            if (!this.RoomExist(args[1])) {
                                sender.sendMessage(">  房间不存在");
                                break;
                            }
                            if (this.isRoomSet(args[1])) {
                                Room a = this.rooms.get(args[1]);
                                if (a.isStarted || a.gamePlayer != null) {
                                    sender.sendMessage(">  房间正在游戏中");
                                    break;
                                }
                            }
                            LinkedHashMap<String, String> list = new LinkedHashMap<>();
                            list.put("gameName", (String) roomsConfig.get(args[1]).get("game_type"));
                            list.put("room_name", args[1]);
                            list.put("step", String.valueOf(1));
                            setters.put(sender.getName(), list);
                            sender.sendMessage(">  房间" + args[1] + "正在设置");
                            this.SetRoomTip((String) roomsConfig.get(args[1]).get("game_type"), sender);
                            if (FT.containsKey(args[1])) {
                                DelFloatingText(args[1]);
                            }
                        } else {
                            sender.sendMessage(">  请在游戏中运行");
                        }
                        break;
                    case "add":
                        if (args.length < 3) {
                            sender.sendMessage(">  参数不足");
                            break;
                        }
                        if (this.RoomExist(args[1])) {
                            sender.sendMessage(">  房间已存在");
                            break;
                        }
                        if (!this.Game.containsKey(Integer.parseInt(args[2]))) {
                            sender.sendMessage(">  游戏类型输入错误");
                            break;
                        }
                        Config a = new Config(this.getDataFolder() + "/Room/" + args[1] + ".yml", Config.YAML);
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
                        roomsConfig.put(args[1], (LinkedHashMap<String, Object>) a.getAll());
                        sender.sendMessage(">  房间" + args[1] + "成功创建");
                        break;
                    case "del":
                        if (args.length < 2) {
                            sender.sendMessage(">  参数不足");
                            break;
                        }
                        if (!this.RoomExist(args[1])) {
                            sender.sendMessage(">  房间不存在");
                            break;
                        }
                        boolean file = new File(this.getDataFolder() + "/Room/" + args[1] + ".yml").delete();
                        if (file) {
                            if (rooms.containsKey(args[1])) {
                                rooms.get(args[1]).stopGame();
                                rooms.remove(args[1]);
                            }
                            if (FT.containsKey(args[1])) {
                                DelFloatingText(args[1]);
                            }
                            this.setters.remove(sender.getName());
                            roomsConfig.remove(args[1]);
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
        } else if ("csrank".equals(command.getName())) {
            if (sender instanceof Player) {
                FormWindowSimple window = new FormWindowSimple("CSwitch排行榜", getRank());
                ((Player) sender).showFormWindow(window);
            } else {
                sender.sendMessage(">  请在游戏中运行");
            }
        }
        return true;
    }

    public String getRank() {
        StringBuilder rank = new StringBuilder("注:玩家名字前的数字代表耗时或分数\n   记录显示为000-player是暂无记录 \n");
        Map<String, Object> c = config.getAll();
        for (String m : c.keySet()) {
            String gameName = m;
            ArrayList<String> a = (ArrayList<String>) config.getList(gameName);
            gameName = getChineseName(gameName);
            for (int i = 0; i < 3; i++) {
                rank.append("\n").append("§l§6").append(gameName).append(":§r§f").append("第§l§c").append(i + 1).append("§r§f名:§f").append(a.get(i));
            }
            rank.append("\n");
        }
        return rank.toString();
    }

    public String getChineseName(String gameName) {
        return switch (gameName) {
            case "LightsOut" -> "关灯";
            case "OneToOne" -> "一一对应";
            case "Jigsaw" -> "拼图";
            case "RemoveAll" -> "方块消消乐";
            case "OnOneLine" -> "宾果消消乐";
            case "BlockPlay_4" -> "4X4方块华容道";
            case "BlockPlay_3" -> "3X3方块华容道";
            case "CrazyClick" -> "疯狂点击";
            case "Sudoku" -> "数独";
            case "C2048" -> "2048";
            case "AvoidWhiteBlock" -> "别踩白块";
            case "HanoiTower" -> "汉诺塔游戏";
            case "BeFaster" -> "快速反应";
            case "CardMemory" -> "颜色记忆";
            default -> null;
        };
    }

    public void checkRank(String gameName, int spendTime, String gamer) {
        try {
            ArrayList<String> a = new ArrayList<>((Collection<? extends String>) config.get(gameName));
            for (int i = 0; i < 3; i++) {
                String[] in = a.get(i).split("-");
                if (in[0].equals("000") || Integer.parseInt(in[0]) > spendTime) {
                    a.set(i, spendTime + "-" + gamer);
                    break;
                }
            }
            config.set(gameName, a);
            config.save();
        } catch (Exception e) {
        }
    }

    public void setFloatingText(String room_name) {
        LinkedHashMap<String, Object> m = roomsConfig.get(room_name);
        Level level = this.getServer().getLevelByName((String) m.get("room_world"));
        String[] p1 = ((String) m.get("button_pos")).split("\\+");

        double x1 = Integer.parseInt(p1[0]);
        double y1 = Integer.parseInt(p1[1]);
        double z1 = Integer.parseInt(p1[2]);

        String[] p2 = ((String) m.get("rule_pos")).split("\\+");

        double x2 = Integer.parseInt(p2[0]);
        double y2 = Integer.parseInt(p2[1]);
        double z2 = Integer.parseInt(p2[2]);

        Block block;
        BlockEntity tile;
        BlockEntitySign sign, sign1;

        block = level.getBlock(new Vector3(x1, y1, z1));
        tile = level.getBlockEntity(block);
        if (tile instanceof BlockEntitySign) {
            sign = (BlockEntitySign) tile;
        } else {
            sign = new BlockEntitySign(block.getLevel().getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), BlockEntity.getDefaultCompound(block, BlockEntity.SIGN));
        }
        sign.setText(PREFIX, (String) m.get("game_type"), "§a点击加入游戏");

        block = level.getBlock(new Vector3(x2, y2, z2));
        tile = level.getBlockEntity(block);
        if (tile instanceof BlockEntitySign) {
            sign1 = (BlockEntitySign) tile;
        } else {
            sign1 = new BlockEntitySign(block.getLevel().getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), BlockEntity.getDefaultCompound(block, BlockEntity.SIGN));
        }
        sign1.setText(PREFIX, (String) m.get("game_type"), "§a点击查看游戏介绍");

        FT.put(room_name, new ArrayList<>(Arrays.asList(sign, sign1)));
    }

    public LinkedHashMap<String, ArrayList<BlockEntitySign>> FT = new LinkedHashMap<>();

    public void changeSign(String roomName) {
        BlockEntitySign sign = FT.get(roomName).get(0);
        Room room = rooms.get(roomName);
        if (!room.isStarted) {
            sign.setText(PREFIX, room.gameTypeName, "§a点击加入游戏");
        } else {
            sign.setText(PREFIX, room.gameTypeName, "§f" + room.gamePlayer.getName() + "§a正在游戏");
        }
    }
}

