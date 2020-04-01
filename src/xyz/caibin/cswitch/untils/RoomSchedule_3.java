package xyz.caibin.cswitch.untils;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import xyz.caibin.cswitch.Room;

import java.util.Random;

public class RoomSchedule_3 extends Task {

    private int startTime, spendTime;
    private String game_type;
    private Room arena;
    private int xi, xa, yi, ya, zi, za, area;
    private Level level;

    public RoomSchedule_3(Room arena) {
        this.arena = arena;
        this.startTime = Integer.parseInt((String) arena.data.get("start_time")) + 1;
        this.game_type = (String) arena.data.get("game_type");
        this.spendTime = (int) arena.data.get("game_time");
        this.level = arena.plugin.getServer().getLevelByName((String) arena.data.get("room_world"));
        String[] p1 = ((String) arena.data.get("pos1")).split("\\+");
        String[] p2 = ((String) arena.data.get("pos2")).split("\\+");
        this.xi = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        this.xa = (Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        this.yi = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        this.ya = (Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        this.zi = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        this.za = (Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
    }

    @Override
    public void onRun(int tick) {
        if (this.arena.game == 0) {
            if (this.arena.gamePlayer != null) {
                this.startTime = startTime - 1;
                Player p = arena.gamePlayer;
                p.sendPopup(new Countdown().countDown(startTime));
                if (this.startTime <= 0) {
                    this.arena.startGame();
                    this.spendTime = (int) arena.data.get("game_time");
                    this.startTime = Integer.parseInt((String) this.arena.data.get("start_time")) + 1;
                }
            } else {
                this.startTime = Integer.parseInt((String) this.arena.data.get("start_time")) + 1;
                this.spendTime = (int) arena.data.get("game_time");
            }
        }
        if (this.arena.game == 1) {
            this.spendTime = spendTime - 1;
            if (this.arena.gamePlayer == null) {
                this.arena.stopGame();
                this.spendTime = (int) arena.data.get("game_time");
            }
            if (this.spendTime <= 0 && this.arena.gamePlayer != null) {
                arena.gamePlayer.sendMessage("§f=======================");
                arena.gamePlayer.sendMessage(">>  §f你的得分: §6§l" + this.arena.rank);
                arena.gamePlayer.sendMessage("§f=======================");
                arena.plugin.checkRank(game_type, arena.rank, arena.gamePlayer.getName());
                this.arena.stopGame();
                this.spendTime = (int) arena.data.get("game_time");
            }
            if (this.arena.gamePlayer != null) {
                RedAlert();
                arena.gamePlayer.sendPopup(game_type + ">> §a§lRemaining time:§c" + spendTime + "s  §eYour point:§6" + this.arena.rank);
            }
        }
    }

    public String getRandPos()//在游戏区域内随机获取坐标
    {
        int x = xi;
        int z = zi;
        int y = new Random().nextInt(ya - yi + 1) + yi;

        if (zi - za != 0) {
            z = new Random().nextInt(za - zi + 1) + zi;
        }
        if (xi - xa != 0) {
            x = new Random().nextInt(xa - xi + 1) + xi;
        }
        return x + "+" + y + "+" + z;
    }

    public void RedAlert() {
        String[] p1 = getRandPos().split("\\+");
        Block block = level.getBlock(new Vector3(Integer.parseInt(p1[0]), Integer.parseInt(p1[1]), Integer.parseInt(p1[2])));
        int mate = block.getDamage();
        if (mate == 0) {
           level.setBlock(block, Block.get(35, 14));
        } else if (mate == 14) {
            level.setBlock(block, Block.get(35, 0));
        }
    }


}