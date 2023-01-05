package cn.createlight.cswitch.schedule;


import cn.createlight.cswitch.config.ConfigManager;
import cn.createlight.cswitch.config.LanguageConfigKey;
import cn.createlight.cswitch.utils.StringUtils;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import cn.createlight.cswitch.room.Room;
import cn.createlight.cswitch.utils.Countdown;
import cn.createlight.cswitch.utils.TimeBlockElement;
import cn.nukkit.utils.Config;

import java.util.List;
import java.util.Random;

public class RoomSchedule_3 extends Task {
    private final Room room;
    private int prepareTimeCountDown;
    private int gameTimeCountDown;
    private final String gamingTipFormat;
    private final String finishTipFormat;

    private final Level level;
    private final int xMin, xMax;
    private final int yMin, yMax;
    private final int zMin, zMax;
    private int grade = 60; // 难度等级，毫秒，值越低越难


    public RoomSchedule_3(Room room) {
        this.room = room;
        this.prepareTimeCountDown = room.prepareTime;
        this.gameTimeCountDown = room.gameTime;
        this.level = room.level;
        this.xMin = room.xMin;
        this.xMax = room.xMax;
        this.yMin = room.yMin;
        this.yMax = room.yMax;
        this.zMin = room.zMin;
        this.zMax = room.zMax;

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
            sendTarget();
            if (gameTimeCountDown <= gameTimeCountDown * 0.8) {
                sendTarget();
                this.grade = 50;
            } else if (gameTimeCountDown <= gameTimeCountDown * 0.5) {
                sendTarget();
                sendTarget();
                this.grade = 40;
            } else if (gameTimeCountDown <= gameTimeCountDown * 0.2) {
                sendTarget();
                sendTarget();
                sendTarget();
                this.grade = 20;
            }

            if (this.room.gamePlayer == null) {
                this.room.stopGame();
                this.gameTimeCountDown = room.gameTime;
            } else if (this.gameTimeCountDown <= 0) {
                room.gamePlayer.sendMessage(finishTipFormat
                        .replace("{SCORE}", String.valueOf(this.room.point)));
                // room.plugin.checkRank(room.gameType, room.rank, room.gamePlayer.getName());
                this.room.stopGame();
                this.gameTimeCountDown = room.gameTime;
            } else {
                room.gamePlayer.sendPopup(gamingTipFormat
                        .replace("{TIME}", String.valueOf(gameTimeCountDown))
                        .replace("{SCORE}", String.valueOf(this.room.point)));
            }
        }
    }

    private Vector3 getRandPosVector3()//在游戏区域内随机获取坐标
    {
        int x = xMin;
        int z = zMin;
        int y = new Random().nextInt(yMax - yMin + 1) + yMin;
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                x = new Random().nextInt(xMax - xMin + 1) + xMin;
                break;
            case Z_PLUS:
            case Z_MINUS:
                z = new Random().nextInt(zMax - zMin + 1) + zMin;
                break;
        }
        return new Vector3(x, y, z);
    }

    private void sendTarget() {
        Block block = level.getBlock(getRandPosVector3());
        level.setBlock(block, Block.get(35, 14));
        this.room.plugin.getServer().getScheduler().scheduleRepeatingTask(new TimeBlockElement(grade, block), 1);
    }


}