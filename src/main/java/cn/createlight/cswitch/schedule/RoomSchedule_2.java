package cn.createlight.cswitch.schedule;

import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.createlight.cswitch.room.Room;
import cn.createlight.cswitch.untils.Countdown;

import java.util.List;

/**
 * 倒计时器
 */
public class RoomSchedule_2 extends Task {
    private final Room room;
    private final int prepareTime; // 准备时间
    private final int spendTime; // 准备时间
    private int prepareTimeCountDown;
    private int spendTimeCountDown;
    private final String gamingTipFormat;
    private final String finishTipFormat;

    public RoomSchedule_2(Room room) {
        this.room = room;
        this.prepareTime = Integer.parseInt((String) room.data.get("start_time"));
        this.prepareTimeCountDown = this.prepareTime;
        this.spendTime = (int) room.data.get("game_time");
        this.spendTimeCountDown = this.spendTime;

        //TODO
        Config gameRuleConfig = new Config("FILE", Config.YAML);
        List<String> tipList = gameRuleConfig.getStringList("countdown-type-gaming");
        StringBuilder stringBuilder = new StringBuilder();
        for (String tip : tipList) {
            stringBuilder.append(tip).append("\n");
        }
        gamingTipFormat = stringBuilder.toString();

        tipList = gameRuleConfig.getStringList("countdown-type-finish");
        stringBuilder = new StringBuilder();
        for (String tip : tipList) {
            stringBuilder.append(tip).append("\n");
        }
        finishTipFormat = stringBuilder.toString();
    }

    @Override
    public void onRun(int tick) {
        if (!this.room.isStarted) {
            if (this.room.gamePlayer != null) {
                --this.prepareTimeCountDown;
                room.gamePlayer.sendPopup(Countdown.countDown(prepareTimeCountDown));
                if (this.prepareTimeCountDown <= 0) {
                    this.room.startGame();
                    this.prepareTimeCountDown = this.prepareTime;
                }
            } else {
                this.prepareTimeCountDown = this.prepareTime;
            }
        } else {
            --this.spendTimeCountDown;
            if (this.room.gamePlayer == null) {
                this.room.stopGame();
                this.spendTimeCountDown = this.spendTime;
            } else if (this.spendTimeCountDown <= 0) {
                room.gamePlayer.sendMessage(finishTipFormat
                        .replace("{SCORE}", String.valueOf(this.room.point)));
                //room.plugin.checkRank(room.gameType, room.rank, room.gamePlayer.getName());
                this.room.stopGame();
                this.spendTimeCountDown = this.spendTime;
            } else {
                room.gamePlayer.sendPopup(gamingTipFormat
                        .replace("{TIME}", String.valueOf(spendTimeCountDown))
                        .replace("{SCORE}", String.valueOf(this.room.point)));
            }
        }
    }

}