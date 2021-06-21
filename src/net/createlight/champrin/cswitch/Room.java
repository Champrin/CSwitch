package net.createlight.champrin.cswitch;

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
import net.createlight.champrin.cswitch.Game.*;
import net.createlight.champrin.cswitch.schedule.RoomSchedule;
import net.createlight.champrin.cswitch.schedule.RoomSchedule_2;
import net.createlight.champrin.cswitch.schedule.RoomSchedule_3;

import java.util.*;


public class Room implements Listener {

    public CSwitch plugin;

    public String id;
    public String game_type;
    public LinkedHashMap<String, Object> data;
    public Game gameType;

    public Player gamePlayer = null;
    public ArrayList<String> playerBag = new ArrayList<>();

    public int game = 0;
    public boolean finish = false;
    public int rank = 0;

    public Level level;
    public int xi, xa, yi, ya, zi, za;

    public String direction;

    public Room(String id, CSwitch plugin) {
        this.plugin = plugin;
        this.id = id;
        this.data = plugin.roomsConfig.get(id);
        this.game_type = (String) data.get("game_type");

        String[] p1 = ((String) data.get("pos1")).split("\\+");
        String[] p2 = ((String) data.get("pos2")).split("\\+");

        this.direction = (String) data.get("direction");
        this.level = this.plugin.getServer().getLevelByName((String) data.get("room_world"));
        this.xi = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        this.xa = (Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        this.yi = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        this.ya = (Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        this.zi = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        this.za = (Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));

        switch (game_type) {
            case "CrazyClick":
                this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule_2(this), 20);
                break;
            case "BeFaster":
                this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule_3(this), 20);
                break;
            default:
                this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule(this), 20);
                break;
        }

        setArenaFrame();

        switch (game_type) {
            case "LightsOut":
                this.gameType = new LightsOut(this);
                break;
            case "OneToOne":
                this.gameType = new OneToOne(this);
                break;
            case "Jigsaw":
                this.gameType = new Jigsaw(this);
                break;
            case "C2048":
                this.gameType = new C2048(this);
                break;
            case "Sudoku":
                this.gameType = new Sudoku(this);
                break;
            case "RemoveAll":
                this.gameType = new RemoveAll(this);
                break;
            case "HanoiTower":
                this.gameType = new HanoiTower(this);
                break;
            case "OnOneLine":
                this.gameType = new OnOneLine(this);
                break;
            case "BlockPlay_4":
                this.gameType = new BlockPlay_4(this);
                break;
            case "BlockPlay_3":
                this.gameType = new BlockPlay_3(this);
                break;
            case "AvoidWhiteBlock":
                this.gameType = new AvoidWhiteBlock(this);
                break;
            case "CrazyClick":
                this.gameType = new CrazyClick(this);
                break;
            case "BeFaster":
                this.gameType = new BeFaster(this);
                break;
            case "CardMemory":
                this.gameType = new CardMemory(this);
                break;
        }
        if (!((Boolean) data.get("arena"))) {
            gameType.madeArena();
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
        switch (game_type) {
            case "Sudoku":
                int a = 0, b = 0;
                switch (direction) {
                    case "x+":
                        for (int y = yi; y <= ya; y++) {
                            for (int x = xi; x <= xa; x++) {
                                level.setBlock(new Vector3(x, y, zi), Block.get(35, 15));
                            }
                        }
                        for (int y = ya - 1; y >= yi + 1; y--) {
                            ++b;
                            for (int x = xi + 1; x <= za - 1; x++) {
                                level.setBlock(new Vector3(x, y, zi), Block.get(0, 0));
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
                        break;
                    case "x-":
                        for (int y = yi; y <= ya; y++) {
                            for (int x = xi; x <= xa; x++) {
                                level.setBlock(new Vector3(x, y, zi), Block.get(35, 15));
                            }
                        }
                        for (int y = ya - 1; y >= yi + 1; y--) {
                            ++b;
                            for (int x = xa - 1; x >= xi + 1; x--) {
                                level.setBlock(new Vector3(x, y, zi), Block.get(0, 0));
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
                        break;
                    case "z+":
                        for (int y = yi; y <= ya; y++) {
                            for (int z = zi; z <= za; z++) {
                                level.setBlock(new Vector3(xi, y, z), Block.get(35, 15));
                            }
                        }
                        for (int y = ya - 1; y >= yi + 1; y--) {
                            ++b;
                            for (int z = zi + 1; z <= za - 1; z++) {
                                level.setBlock(new Vector3(xi, y, z), Block.get(0, 0));
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
                        break;
                    case "z-":
                        for (int y = yi; y <= ya; y++) {
                            for (int z = zi; z <= za; z++) {
                                level.setBlock(new Vector3(xi, y, z), Block.get(35, 15));
                            }
                        }
                        for (int y = ya - 1; y >= yi + 1; y--) {
                            ++b;
                            for (int z = za - 1; z >= zi + 1; z--) {
                                level.setBlock(new Vector3(xi, y, z), Block.get(0, 0));
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
                        break;
                }
                break;
            case "CrazyClick":
                level.setBlock(new Vector3(xi, yi, zi), Block.get(57, 0));
                break;
            case "HanoiTower":
                int ma = 0;
                int aa = 1;
                switch (direction) {
                    case "x+":
                    case "x-":
                        for (int x = xi; x <= xa; x++) {
                            for (int y = yi; y <= ya; y++) {
                                level.setBlock(new Vector3(x, y, zi), Block.get(35, ma));
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
                    case "z+":
                    case "z-":
                        for (int z = zi; z <= za; z++) {
                            for (int y = yi; y <= ya; y++) {
                                level.setBlock(new Vector3(xi, y, z), Block.get(35, ma));
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
                break;
            default:
                int id = 35, mate = 5;
                if (game_type.equals("Jigsaw") || game_type.equals("BlockPlay_4") || game_type.equals("BlockPlay_3") || game_type.equals("C2048")) {
                    id = 20;
                    mate = 0;
                } else if (game_type.equals("BeFaster")|| game_type.equals("CardMemory") ) {
                    mate = 0;
                }

                switch (direction) {
                    case "x+":
                    case "x-":
                        for (int y = yi; y <= ya; y++) {
                            for (int x = xi; x <= xa; x++) {
                                level.setBlock(new Vector3(x, y, zi), Block.get(id, mate));
                            }
                        }
                        break;
                    case "z+":
                    case "z-":
                        for (int y = yi; y <= ya; y++) {
                            for (int z = zi; z <= za; z++) {
                                level.setBlock(new Vector3(xi, y, z), Block.get(id, mate));
                            }
                        }
                        break;
                }
                break;
        }
    }

    public boolean isInArena(Block block) {
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));
        return x >= xi && x <= xa && y >= yi && y <= ya && z >= zi && z <= za;
    }

    public boolean isInGame(Player p)//获取玩家当前状态
    {
        return gamePlayer == p;
    }

    public void addItem() {
        switch (game_type) {
            case "OneToOne":
                for (int i = 0; i <= 15; i++) {
                    this.gamePlayer.getInventory().setItem(i, Item.get(351, i));
                }
                break;
            case "Jigsaw":
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
            case "Sudoku":
                int a = 0;
                for (int i = 1; i <= 9; i++) {
                    this.gamePlayer.getInventory().setItem(a, Item.get(35, i, 64));
                    a = a + 1;
                }
                break;
            case "C2048":
                Item item = Item.get(35, 0, 1);
                this.gamePlayer.getInventory().setItem(1, item.setCustomName(">>  §a上  <<"));
                item = Item.get(35, 1, 1);
                this.gamePlayer.getInventory().setItem(2, item.setCustomName(">>  §a下  <<"));
                item = Item.get(35, 2, 1);
                this.gamePlayer.getInventory().setItem(3, item.setCustomName(">>  §a左  <<"));
                item = Item.get(35, 3, 1);
                this.gamePlayer.getInventory().setItem(4, item.setCustomName(">>  §a右  <<"));
                break;
            case "OnOneLine":
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
        this.finish = false;
        this.gamePlayer = player;

        player.sendMessage(">  你加入了游戏,等待游戏开始");
        player.sendMessage(">  输入@hub可退出游戏！");
        if (game_type.equals("OnOneLine")) {
            player.sendMessage(">>  §c当你认为你已经不能再进行下一步时,请切换“门”物品以结束游戏！此游戏排行榜以分统计！");
            player.sendMessage(">>  §c当你认为你已经不能再进行下一步时,请切换“门”物品以结束游戏！此游戏排行榜以分统计！");
            player.sendMessage(">>  §c当你认为你已经不能再进行下一步时,请切换“门”物品以结束游戏！此游戏排行榜以分统计！");
        }
    }

    public void startGame() {
        this.game = 1;
        plugin.changeSign(id);
        saveBag();
        addItem();
        gameType.useBuild();
        gamePlayer.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, true);
        gamePlayer.getAdventureSettings().update();
    }

    public void stopGame() {
        this.game = 0;
        if (gamePlayer != null) {
            if (!playerBag.isEmpty()) loadBag();
            gamePlayer.sendMessage(">>>   游戏结束");
            gamePlayer.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
            gamePlayer.getAdventureSettings().update();
        }

        plugin.changeSign(id);

        this.gamePlayer = null;
        this.finish = false;
        this.rank = 0;
        this.setArenaFrame();
        gameType.madeArena();
    }
    public void serverStop() {
        if (gamePlayer != null) {
            if (!playerBag.isEmpty()) loadBag();
            gamePlayer.sendMessage(">>>   游戏结束");
            gamePlayer.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
            gamePlayer.getAdventureSettings().update();
        }
        plugin.changeSign(id);
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
            if (!game_type.equals("Sudoku")) event.setCancelled(true);
            if (game != 1) event.setCancelled(true);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isInGame(event.getPlayer())) {
            if (!game_type.equals("Sudoku")) event.setCancelled(true);
            if (game != 1) event.setCancelled(true);
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