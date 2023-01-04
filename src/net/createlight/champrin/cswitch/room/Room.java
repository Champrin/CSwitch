package net.createlight.champrin.cswitch.room;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import net.createlight.champrin.cswitch.CSwitch;
import net.createlight.champrin.cswitch.game.*;
import net.createlight.champrin.cswitch.schedule.RoomSchedule;
import net.createlight.champrin.cswitch.schedule.RoomSchedule_2;
import net.createlight.champrin.cswitch.schedule.RoomSchedule_3;

import java.util.*;


public class Room implements Listener {

    public CSwitch plugin = CSwitch.getInstance();

    public String id; // 房间id

    public LinkedHashMap<String, Object> data; // 房间数据

    public Game gameTypeObject; // 游戏类型对象

    public CSwitch.GameType gameType; // 游戏类型名

    public Player gamePlayer = null; // 游戏玩家

    public ArrayList<String> playerBag = new ArrayList<>(); // 玩家背包保存

    public boolean isStarted = false; // 是否开始游戏

    public boolean isFinished = false; // 是否完成游戏

    public boolean isFailed = false; // 是否游戏失败 //TODO

    public int point = 0;

    public Level level; // 房间世界

    public int xMin, xMax, yMin, yMax, zMin, zMax; // 游戏区域

    //TODO
    public enum Direction {
        X_PLUS, // x+
        X_MINUS, // x-
        Z_PLUS,
        Z_MINUS,
    }

    public Direction direction; // 游戏区域方向

