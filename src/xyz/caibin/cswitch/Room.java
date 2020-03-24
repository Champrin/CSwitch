package xyz.caibin.cswitch;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.math.Vector3;
import xyz.caibin.cswitch.Game.*;

import java.util.*;


public class Room implements Listener {

    public CSwitch plugin;
    public String id;
    public String game_type;
    public LinkedHashMap<String, Object> data;

    public Player gamePlayer = null;
    public int game = 0;
    public boolean finish = false;
    public int rank = 0;

    public Room(String id, CSwitch plugin) {
        this.plugin = plugin;
        this.id = id;
        this.data = plugin.rooms_message.get(id);
        this.game_type = (String) plugin.rooms_message.get(id).get("game_type");
        this.plugin.getServer().getScheduler().scheduleRepeatingTask(new RoomSchedule(this), 20);
        registerEvent();

    }

    public void setGameArena() {
        int id = 35, mate = 5;
        if (game_type.equals("Jigsaw") || game_type.equals("BlockPlay_4") || game_type.equals("BlockPlay_3") ) {
            id = 20;
            mate = 0;
        }
        String direction = (String) data.get("direction");

        String[] p1 = ((String) data.get("pos1")).split("\\+");
        String[] p2 = ((String) data.get("pos2")).split("\\+");
        int xi = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int xa = (Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int yi = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int ya = (Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int zi = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        int za = (Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));

        Level level = this.plugin.getServer().getLevelByName((String) data.get("room_world"));

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

    }

    public boolean isInArena(int[] pos) {
        String[] p1 = ((String) data.get("pos1")).split("\\+");
        String[] p2 = ((String) data.get("pos2")).split("\\+");
        int xi = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int xa = (Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int yi = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int ya = (Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int zi = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        int za = (Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        return pos[0] >= xi && pos[0] <= xa && pos[1] >= yi && pos[1] <= ya && pos[2] >= zi && pos[2] <= za;
    }

    public boolean isInGame(Player p)//获取玩家当前状态
    {
        return gamePlayer == p;
    }

    public void joinToRoom(Player p) {
        if (this.gamePlayer != null) {
            p.sendMessage("> 已经有玩家加入游戏了");
            return;
        }
        if (this.isInGame(p)) {
            p.sendMessage("> 你已经加入一个游戏了");
            return;
        }
        this.finish = false;
        this.setGameArena();
        this.gamePlayer = p;
        p.sendMessage(">  你加入了游戏,等待游戏开始");
        p.sendMessage(">  输入@hub可退出游戏！");
    }

    public void setStartArena() {
        switch (this.game_type) {
            case "LightsOut":
                setStartArena_LightsOut();
                break;
            case "OneToOne":
                setStartArena_OneToOne();
                break;
            case "Jigsaw":
                setStartArena_Jigsaw();
                break;
            case "RemoveAll":
                setStartArena_RemoveAll();
                break;
            case "BlockPlay_4":
                setStartArena_BlockPlay_4();
                break;
            case "BlockPlay_3":
                setStartArena_BlockPlay_3();
                break;
        }
    }

    public Jigsaw jigsaw;

    public void registerEvent() {
        switch (this.game_type) {
            case "LightsOut":
                new LightsOut(this);
                break;
            case "OneToOne":
                new OneToOne(this);
                break;
            case "Jigsaw":
                new Jigsaw(this);
                break;
            case "RemoveAll":
                new RemoveAll(this);
                break;
            case "BlockPlay_4":
                new BlockPlay_4(this);
                break;
            case "BlockPlay_3":
                new BlockPlay_3(this);
                break;
        }
    }

    public void setStartArena_BlockPlay_4() {
        ArrayList<Integer> layout = new ArrayList<>(Arrays.asList(14, 1, 4, 5, 13, 9, 3, 11, 10, 8, 7, 6, 15, 0, 12));
        Collections.shuffle(layout);

        String direction = (String) this.data.get("direction");
        String[] p1 = ((String) this.data.get("pos1")).split("\\+");
        String[] p2 = ((String) this.data.get("pos2")).split("\\+");
        Level level = this.plugin.getServer().getLevelByName((String) data.get("room_world"));
        int a = 0;
        switch (direction) {
            case "x+": {
                int z = Integer.parseInt(p1[2]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int x = Integer.parseInt(p1[0]); x <= Integer.parseInt(p2[0]); x++) {
                        if (a == 15) break;
                        Vector3 v3 = new Vector3(x, y, z);
                        int mate = layout.get(a);
                        level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;
                    }
                }
                break;
            }
            case "x-": {
                int z = Integer.parseInt(p1[2]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int x = Integer.parseInt(p1[0]); x >= Integer.parseInt(p2[0]); x--) {
                        if (a == 15) break;
                        Vector3 v3 = new Vector3(x, y, z);
                        int mate = layout.get(a);
                        level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;
                    }
                }
                break;
            }
            case "z+": {
                int x = Integer.parseInt(p1[0]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int z = Integer.parseInt(p1[2]); z <= Integer.parseInt(p2[2]); z++) {
                        if (a == 15) break;
                        Vector3 v3 = new Vector3(x, y, z);
                        int mate = layout.get(a);
                        level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;

                    }
                }
                break;
            }
            case "z-": {
                int x = Integer.parseInt(p1[0]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int z = Integer.parseInt(p1[2]); z >= Integer.parseInt(p2[2]); z--) {
                        if (a == 15) break;
                        Vector3 v3 = new Vector3(x, y, z);
                        int mate = layout.get(a);
                        level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;
                    }
                }
                break;
            }
        }
    }

