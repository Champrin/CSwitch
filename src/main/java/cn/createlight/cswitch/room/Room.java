package cn.createlight.cswitch.room;

import cn.createlight.cswitch.CSwitch;
import cn.createlight.cswitch.CSwitchGameType;
import cn.createlight.cswitch.game.*;
import cn.createlight.cswitch.schedule.RoomSchedule;
import cn.createlight.cswitch.schedule.RoomSchedule_2;
import cn.createlight.cswitch.schedule.RoomSchedule_3;
import cn.createlight.cswitch.utils.SavePlayerBag;
import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
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
import cn.nukkit.utils.Config;

import java.util.*;


public class Room implements Listener {

    public CSwitch plugin = CSwitch.getInstance();

    public String roomID; // 房间id

    // public LinkedHashMap<String, Object> data; // 房间数据

    public Game gameTypeObject; // 游戏类型对象

    public CSwitchGameType gameType; // 游戏类型名

    public Player gamePlayer = null; // 游戏玩家

    public ArrayList<String> playerBag; // 玩家背包保存

    public boolean isStarted = false; // 是否开始游戏

    public boolean isFinished = false; // 是否完成游戏

    public boolean isFailed = false; // 是否游戏失败 //TODO

    public int point = 0;

    public Level level; // 房间世界

    public int xMin, xMax, yMin, yMax, zMin, zMax; // 游戏区域

    public int width, length, area;

    public int prepareTime; // 准备时间
    public int gameTime;

    public ArrayList<Object> additions;

    //TODO
    public enum Direction {
        X_PLUS, // x+
        X_MINUS, // x-
        Z_PLUS,
        Z_MINUS,
    }

    public Direction direction; // 游戏区域方向

    public String[] arenaPoint1, arenaPoint2;

    public Room(String id) {
        this.roomID = id;
        // this.data = RoomManager.getRoomData(id);
        Config roomConfig = RoomManager.getRoomConfig(id);
        //TODO 检查GameType是不是按toString存的
        this.gameType = CSwitchGameType.valueOf(roomConfig.getString(RoomConfigKey.GAME_TYPE.toConfigKey()));

        this.arenaPoint1 = roomConfig.getString(RoomConfigKey.ARENA_POINT1.toConfigKey()).split(",");
        this.arenaPoint2 = roomConfig.getString(RoomConfigKey.ARENA_POINT2.toConfigKey()).split(",");

        this.level = this.plugin.getServer().getLevelByName(roomConfig.getString(RoomConfigKey.ROOM_WORLD.toConfigKey()));
        //TODO 创建房间时就写好min和max
        this.xMin = (Math.min(Integer.parseInt(arenaPoint1[0]), Integer.parseInt(arenaPoint2[0])));
        this.xMax = (Math.max(Integer.parseInt(arenaPoint1[0]), Integer.parseInt(arenaPoint2[0])));
        this.yMin = (Math.min(Integer.parseInt(arenaPoint1[1]), Integer.parseInt(arenaPoint2[1])));
        this.yMax = (Math.max(Integer.parseInt(arenaPoint1[1]), Integer.parseInt(arenaPoint2[1])));
        this.zMin = (Math.min(Integer.parseInt(arenaPoint1[2]), Integer.parseInt(arenaPoint2[2])));
        this.zMax = (Math.max(Integer.parseInt(arenaPoint1[2]), Integer.parseInt(arenaPoint2[2])));

        this.area = roomConfig.getInt(RoomConfigKey.AREA.toConfigKey());
        this.width = roomConfig.getInt(RoomConfigKey.WIDTH.toConfigKey());
        this.length = roomConfig.getInt(RoomConfigKey.LENGTH.toConfigKey());
        this.direction = Direction.valueOf(roomConfig.getString(RoomConfigKey.DIRECTION.toConfigKey()));

        this.prepareTime = roomConfig.getInt(RoomConfigKey.PREPARE_TIME.toConfigKey());
        this.gameTime = roomConfig.getInt(RoomConfigKey.GAME_TIME.toConfigKey());

        this.additions = new ArrayList<Object>(roomConfig.getList(RoomConfigKey.ADDITION.toConfigKey()));

        switch (gameType) {
            case CRAZY_CLICK:
                this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule_2(this), 20);
                break;
            case QUICK_REACTION:
                this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule_3(this), 20);
                break;
            default:
                this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule(this), 20);
                break;
        }

        switch (gameType) {
            case LIGHTS_OUT:
                this.gameTypeObject = new LightsOut(this);
                break;
            case ONE_TO_ONE:
                this.gameTypeObject = new OneToOne(this);
                break;
            case JIGSAW:
                this.gameTypeObject = new Jigsaw(this);
                break;
            case THE_2048:
                this.gameTypeObject = new The2048(this);
                break;
            case SUDOKU:
                this.gameTypeObject = new Sudoku(this);
                break;
            case REMOVE_ALL:
                this.gameTypeObject = new RemoveAll(this);
                break;
            case HANOI_TOWER:
                this.gameTypeObject = new HanoiTower(this);
                break;
            case MAKE_A_LINE:
                this.gameTypeObject = new OnOneLine(this);
                break;
            case N_PUZZLE:
                this.gameTypeObject = new BlockPlay_4(this); // TODO
                break;
            case AVOID_WHITE_BLOCK:
                this.gameTypeObject = new AvoidWhiteBlock(this);
                break;
            case CRAZY_CLICK:
                this.gameTypeObject = new CrazyClick(this);
                break;
            case QUICK_REACTION:
                this.gameTypeObject = new QuickReaction(this);
                break;
            case CARD_MEMORY:
                this.gameTypeObject = new CardMemory(this);
                break;
            case GREEDY_SNAKE:
                this.gameTypeObject = null;
                break;
            case TETRIS:
                this.gameTypeObject = null;
        }

        if (!roomConfig.getBoolean(RoomConfigKey.BUILD_FINISH.toConfigKey())) {
            gameTypeObject.buildArena();
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
        if (gameType == CSwitchGameType.MAKE_A_LINE) {
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
        RoomManager.changeSignText(roomID);
        playerBag = SavePlayerBag.saveBag(gamePlayer);
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
            SavePlayerBag.loadBag(gamePlayer, playerBag);
            gamePlayer.sendMessage(">>>   游戏结束");
            gamePlayer.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
            gamePlayer.getAdventureSettings().update();
        }

        RoomManager.changeSignText(roomID);

        this.gamePlayer = null;
        this.isFinished = false;
        this.point = 0;
        gameTypeObject.buildArena();
        //TODO 记录玩家分数
    }

    /**
     * 服务器停止进行的操作
     */
    public void serverStop() {
        if (gamePlayer != null) {
            SavePlayerBag.loadBag(gamePlayer, playerBag);
            gamePlayer.sendMessage(">>>   游戏结束");
            gamePlayer.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
            gamePlayer.getAdventureSettings().update();
        }
        RoomManager.changeSignText(roomID);
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
            if (gameType != CSwitchGameType.SUDOKU) event.setCancelled(true);
            if (!isStarted) event.setCancelled(true);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isInGame(event.getPlayer())) {
            if (gameType != CSwitchGameType.SUDOKU) event.setCancelled(true);
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