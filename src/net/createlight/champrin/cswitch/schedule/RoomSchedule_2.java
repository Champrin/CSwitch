package net.createlight.champrin.cswitch.schedule;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import net.createlight.champrin.cswitch.Room;
import net.createlight.champrin.cswitch.untils.Countdown;

public class RoomSchedule_2 extends Task {

    private int startTime, spendTime;
    private final Room room;

    public RoomSchedule_2(Room room) {
        this.room = room;
        this.startTime = Integer.parseInt((String) room.data.get("start_time")) + 1;
        this.spendTime = (int) room.data.get("game_time");
    }

    @Override
    public void onRun(int tick) {
        if (!this.room.isStarted) {
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
        if (this.room.isStarted) {
            this.spendTime = spendTime - 1;
            if (this.room.gamePlayer == null) {
                this.room.stopGame();
                this.spendTime = (int) room.data.get("game_time");
            } else if (this.spendTime <= 0) {
                room.gamePlayer.sendMessage("§f=======================");
                room.gamePlayer.sendMessage(">>  §f你的得分: §6§l" + this.room.rank);
                room.gamePlayer.sendMessage("§f=======================");
                room.plugin.checkRank(room.gameTypeName, room.rank, room.gamePlayer.getName());
                this.room.stopGame();
                this.spendTime = (int) room.data.get("game_time");
            } else {
                room.gamePlayer.sendPopup(room.gameTypeName + ">> §a§lRemaining time:§c" + spendTime + "s  §eYour point:§6" + this.room.rank);
            }
        }
    }

}