package cn.createlight.cswitch.schedule;


import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.createlight.cswitch.room.Room;
import cn.createlight.cswitch.untils.Countdown;

import java.util.List;

/**
 * 正计时器
 */
public class RoomSchedule extends Task {
    private final Room room;
    private final int prepareTime; // 准备时间
    private int prepareTimeCountDown; // 准备时间倒计时
    private int spendTime = 0; // 游戏花费时间
    private final String gamingTipFormat;
    private final String finishTipFormat;

    public RoomSchedule(Room room) {
        this.room = room;
        this.prepareTime = Integer.parseInt((String) room.data.get("start_time"));
        this.prepareTimeCountDown = this.prepareTime;

        //TODO
        Config gameRuleConfig = new Config("FILE", Config.YAML);
        List<String> tipList = gameRuleConfig.getStringList("count-type-gaming");
        StringBuilder stringBuilder = new StringBuilder();
        for (String tip : tipList) {
            stringBuilder.append(tip).append("\n");
        }
        gamingTipFormat = stringBuilder.toString();

        tipList = gameRuleConfig.getStringList("count-type-finish");
        stringBuilder = new StringBuilder();
        for (String tip : tipList) {
            stringBuilder.append(tip).append("\n");
        }
        finishTipFormat = stringBuilder.toString();
    }

    @Override
    public void onRun(int tick) {
        //TODO 没有人时重新设置delay this.getHandler().setDelay(20);
        if (!this.room.isStarted) {
            if (this.room.gamePlayer != null) {
                --this.prepareTimeCountDown;
                this.room.gamePlayer.sendPopup(Countdown.countDown(prepareTimeCountDown));
                if (this.prepareTimeCountDown <= 0) {
                    this.room.startGame();
                    this.prepareTimeCountDown = this.prepareTime;
                }
            } else {
                this.prepareTimeCountDown = this.prepareTime;
            }
        } else {
            ++this.spendTime;
            if (this.room.gamePlayer == null) {
                this.room.stopGame();
                this.spendTime = 0;
            } else if (this.room.isFinished) {
                room.gamePlayer.sendMessage(finishTipFormat
                        .replace("{TIME}", String.valueOf(this.spendTime))
                        .replace("{SCORE}", String.valueOf(this.room.point)));
                // room.plugin.checkRank(room.gameType, spendTime, room.gamePlayer.getName());
                this.room.stopGame();
                this.spendTime = 0;
            } else {
                room.gamePlayer.sendPopup(gamingTipFormat
                        .replace("{TIME}", String.valueOf(this.spendTime))
                        .replace("{SCORE}", String.valueOf(this.room.point)));
            }
        }
    }

}