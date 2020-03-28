package xyz.caibin.cswitch.untils;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import xyz.caibin.cswitch.Room;
import xyz.caibin.cswitch.untils.Countdown;

public class RoomSchedule extends Task {

    private int startTime, spendTime = 0;
    private String game_type;
    private Room arena;

    public RoomSchedule(Room arena) {
        this.arena = arena;
        this.startTime = Integer.parseInt((String) arena.data.get("start_time"))+1;
        this.game_type = (String) arena.data.get("game_type");

    }

    @Override
    public void onRun(int tick) {
        if (this.arena.game == 0) {
            if (this.arena.gamePlayer != null) {
                this.startTime=startTime-1;
                Player p = arena.gamePlayer;
                p.sendPopup(new Countdown().countDown(startTime));
                if (this.startTime <= 0) {
                    this.arena.startGame();
                    this.spendTime = 0;
                    this.startTime = Integer.parseInt((String) this.arena.data.get("start_time"))+1;
                }
            } else {
                this.startTime = Integer.parseInt((String) this.arena.data.get("start_time"))+1;
                this.spendTime = 0;
            }
        }
        if (this.arena.game == 1) {
            this.spendTime=spendTime+1;
            arena.gamePlayer.sendPopup(game_type + ">> §a§lElapsed time:§c" + spendTime +"s  §eYour point:§6"+this.arena.rank);
            if (this.arena.finish && this.arena.gamePlayer != null) {
                arena.gamePlayer.sendMessage("§f=======================");
                arena.gamePlayer.sendMessage(">>  §a完成游戏所用时间: §6§l"+spendTime);
                arena.gamePlayer.sendMessage(">>  §f你的得分: §6§l" +this.arena.rank);
                arena.gamePlayer.sendMessage("§f=======================");
                arena.plugin.checkRank(game_type,spendTime,arena.gamePlayer.getName());
                this.arena.stopGame();
                this.spendTime = 0;
            }
            if (this.arena.gamePlayer == null ) {
                this.arena.stopGame();
                this.spendTime = 0;
            }
        }
    }

}