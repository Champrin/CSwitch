package cn.createlight.cswitch.game;

import cn.createlight.cswitch.CSwitchGameType;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.createlight.cswitch.room.Room;

import java.util.Random;

public class AvoidWhiteBlock extends Game {

    private int useTimes;
    private int max, min;
    private int axis, minY, maxY;

    public AvoidWhiteBlock(Room room) {
        super(room);
        this.count = (int) room.additions.get(0);
        this.useTimes = count;
        getMaxAndMin();
    }

    public void getMaxAndMin() {
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                this.min = room.xMin;
                this.max = room.xMax;
                this.axis = room.zMin;
                break;
            case Z_PLUS:
            case Z_MINUS:
                this.min = room.zMin;
                this.max = room.zMax;
                this.axis = room.xMin;
                break;
        }
        this.minY = room.yMax;
        this.maxY = room.yMin;
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 房间游戏条件限制
        if (this.room.gameType != CSwitchGameType.AVOID_WHITE_BLOCK) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        Block block = event.getBlock();
        if (!this.room.isInArena(block)) return;

        if (block.getDamage() == 15) {
            event.setCancelled(true);
            if ((int) Math.round(Math.floor(block.y)) != minY) return;
            this.room.point = room.point + 1;
            checkFinish();
            updateBlock(block);
        } else {
            this.room.gamePlayer.sendMessage(">>  游戏失败");
            this.room.gamePlayer = null;
        }
    }

    public void updateBlock(Block block) {
        Level level = block.level;
        this.useTimes = useTimes - 1;
        int newW = new Random().nextInt(room.width);
        switch (room.direction) {
            case X_PLUS:
                for (int x = min; x <= max; x++) {
                    level.setBlock(new Vector3(x, (int) Math.round(Math.floor(block.y)), axis), airBlock);

                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int x = min; x <= max; x++) {
                        Block b = level.getBlock(x, y, axis);
                        level.setBlock(new Vector3(x, y - 1, axis), b);
                    }
                }
                if (useTimes < room.length) {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, axis), greenWoolBlock);
                    }
                } else {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, axis), whiteWoolBlock);
                    }
                    level.setBlock(new Vector3(min + newW, maxY, axis), blackWoolBlock);
                }
                break;
            case X_MINUS:
                for (int x = min; x <= max; x++) {
                    level.setBlock(new Vector3(x, (int) Math.round(Math.floor(block.y)), axis), airBlock);

                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int x = max; x >= min; x--) {
                        Block b = level.getBlock(x, y, axis);
                        level.setBlock(new Vector3(x, y - 1, axis), b);
                    }
                }
                if (useTimes < room.length) {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, axis), greenWoolBlock);
                    }
                } else {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, axis), whiteWoolBlock);
                    }
                    level.setBlock(new Vector3(min + newW, maxY, axis), blackWoolBlock);
                }
                break;
            case Z_PLUS:
                for (int z = min; z <= max; z++) {
                    level.setBlock(new Vector3(this.axis, (int) Math.round(Math.floor(block.y)), z), airBlock);
                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int z = min; z <= max; z++) {
                        Block b = level.getBlock(this.axis, y, z);
                        level.setBlock(new Vector3(this.axis, y - 1, z), b);
                    }
                }
                if (useTimes < room.length) {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(this.axis, maxY, z), greenWoolBlock);
                    }
                } else {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(this.axis, maxY, z), whiteWoolBlock);
                    }
                    level.setBlock(new Vector3(axis, maxY, min + newW), blackWoolBlock);

                }
                break;
            case Z_MINUS:
                for (int z = min; z <= max; z++) {
                    level.setBlock(new Vector3(this.axis, (int) Math.round(Math.floor(block.y)), z), airBlock);
                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int z = max; z >= min; z--) {
                        Block b = level.getBlock(this.axis, y, z);
                        level.setBlock(new Vector3(this.axis, y - 1, z), b);
                    }
                }
                if (useTimes < room.length) {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(this.axis, maxY, z), greenWoolBlock);
                    }
                } else {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(this.axis, maxY, z), whiteWoolBlock);
                    }
                    level.setBlock(new Vector3(axis, maxY, min + newW), blackWoolBlock);
                }
                break;
        }
    }

    @Override
    public void checkFinish() {
        if (this.room.point >= this.count) {
            this.room.isFinished = true;
        }
    }

    @Override
    public void buildArena() {
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                for (int y = room.yMin; y <= room.yMax; y++) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        room.level.setBlock(new Vector3(x, y, room.zMin), whiteWoolBlock);
                    }
                    int x = new Random().nextInt(room.width);
                    room.level.setBlock(new Vector3(room.xMin + x, y, room.zMin), blackWoolBlock);
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int y = room.yMin; y <= room.yMax; y++) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        room.level.setBlock(new Vector3(room.xMin, y, z), whiteWoolBlock);
                    }
                    int z = new Random().nextInt(room.width);
                    room.level.setBlock(new Vector3(room.xMin, y, room.zMin + z), blackWoolBlock);
                    break;
                }

        }

        buildOperation(true);
    }
}