    public void setStartArena_BlockPlay_3() {
        ArrayList<Integer> layout = new ArrayList<>(Arrays.asList(14, 1, 4, 5, 13, 9, 3, 11));
        Collections.shuffle(layout);

        String direction = (String) this.data.get("direction");
        String[] p1 = ((String) this.data.get("pos1")).split("\\+");
        String[] p2 = ((String) this.data.get("pos2")).split("\\+");
        Level level = this.plugin.getServer().getLevelByName((String) data.get("room_world"));
        int a = 0;
        switch (direction) {
            case "x+": {
                int z = Integer.parseInt(p1[2]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int x = Integer.parseInt(p1[0]); x <= Integer.parseInt(p2[0]); x++) {
                        if (a == 8) break;
                        Vector3 v3 = new Vector3(x, y, z);
                        int mate = layout.get(a);
                        level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;
                    }
                }
                break;
            }
            case "x-": {
                int z = Integer.parseInt(p1[2]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int x = Integer.parseInt(p1[0]); x >= Integer.parseInt(p2[0]); x--) {
                        if (a == 8) break;
                        Vector3 v3 = new Vector3(x, y, z);
                        int mate = layout.get(a);
                        level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;
                    }
                }
                break;
            }
            case "z+": {
                int x = Integer.parseInt(p1[0]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int z = Integer.parseInt(p1[2]); z <= Integer.parseInt(p2[2]); z++) {
                        if (a == 8) break;
                        Vector3 v3 = new Vector3(x, y, z);
                        int mate = layout.get(a);
                        level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;

                    }
                }
                break;
            }
            case "z-": {
                int x = Integer.parseInt(p1[0]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int z = Integer.parseInt(p1[2]); z >= Integer.parseInt(p2[2]); z--) {
                        if (a == 8) break;
                        Vector3 v3 = new Vector3(x, y, z);
                        int mate = layout.get(a);
                        level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;
                    }
                }
                break;
            }
        }
    }

    public void setStartArena_OneToOne() {
        String direction = (String) data.get("direction");
        String[] p1 = ((String) data.get("pos1")).split("\\+");
        String[] p2 = ((String) data.get("pos2")).split("\\+");
        int xi = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int xa = (Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int yi = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int ya = (Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int zi = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        int za = (Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        Level level = this.plugin.getServer().getLevelByName((String) data.get("room_world"));
        switch (direction) {
            case "x+":
            case "x-":
                for (int x = xi; x <= xa; x++) {
                    for (int y = yi; y <= ya; y++) {
                        int num = new Random().nextInt(16);
                        Block block = Block.get(35, num);
                        level.setBlock(new Vector3(x, y, Integer.parseInt(p1[2])), block);
                    }
                }
                break;
            case "z+":
            case "z-":
                for (int z = zi; z <= za; z++) {
                    for (int y = yi; y <= ya; y++) {
                        int num = new Random().nextInt(16);
                        Block block = Block.get(35, num);
                        level.setBlock(new Vector3(Integer.parseInt(p1[0]), y, z), block);
                    }
                }
                break;
        }
        for (int i = 0; i <= 15; i++) {
            this.gamePlayer.getInventory().setItem(i, Item.get(351, i));
        }
    }

    public void setStartArena_Jigsaw() {
        int b = 0;
        for (String i : jigsaw.layout) {
            String[] item = i.split("-");
            this.gamePlayer.getInventory().setItem(b, Item.get(Integer.parseInt(item[0]), Integer.parseInt(item[1])));
            b = b + 1;
        }
        jigsaw.shuffleLayout();
        jigsaw.setRightPlace();
        jigsaw.setBlock();
    }

    public void setStartArena_RemoveAll() {
        String direction = (String) data.get("direction");
        String[] p1 = ((String) data.get("pos1")).split("\\+");
        String[] p2 = ((String) data.get("pos2")).split("\\+");
        int xi = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int xa = (Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int yi = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int ya = (Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int zi = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        int za = (Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        Level level = this.plugin.getServer().getLevelByName((String) data.get("room_world"));

        switch (direction) {
            case "x+":
            case "x-":
                for (int x = xi; x <= xa; x++) {
                    for (int y = yi; y <= ya; y++) {
                        int num = new Random().nextInt(5) + 4;
                        Block block = Block.get(159, num);
                        level.setBlock(new Vector3(x, y, Integer.parseInt(p1[2])), block);
                    }
                }
                break;
            case "z+":
            case "z-":
                for (int z = zi; z <= za; z++) {
                    for (int y = yi; y <= ya; y++) {
                        int num = new Random().nextInt(5) + 4;
                        Block block = Block.get(159, num);
                        level.setBlock(new Vector3(Integer.parseInt(p1[0]), y, z), block);
                    }
                }
                break;
        }
    }

    public void setStartArena_LightsOut() {

        String direction = (String) data.get("direction");
        String[] p1 = ((String) data.get("pos1")).split("\\+");
        String[] p2 = ((String) data.get("pos2")).split("\\+");
        int xi = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int xa = (Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int yi = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int ya = (Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int zi = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        int za = (Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        Level level = this.plugin.getServer().getLevelByName((String) data.get("room_world"));

        switch (direction) {
            case "x+":
            case "x-":
                for (int x = xi; x <= xa; x++) {
                    for (int y = yi; y <= ya; y++) {
                        int num = new Random().nextInt(2);
                        int mate = (num == 1 ? 5 : 15);
                        if (mate == 5) {
                            this.rank = rank + 1;
                        }
                        Block block = Block.get(35, mate);
                        level.setBlock(new Vector3(x, y, Integer.parseInt(p1[2])), block);
                    }
                }
                break;
            case "z+":
            case "z-":
                for (int z = zi; z <= za; z++) {
                    for (int y = yi; y <= ya; y++) {
                        int num = new Random().nextInt(2);
                        int mate = (num == 1 ? 5 : 15);
                        if (mate == 5) {
                            this.rank = rank + 1;
                        }
                        Block block = Block.get(35, mate);
                        level.setBlock(new Vector3(Integer.parseInt(p1[0]), y, z), block);
                    }
                }
                break;
        }

    }

    public void startGame() {
        this.game = 1;
        setStartArena();
    }

    public void stopGame() {
        this.game = 0;
//TODO
        if (gamePlayer != null) {
            gamePlayer.sendMessage(">>>   游戏结束");
            if (game_type.equals("Jigsaw")) {
                for (int i = 0; i < 9; i++) {
                    gamePlayer.getInventory().clear(i);
                }
            } else if (game_type.equals("OneToOne")) {
                for (int i = 0; i < 16; i++) {
                    gamePlayer.getInventory().clear(i);
                }
            }
        }
        this.gamePlayer = null;
        this.rank = 0;

        this.setGameArena();
    }

    /**
     * 玩家退出类事件
     **/
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (this.isInGame(event.getPlayer())) {
            this.stopGame();
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (this.isInGame(event.getPlayer())) {
            this.stopGame();
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (this.isInGame(event.getEntity())) {
            this.stopGame();
        }
    }

    /**
     * 玩家操作类事件
     **/
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (this.isInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}