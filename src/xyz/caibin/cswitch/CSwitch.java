package xyz.caibin.cswitch;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.*;


public class CSwitch extends PluginBase implements Listener {
/*
//TODO
// 1.HanoiTower 汉诺塔游戏
// 2.MemoryMaster 记忆大师
*/
    public Config config;
    public final String PLUGIN_NAME = "CSwitch";
    public final String PLUGIN_No = "8";
    public final String PREFIX = "§a=§l§6CSwitch§r§a=";
    public final String GAME_NAME = "我的游戏机!";
    public LinkedHashMap<String, LinkedHashMap<String, Object>> rooms_message = new LinkedHashMap<>();//房间基本信息
    public LinkedHashMap<String, Room> rooms = new LinkedHashMap<>();//开启的房间信息 存储Room实例
    public LinkedHashMap<String, LinkedHashMap<String, String>> setters = new LinkedHashMap<>();//房间设置信息
    public LinkedHashMap<Integer, String> Game = new LinkedHashMap<>();

    private static CSwitch instance;

    public static CSwitch getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        Game.put(1, "LightsOut");
        Game.put(2, "OneToOne");//关灯
        Game.put(3, "Jigsaw");//五子棋
        Game.put(4, "RemoveAll");//方块消消乐
        Game.put(5, "BlockPlay_4");//15字游戏
        Game.put(6, "BlockPlay_3");//8字游戏
        instance = this;
    }

    @Override
    public void onEnable() {
        long start = new Date().getTime();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info(PREFIX + "  §d加载中。。。§e|作者：Champrin");
        this.getLogger().info(PREFIX + "  §e ==> Champrin的第§c" + PLUGIN_No + "§e款插件/小游戏 " + GAME_NAME + "！");

        this.LoadConfig();
        this.LoadRoomConfig();

        this.getLogger().info(PREFIX + "  §d已加载完毕。。。");
        this.getLogger().info(PREFIX + "  §e加载耗时" + (new Date().getTime() - start) + "毫秒");
    }
    @Override
    public void onDisable() {
        //给每个房间结算结果
        if (!rooms.isEmpty()) {
            for (Map.Entry<String, Room> map : rooms.entrySet()) {
                map.getValue().stopGame();
            }
        }
    }

    public void LoadConfig() {
        this.getLogger().info("-配置文件加载中...");

        if (!new File(this.getDataFolder() + "/config.yml").exists()) {
            this.saveResource("config.yml", false);
        }
        this.config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);

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
                    rooms_message.put(FileName, new LinkedHashMap<>(room.getAll()));
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


    public boolean RoomExist(String name)//判断房间是否存在
    {
        return rooms_message.containsKey(name);
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

    public LinkedHashMap<String, LinkedHashMap<FloatingTextParticle, Level>> Ft = new LinkedHashMap<>();

    public void setFloatingText(String room_name) {
        LinkedHashMap<String, Object> m = rooms_message.get(room_name);
        Level level = this.getServer().getLevelByName((String) m.get("room_world"));
        String[] p1 = ((String) m.get("button_pos")).split("\\+");
        String[] p2 = ((String) m.get("pos2")).split("\\+");
        String direction = (String) m.get("direction");
        FloatingTextParticle P1 = null, P2 = null;
        switch (direction) {
            case "x+": {
                int z = Integer.parseInt(p1[2]);
                P1 = new FloatingTextParticle(new Vector3(Integer.parseInt(p1[0]) + 0.5, Integer.parseInt(p1[1]) + 1.5, z + 0.5),
                        getFtTitle((String) m.get("game_type")), "§d§l点击下方按钮加入游戏！");
                P2 = new FloatingTextParticle(new Vector3(Integer.parseInt(p2[0]) + 2.5, Integer.parseInt(p2[1]) + 1.5, z + 0.5),
                        getFtTitle((String) m.get("game_type")) + "§7§r---§c§l游戏玩法", getGameRule((String) m.get("game_type")));
                break;
            }
            case "x-": {
                int z = Integer.parseInt(p1[2]);
                P1 = new FloatingTextParticle(new Vector3(Integer.parseInt(p1[0]) + 0.5, Integer.parseInt(p1[1]) + 1.5, z + 0.5),
                        getFtTitle((String) m.get("game_type")), "§d§l点击下方按钮加入游戏！");
                P2 = new FloatingTextParticle(new Vector3(Integer.parseInt(p2[0]) - 2.5, Integer.parseInt(p2[1]) + 1.5, z + 0.5),
                        getFtTitle((String) m.get("game_type")) + "§7§r---§c§l游戏玩法", getGameRule((String) m.get("game_type")));
                break;
            }
            case "z+": {
                int x = Integer.parseInt(p1[0]);
                P1 = new FloatingTextParticle(new Vector3(x + 0.5, Integer.parseInt(p1[1]) + 1.5, Integer.parseInt(p1[2]) + 0.5),
                        getFtTitle((String) m.get("game_type")), "§d§l点击下方按钮加入游戏！");
                P2 = new FloatingTextParticle(new Vector3(x + 0.5, Integer.parseInt(p2[1]) + 1.5, Integer.parseInt(p2[2]) + 2.5),
                        getFtTitle((String) m.get("game_type")) + "§7§r---§c§l游戏玩法", getGameRule((String) m.get("game_type")));
                break;
            }
            case "z-": {
                int x = Integer.parseInt(p1[0]);
                P1 = new FloatingTextParticle(new Vector3(x + 0.5, Integer.parseInt(p1[1]) + 1.5, Integer.parseInt(p1[2]) + 0.5),
                        getFtTitle((String) m.get("game_type")), "§d§l点击下方按钮加入游戏！");
                P2 = new FloatingTextParticle(new Vector3(x + 0.5, Integer.parseInt(p2[1]) + 1.5, Integer.parseInt(p2[2]) - 2.5),
                        getFtTitle((String) m.get("game_type")) + "§7§r---§c§l游戏玩法", getGameRule((String) m.get("game_type")));
                break;
            }
        }
        LinkedHashMap<FloatingTextParticle, Level> t = new LinkedHashMap<>();
        t.put(P1, level);
        t.put(P2, level);
        Ft.put(room_name, t);
    }

    public void DelFloatingText(String room_name) {
        LinkedHashMap<FloatingTextParticle, Level> FT = Ft.get(room_name);
        for (Map.Entry<FloatingTextParticle, Level> map : FT.entrySet()) {
            map.getKey().setInvisible(true);
        }
        Ft.remove(room_name);
    }

    public String getFtTitle(String game_type) {
        String gt = "";
        switch (game_type) {
            case "LightsOut":
                gt = "关灯";
                break;
            case "OneToOne":
                gt = "一一对应";
                break;
            case "Jigsaw":
                gt = "拼图";
                break;
            case "RemoveAll":
                gt = "方块消消乐";
                break;
            case "BlockPlay_4":
            case "BlockPlay_3":
                gt = "方块华容道";
                break;
        }
        return "§f" + game_type + "§7>>§6§l" + gt + "";
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
        sender.sendMessage(">>  §l§c!!!设置要求：§r请使用竖立平面且最左上角必须设为点1，最右下角必须设为点2");
        switch (type) {
            case "LightsOut":
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a大于3x3大小§r的竖直平面的方块设置点2");
                break;
            case "OneToOne":
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a大于6x6§r大小的竖直平面的方块来设置点2");
                break;
            case "Jigsaw":
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a3x3§r大小的竖直平面的方块设置点2");
                break;
            case "RemoveAll":
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a大于6x6§r大小的竖直平面的方块设置点2");
                break;
            case "BlockPlay_4":
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a4x4大小§r的竖直平面的方块设置点2");
                break;
            case "BlockPlay_3":
                sender.sendMessage(">>  请破坏方块设置点1，然后破坏一个§a3x3大小§r的竖直平面的方块设置点2");
                break;
        }
        sender.sendMessage(">>  §l§c如不按照要求来设置游戏区域范围，程序运行错误导致服务器崩溃造成的后果自负");
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (setters.containsKey(p.getName())) {
            event.setCancelled(true);
            Block b = event.getBlock();

            String room_name = setters.get(p.getName()).get("room_name");
            Config room = this.getRoomData(room_name);

            int x = (int) Math.round(Math.floor(b.x));
            int y = (int) Math.round(Math.floor(b.y));
            int z = (int) Math.round(Math.floor(b.z));
            String xyz = x + "+" + y + "+" + z;

            int step = Integer.parseInt(setters.get(p.getName()).get("step"));

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
                    p.sendMessage(">>  请设置加入游戏按钮");

                    String[] pos1 = setters.get(p.getName()).get("pos1").split("\\+");
                    String[] pos2 = xyz.split("\\+");

                    String d = null;
                    int length = 0;

                    if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) < Integer.parseInt(pos2[0]))//从pos1开始运作
                    {
                        d = "x+";
                        length = Math.abs(Math.max(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0])) - Math.min(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0]))) + 1;
                    } else if (pos1[2].equals(pos2[2]) && Integer.parseInt(pos1[0]) > Integer.parseInt(pos2[0])) {
                        d = "x-";
                        length = Math.abs(Math.max(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0])) - Math.min(Integer.parseInt(pos1[0]), Integer.parseInt(pos2[0]))) + 1;
                    } else if (pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) < Integer.parseInt(pos2[2])) {
                        d = "z+";
                        length = Math.abs(Math.max(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2])) - Math.min(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2]))) + 1;
                    } else if (pos1[0].equals(pos2[0]) && Integer.parseInt(pos1[2]) > Integer.parseInt(pos2[2])) {
                        d = "z-";
                        length = Math.abs(Math.max(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2])) - Math.min(Integer.parseInt(pos1[2]), Integer.parseInt(pos2[2]))) + 1;
                    }
                    int width = Math.abs(Math.min(Integer.parseInt(pos1[1]), Integer.parseInt(pos2[1])) - Math.max(Integer.parseInt(pos1[1]), Integer.parseInt(pos2[1]))) + 1;
                    int area = length * width;
                    room.set("direction", d);
                    room.set("area", area);//面积
                    room.set("length", length);
                    room.set("width", width);
                    room.save();
                    setters.get(p.getName()).put("step", String.valueOf(step + 1));
                    break;
                case 3:
                    if (b.getId() == 143) {
                        room.set("button_pos", xyz);
                        room.set("state", "true");
                        room.set("room_world", b.level.getName());
                        room.save();
                        rooms_message.put(room_name, (LinkedHashMap<String, Object>) room.getAll());
                        setRoomData(room_name);
                        rooms.get(room_name).setGameArena();
                        setters.remove(p.getName());
                        loadFloatingText(p, Ft.get(room_name));
                        p.sendMessage(">>  房间设置已完成");
                    } else {
                        setters.get(p.getName()).put("step", "3");
                        p.sendMessage(">>  请破坏木质按钮");
                    }
                    break;
            }
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        setters.remove(p.getName());
        loadAllFloatingText(p);
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player p = event.getPlayer();
        if (getPlayerRoom(p) == null) return;
        if (event.getMessage().contains("@hub")) {
            event.setCancelled(true);
            p.sendMessage(">  你已退出游戏！");
            this.getPlayerRoom(p).stopGame();
        }
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block b = event.getBlock();
        if (b.getId() == 143) {
            int x = (int) Math.round(Math.floor(b.x));
            int y = (int) Math.round(Math.floor(b.y));
            int z = (int) Math.round(Math.floor(b.z));
            String xyz = x + "+" + y + "+" + z;
            for (Map.Entry<String, LinkedHashMap<String, Object>> map : this.rooms_message.entrySet()) {
                if (xyz.equals(map.getValue().get("button_pos"))) {
                    if (this.getRoom(map.getKey()) != null) {
                        this.getRoom(map.getKey()).joinToRoom(player);
                    }
                    break;
                }
            }

        }
    }

    public void loadAllFloatingText(Player player) {
        for (Map.Entry<String, LinkedHashMap<FloatingTextParticle, Level>> map : Ft.entrySet()) {
            LinkedHashMap<FloatingTextParticle, Level> ft = map.getValue();
            loadFloatingText(player, ft);
        }
    }

    public void loadFloatingText(Player player, LinkedHashMap<FloatingTextParticle, Level> ft) {
        for (Map.Entry<FloatingTextParticle, Level> FtMap : ft.entrySet()) {
            FtMap.getValue().addParticle(FtMap.getKey(), player);
        }

    }

    @EventHandler
    public void onTp(PlayerTeleportEvent event) {
        loadAllFloatingText(event.getPlayer());
    }

    public void Op_HelpMessage(CommandSender sender) {
        sender.sendMessage(">  §b==========" + PREFIX + "==========§r");
        sender.sendMessage(">  /cs add [房间名] [游戏序号]------ §d创建新房间");
        sender.sendMessage(">  /cs set [房间名] ------ §d设置房间");
        sender.sendMessage(">  /cs del [房间名] ------ §d删除房间");
        sender.sendMessage(">  游戏类型： 1:关灯,2:一一对应,3:拼图,4:方块消消乐\n          5:方块华容道4*4,6:方块华容道3*3");
        sender.sendMessage(">  [游戏序号]为 游戏类型前的数字,请正确填写");
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
                                if (a.game != 0 || a.gamePlayer != null) {
                                    sender.sendMessage(">  房间正在游戏中");
                                    break;
                                }
                            }
                            LinkedHashMap<String, String> list = new LinkedHashMap<>();
                            list.put("room_name", args[1]);
                            list.put("step", String.valueOf(1));
                            setters.put(sender.getName(), list);
                            sender.sendMessage(">  房间" + args[1] + "正在设置");
                            this.SetRoomTip((String) rooms_message.get(args[1]).get("game_type"), sender);
                            if (rooms_message.get(args[1]).get("button_pos") != null) {
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
                        a.set("room_world", " ");
                        a.set("start_time", "5");
                        a.save();
                        rooms_message.put(args[1], (LinkedHashMap<String, Object>) a.getAll());
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
                            rooms_message.remove(args[1]);
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
        StringBuilder rank= new StringBuilder("注:记录的时间单位都为秒\n   记录显示为000-player是暂无记录 \n");;
        Map<String, Object> c = config.getAll();
        for (String m : c.keySet())
        {
            String gameName = m;
            ArrayList<String> a = new ArrayList<>((Collection<? extends String>) config.get(gameName));
            gameName = getChineseName(gameName);
            for (int i = 0; i < 3; i++) {
                rank.append("\n").append("§l§6").append(gameName).append(":§r§f").append("第§l§c").append(i + 1).append("§r§f名:§f").append(a.get(i));
            }
            rank.append("\n");
        }
        return rank.toString();
    }

    public String getChineseName(String gameName) {
        switch (gameName) {
            case "LightsOut":
                return "关灯";
            case "OneToOne":
                return "一一对应";
            case "Jigsaw":
                return "拼图";
            case "RemoveAll":
                return "方块消消乐";
            case "BlockPlay_4":
                return "方块华容道4*4";
            case "BlockPlay_3":
                return "方块华容道3*3";
            default:
                return null;
        }
    }

    public void checkRank(String gameName, int spendTime, String gamer) {
        ArrayList<String> a = new ArrayList<>((Collection<? extends String>) config.get(gameName));
        for (int i = 0; i < 3; i++) {
            String[] in = a.get(i).split("-");
            if (in[0].equals("000") || Integer.parseInt(in[0]) > spendTime) {
                a.set(i, spendTime + "-" + gamer);
                break;
            }
        }
        config.set(gameName,a);
        config.save();
    }
}

