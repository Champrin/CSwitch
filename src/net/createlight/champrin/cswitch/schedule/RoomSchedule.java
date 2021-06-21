package net.createlight.champrin.cswitch.schedule;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import net.createlight.champrin.cswitch.Room;
import net.createlight.champrin.cswitch.untils.Countdown;

public class RoomSchedule extends Task {

    private int startTime, spendTime = 0;
    private Room room;

    public RoomSchedule(Room room) {
        this.room = room;
        this.startTime = Integer.parseInt((String) room.data.get("start_time")) + 1;
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
                    this.spendTime = 0;
                    this.startTime = Integer.parseInt((String) this.room.data.get("start_time")) + 1;
                }
            } else {
                this.startTime = Integer.parseInt((String) this.room.data.get("start_time")) + 1;
                this.spendTime = 0;
            }
        }
        if (this.room.game == 1) {
            this.spendTime = spendTime + 1;
            if (this.room.gamePlayer == null) {
                this.room.stopGame();
                this.spendTime = 0;
            } else if (this.room.finish) {
                room.gamePlayer.sendMessage("§f=======================");
                room.gamePlayer.sendMessage(">>  §a完成游戏所用时间: §6§l" + spendTime);
                room.gamePlayer.sendMessage("§f=======================");
                room.plugin.checkRank(room.game_type, spendTime, room.gamePlayer.getName());
                this.room.stopGame();
                this.spendTime = 0;
            } else {
                room.gamePlayer.sendPopup(room.game_type + ">> §a§lElapsed time:§c" + spendTime + "s  §eYour point:§6" + this.room.rank);
            }
        }
    }

}