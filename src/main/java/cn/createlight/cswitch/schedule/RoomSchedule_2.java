package cn.createlight.cswitch.schedule;

import cn.createlight.cswitch.config.ConfigManager;
import cn.createlight.cswitch.config.LanguageConfigKey;
import cn.createlight.cswitch.utils.StringUtils;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.createlight.cswitch.room.Room;
import cn.createlight.cswitch.utils.Countdown;

import java.util.List;

/**
 * 倒计时器
 */
public class RoomSchedule_2 extends Task {
    private final Room room;
    private int prepareTimeCountDown;
    private int gameTimeCountDown;
    private final String gamingTipFormat;
    private final String finishTipFormat;

    public RoomSchedule_2(Room room) {
        this.room = room;
        this.prepareTimeCountDown = room.prepareTime;
        this.gameTimeCountDown = room.gameTime;

        gamingTipFormat = StringUtils.combineStringList(
                ConfigManager.getConfig(ConfigManager.ConfigName.GAME_TIP)
                        .getStringList(LanguageConfigKey.COUNTDOWN_TYPE_GAMING.toConfigKey())
        );
        finishTipFormat = StringUtils.combineStringList(
                ConfigManager.getConfig(ConfigManager.ConfigName.GAME_TIP)
                        .getStringList(LanguageConfigKey.COUNTDOWN_TYPE_FINISH.toConfigKey())
        );
    }

    @Override
    public void onRun(int tick) {
        if (!this.room.isStarted) {
            if (this.room.gamePlayer != null) {
                --this.prepareTimeCountDown;
                room.gamePlayer.sendPopup(Countdown.countDown(prepareTimeCountDown));
                if (this.prepareTimeCountDown <= 0) {
                    this.room.startGame();
                    this.prepareTimeCountDown = room.prepareTime;
                }
            } else {
                this.prepareTimeCountDown = room.prepareTime;
            }
        } else {
            --this.gameTimeCountDown;
            if (this.room.gamePlayer == null) {
                this.room.stopGame();
                this.gameTimeCountDown = room.gameTime;
            } else if (this.gameTimeCountDown <= 0) {
                room.gamePlayer.sendMessage(finishTipFormat
                        .replace("{SCORE}", String.valueOf(this.room.point)));
                //room.plugin.checkRank(room.gameType, room.rank, room.gamePlayer.getName());
                this.room.stopGame();
                this.gameTimeCountDown = room.gameTime;
            } else {
                room.gamePlayer.sendPopup(gamingTipFormat
                        .replace("{TIME}", String.valueOf(gameTimeCountDown))
                        .replace("{SCORE}", String.valueOf(this.room.point)));
            }
        }
    }

}