package cn.createlight.cswitch.schedule;


import cn.createlight.cswitch.config.ConfigManager;
import cn.createlight.cswitch.config.LanguageConfigKey;
import cn.createlight.cswitch.utils.StringUtils;
import cn.nukkit.scheduler.Task;
import cn.createlight.cswitch.room.Room;
import cn.createlight.cswitch.utils.Countdown;

import java.util.List;

/**
 * 正计时器
 */
public class RoomSchedule extends Task {
    private final Room room;
    private int prepareTimeCountDown; // 准备时间倒计时
    private int spendTime = 0; // 游戏已花费时间
    private final String gamingTipFormat; // 循环的任务，将需要的字符串提前存储，提升效率
    private final String finishTipFormat;

    public RoomSchedule(Room room) {
        this.room = room;
        this.prepareTimeCountDown = room.prepareTime;

        gamingTipFormat = StringUtils.combineStringList(
                ConfigManager.getConfig(ConfigManager.ConfigName.GAME_TIP)
                        .getStringList(LanguageConfigKey.COUNT_TYPE_GAMING.toConfigKey())
        );
        finishTipFormat = StringUtils.combineStringList(
                ConfigManager.getConfig(ConfigManager.ConfigName.GAME_TIP)
                        .getStringList(LanguageConfigKey.COUNT_TYPE_FINISH.toConfigKey())
        );
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
                    this.prepareTimeCountDown = room.prepareTime;
                }
            } else {
                this.prepareTimeCountDown = room.prepareTime;
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