    public Room(String id) {
        this.id = id;
        this.data = RoomManager.roomsConfig.get(id);
        this.gameType = CSwitch.GameType.valueOf((String) data.get(RoomConfig.GAME_TYPE.toConfigKey()));

        String[] p1 = ((String) data.get("pos1")).split("\\+");
        String[] p2 = ((String) data.get("pos2")).split("\\+");

        this.direction = Direction.valueOf((String) data.get("direction"));
        this.level = this.plugin.getServer().getLevelByName((String) data.get("room_world"));
        //TODO 创建房间时就写好min和max
        this.xMin = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        this.xMax = (Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        this.yMin = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        this.yMax = (Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        this.zMin = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        this.zMax = (Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));

        switch (gameType) {
            case CRAZY_CLICK ->
                    this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule_2(this), 20);
            case QUICK_REACTION ->
                    this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule_3(this), 20);
            default -> this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule(this), 20);
        }

        setArenaFrame();

        this.gameTypeObject =
                switch (gameType) {
                    case LIGHTS_OUT -> new LightsOut(this);
                    case ONE_TO_ONE -> new OneToOne(this);
                    case JIGSAW -> new Jigsaw(this);
                    case THE_2048 -> new C2048(this);
                    case SUDOKU -> new Sudoku(this);
                    case REMOVE_ALL -> new RemoveAll(this);
                    case HANOI_TOWER -> new HanoiTower(this);
                    case MAKE_A_LINE -> new OnOneLine(this);
                    case N_PUZZLE -> new BlockPlay_4(this); // TODO
                    case AVOID_WHITE_BLOCK -> new AvoidWhiteBlock(this);
                    case CRAZY_CLICK -> new CrazyClick(this);
                    case QUICK_REACTION -> new BeFaster(this);
                    case CARD_MEMORY -> new CardMemory(this);
                    case GREEDY_SNAKE -> null;
                    case TETRIS -> null;
                };

        if (!((Boolean) data.get("arena"))) {
            gameTypeObject.buildArena();
        }
    }

    //这里使用了若水的保存物品NBT的方法
    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public void saveBag() {
        for (int i = 0; i < gamePlayer.getInventory().getSize() + 4; i++) {
            Item item = gamePlayer.getInventory().getItem(i);
            String nbt = "null";
            if (item.hasCompoundTag()) {
                nbt = bytesToHexString(item.getCompoundTag());
            }
            playerBag.add(item.getId() + "-" + item.getDamage() + "-" + item.getCount() + "-" + nbt);
        }
        gamePlayer.getInventory().clearAll();
    }

    public void loadBag() {
        gamePlayer.getInventory().clearAll();
        for (int i = 0; i < gamePlayer.getInventory().getSize() + 4; i++) {
            String[] a = playerBag.get(i).split("-");
            Item item = new Item(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
            if (!a[3].equals("null")) {
                CompoundTag tag = Item.parseCompoundTag(hexStringToBytes(a[3]));
                item.setNamedTag(tag);
            }
            gamePlayer.getInventory().setItem(i, item);
        }
        playerBag.clear();
    }

    public void setArenaFrame() {
        switch (gameType) {
            case SUDOKU -> {
                int a = 0, b = 0;
                switch (direction) {
                    case X_PLUS -> {
                        for (int y = yMin; y <= yMax; y++) {
                            for (int x = xMin; x <= xMax; x++) {
                                level.setBlock(new Vector3(x, y, zMin), Block.get(35, 15));
                            }
                        }
                        for (int y = yMax - 1; y >= yMin + 1; y--) {
                            ++b;
                            for (int x = xMin + 1; x <= zMax - 1; x++) {
                                level.setBlock(new Vector3(x, y, zMin), Block.get(0, 0));
                                ++a;
                                if (a == 3) {
                                    ++x;
                                    a = 0;
                                }
                            }
                            if (b == 3) {
                                --y;
                                b = 0;
                            }
                        }
                    }
                    case X_MINUS -> {
                        for (int y = yMin; y <= yMax; y++) {
                            for (int x = xMin; x <= xMax; x++) {
                                level.setBlock(new Vector3(x, y, zMin), Block.get(35, 15));
                            }
                        }
                        for (int y = yMax - 1; y >= yMin + 1; y--) {
                            ++b;
                            for (int x = xMax - 1; x >= xMin + 1; x--) {
                                level.setBlock(new Vector3(x, y, zMin), Block.get(0, 0));
                                ++a;
                                if (a == 3) {
                                    --x;
                                    a = 0;
                                }
                            }
                            if (b == 3) {
                                --y;
                                b = 0;
                            }
                        }
                    }
                    case Z_PLUS -> {
                        for (int y = yMin; y <= yMax; y++) {
                            for (int z = zMin; z <= zMax; z++) {
                                level.setBlock(new Vector3(xMin, y, z), Block.get(35, 15));
                            }
                        }
                        for (int y = yMax - 1; y >= yMin + 1; y--) {
                            ++b;
                            for (int z = zMin + 1; z <= zMax - 1; z++) {
                                level.setBlock(new Vector3(xMin, y, z), Block.get(0, 0));
                                ++a;
                                if (a == 3) {
                                    ++z;
                                    a = 0;
                                }
                            }
                            if (b == 3) {
                                --y;
                                b = 0;
                            }
                        }
                    }
                    case Z_MINUS -> {
                        for (int y = yMin; y <= yMax; y++) {
                            for (int z = zMin; z <= zMax; z++) {
                                level.setBlock(new Vector3(xMin, y, z), Block.get(35, 15));
                            }
                        }
                        for (int y = yMax - 1; y >= yMin + 1; y--) {
                            ++b;
                            for (int z = zMax - 1; z >= zMin + 1; z--) {
                                level.setBlock(new Vector3(xMin, y, z), Block.get(0, 0));
                                ++a;
                                if (a == 3) {
                                    --z;
                                    a = 0;
                                }
                            }
                            if (b == 3) {
                                --y;
                                b = 0;
                            }
                        }
                    }
                }
            }
            case CRAZY_CLICK -> level.setBlock(new Vector3(xMin, yMin, zMin), Block.get(57, 0));
            case HANOI_TOWER -> {
                int ma = 0;
                int aa = 1;
                switch (direction) {
                    case X_PLUS:
                    case X_MINUS:
                        for (int x = xMin; x <= xMax; x++) {
                            for (int y = yMin; y <= yMax; y++) {
                                level.setBlock(new Vector3(x, y, zMin), Block.get(35, ma));
                            }
                            if (aa == 1) {
                                ma = 15;
                                aa = 2;
                            } else {
                                ma = 0;
                                aa = 1;
                            }
                        }
                        break;
                    case Z_PLUS:
                    case Z_MINUS:
                        for (int z = zMin; z <= zMax; z++) {
                            for (int y = yMin; y <= yMax; y++) {
                                level.setBlock(new Vector3(xMin, y, z), Block.get(35, ma));
                            }
                            if (aa == 1) {
                                ma = 15;
                                aa = 2;
                            } else {
                                ma = 0;
                                aa = 1;
                            }
                        }
                        break;
                }
            }
            default -> {
                int id = 35, mate = 5;
                if (gameType == CSwitch.GameType.JIGSAW || gameType == CSwitch.GameType.N_PUZZLE || gameType == CSwitch.GameType.THE_2048) {
                    id = 20;
                    mate = 0;
                } else if (gameType == CSwitch.GameType.QUICK_REACTION || gameType == CSwitch.GameType.CARD_MEMORY) {
                    mate = 0;
                }
                switch (direction) {
                    case X_PLUS:
                    case X_MINUS:
                        for (int y = yMin; y <= yMax; y++) {
                            for (int x = xMin; x <= xMax; x++) {
                                level.setBlock(new Vector3(x, y, zMin), Block.get(id, mate));
                            }
                        }
                        break;
                    case Z_PLUS:
                    case Z_MINUS:
                        for (int y = yMin; y <= yMax; y++) {
                            for (int z = zMin; z <= zMax; z++) {
                                level.setBlock(new Vector3(xMin, y, z), Block.get(id, mate));
                            }
                        }
                        break;
                }
            }
        }
    }

    //TODO 加上判断level
    public boolean isInArena(Vector3 block) {
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));
        return x >= xMin && x <= xMax && y >= yMin && y <= yMax && z >= zMin && z <= zMax;
    }

    public boolean isInGame(Player player)//获取玩家当前状态
    {
        return gamePlayer == player;
    }

    public void addItem() {
        switch (gameType) {
            case ONE_TO_ONE:
                for (int i = 0; i <= 15; i++) {
                    this.gamePlayer.getInventory().setItem(i, Item.get(351, i));
                }
                break;
            case JIGSAW:
                this.gamePlayer.getInventory().addItem(Item.get(35, 0, 1));
                this.gamePlayer.getInventory().addItem(Item.get(42, 0, 1));
                this.gamePlayer.getInventory().addItem(Item.get(80, 0, 1));
                this.gamePlayer.getInventory().addItem(Item.get(155, 0, 1));
                this.gamePlayer.getInventory().addItem(Item.get(159, 0, 1));
                this.gamePlayer.getInventory().addItem(Item.get(24, 0, 1));
                this.gamePlayer.getInventory().addItem(Item.get(35, 4, 1));
                this.gamePlayer.getInventory().addItem(Item.get(159, 4, 1));
                this.gamePlayer.getInventory().addItem(Item.get(179, 0, 1));
                break;
            case SUDOKU:
                int a = 0;
                for (int i = 1; i <= 9; i++) {
                    this.gamePlayer.getInventory().setItem(a, Item.get(35, i, 64));
                    a = a + 1;
                }
                break;
            case THE_2048:
                Item item = Item.get(35, 0, 1);
                this.gamePlayer.getInventory().setItem(1, item.setCustomName(">>  §a上  <<"));
                item = Item.get(35, 1, 1);
                this.gamePlayer.getInventory().setItem(2, item.setCustomName(">>  §a下  <<"));
                item = Item.get(35, 2, 1);
                this.gamePlayer.getInventory().setItem(3, item.setCustomName(">>  §a左  <<"));
                item = Item.get(35, 3, 1);
                this.gamePlayer.getInventory().setItem(4, item.setCustomName(">>  §a右  <<"));
                break;
            case MAKE_A_LINE:
                this.gamePlayer.getInventory().setItem(1, (Item.get(Item.DOOR_BLOCK, 0, 1)).setCustomName(">>  §a我完成了  <<"));
                break;
            default:
                break;
        }
    }

    public void joinToRoom(Player player) {
        if (this.gamePlayer != null) {
            player.sendMessage("> 已经有玩家加入游戏了");
            return;
        }
        if (this.isInGame(player)) {
            player.sendMessage("> 你已经加入一个游戏了");
            return;
        }
        this.isFinished = false;
        this.gamePlayer = player;

        player.sendMessage(">  你加入了游戏,等待游戏开始");
        player.sendMessage(">  输入@hub可退出游戏！");
        if (gameType == CSwitch.GameType.MAKE_A_LINE) {
            player.sendMessage(">>  §c当你认为你已经不能再进行下一步时,请切换“门”物品以结束游戏！此游戏排行榜以分统计！");
            player.sendMessage(">>  §c当你认为你已经不能再进行下一步时,请切换“门”物品以结束游戏！此游戏排行榜以分统计！");
            player.sendMessage(">>  §c当你认为你已经不能再进行下一步时,请切换“门”物品以结束游戏！此游戏排行榜以分统计！");
        }
    }

    /**
     * 开始游戏时进行的操作
     */
    public void startGame() {
        this.isStarted = true;
        RoomManager.changeSignText(id);
        saveBag();
        addItem();
        gameTypeObject.buildOperation(false);
        gamePlayer.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, true);
        gamePlayer.getAdventureSettings().update();
    }

    /**
     * 停止游戏后进行的操作
     */
    public void stopGame() {
        this.isStarted = false;
        if (gamePlayer != null) {
            if (!playerBag.isEmpty()) loadBag();
            gamePlayer.sendMessage(">>>   游戏结束");
            gamePlayer.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
            gamePlayer.getAdventureSettings().update();
        }

        RoomManager.changeSignText(id);

        this.gamePlayer = null;
        this.isFinished = false;
        this.point = 0;
        this.setArenaFrame();
        gameTypeObject.buildArena();
        //TODO 记录玩家分数
    }

    /**
     * 服务器停止进行的操作
     */
    public void serverStop() {
        if (gamePlayer != null) {
            if (!playerBag.isEmpty()) loadBag();
            gamePlayer.sendMessage(">>>   游戏结束");
            gamePlayer.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
            gamePlayer.getAdventureSettings().update();
        }
        RoomManager.changeSignText(id);
    }

    /**
     * 玩家退出类事件
     **/
    @EventHandler
    @SuppressWarnings("unused")
    public void onQuit(PlayerQuitEvent event) {
        if (this.isInGame(event.getPlayer())) {
            this.stopGame();
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onKick(PlayerKickEvent event) {
        if (this.isInGame(event.getPlayer())) {
            this.stopGame();
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDeath(PlayerDeathEvent event) {
        if (this.isInGame(event.getEntity())) {
            this.stopGame();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onHit(EntityDamageEvent event) {
        Entity en = event.getEntity();
        if (en instanceof Player) {
            if (this.isInGame((Player) en)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 玩家操作类事件
     **/
    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isInGame(event.getPlayer())) {
            if (gameType != CSwitch.GameType.SUDOKU) event.setCancelled(true);
            if (!isStarted) event.setCancelled(true);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isInGame(event.getPlayer())) {
            if (gameType != CSwitch.GameType.SUDOKU) event.setCancelled(true);
            if (!isStarted) event.setCancelled(true);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDrop(PlayerDropItemEvent event) {
        if (this.isInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}