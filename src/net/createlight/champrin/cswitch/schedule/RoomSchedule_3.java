package net.createlight.champrin.cswitch.schedule;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import net.createlight.champrin.cswitch.Room;
import net.createlight.champrin.cswitch.untils.Countdown;
import net.createlight.champrin.cswitch.untils.TimeBlockElement;

import java.util.Random;

public class RoomSchedule_3 extends Task {

    private int startTime, spendTime, gameTime;
    private Room room;
    private int xi, xa, yi, ya, zi, za;
    private int grade = 60;//毫秒
    private Level level;

    public RoomSchedule_3(Room room) {
        this.room = room;
        this.startTime = Integer.parseInt((String) room.data.get("start_time")) + 1;
        this.gameTime = (int) room.data.get("game_time");
        this.spendTime = gameTime;
        this.level = room.level;
        this.xi = room.xi;
        this.xa = room.xa;
        this.yi = room.yi;
        this.ya = room.ya;
        this.zi = room.zi;
        this.za = room.za;
    }

    @Override
    public void onRun(int tick) {
        if (this.room.game == 0) {
            if (this.room.gamePlayer != null) {
                this.startTime = startTime - 1;
                Player p = room.gamePlayer;
                p.sendPopup(new Countdown().countDown(startTime));
                if (this.startTime <= 0) {
                    this.room.startGame();
                    this.spendTime = (int) room.data.get("game_time");
                    this.startTime = Integer.parseInt((String) this.room.data.get("start_time")) + 1;
                }
            } else {
                this.startTime = Integer.parseInt((String) this.room.data.get("start_time")) + 1;
                this.spendTime = (int) room.data.get("game_time");
            }
        }
        if (this.room.game == 1) {
            this.spendTime = spendTime - 1;
            sendTarget();
            if (spendTime <= gameTime * 0.8) {
                sendTarget();
                this.grade = 50;
            } else if (spendTime <= gameTime * 0.5) {
                sendTarget();
                sendTarget();
                this.grade = 40;
            } else if (spendTime <= gameTime * 0.2) {
                sendTarget();
                sendTarget();
                sendTarget();
                this.grade = 20;
            }

            if (this.room.gamePlayer == null) {
                this.room.stopGame();
                this.spendTime = (int) room.data.get("game_time");
            } else if (this.spendTime <= 0) {
                room.gamePlayer.sendMessage("§f=======================");
                room.gamePlayer.sendMessage(">>  §f你的得分: §6§l" + this.room.rank);
                room.gamePlayer.sendMessage("§f=======================");
                room.plugin.checkRank(room.game_type, room.rank, room.gamePlayer.getName());
                this.room.stopGame();
                this.spendTime = (int) room.data.get("game_time");
            } else {
                room.gamePlayer.sendPopup(room.game_type + ">> §a§lRemaining time:§c" + spendTime + "s  §eYour point:§6" + this.room.rank);
            }
        }
    }

    public Vector3 getRandPosVector3()//在游戏区域内随机获取坐标
    {
        int x = xi;
        int z = zi;
        int y = new Random().nextInt(ya - yi + 1) + yi;
        switch (room.direction) {
            case "x+":
            case "x-":
                x = new Random().nextInt(xa - xi + 1) + xi;
                break;
            case "z+":
            case "z-":
                z = new Random().nextInt(za - zi + 1) + zi;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + room.direction);
        }
        return new Vector3(x, y, z);
    }

    public void sendTarget() {
        Block block = level.getBlock(getRandPosVector3());
        level.setBlock(block, Block.get(35, 14));
        this.room.plugin.getServer().getScheduler().scheduleRepeatingTask(new TimeBlockElement(grade, block), 1);
    }


}