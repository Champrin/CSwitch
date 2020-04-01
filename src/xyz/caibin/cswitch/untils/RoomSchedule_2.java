package xyz.caibin.cswitch.untils;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import xyz.caibin.cswitch.Room;

public class RoomSchedule_2 extends Task {

    private int startTime, spendTime;
    private String game_type;
    private Room arena;

    public RoomSchedule_2(Room arena) {
        this.arena = arena;
        this.startTime = Integer.parseInt((String) arena.data.get("start_time")) + 1;
        this.game_type = (String) arena.data.get("game_type");
        this.spendTime = (int) arena.data.get("game_time");
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
                arena.gamePlayer.sendPopup(game_type + ">> §a§lRemaining time:§c" + spendTime + "s  §eYour point:§6" + this.arena.rank);
            }
        }
    }

}