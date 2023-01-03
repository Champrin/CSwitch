package net.createlight.champrin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.Room;

import java.util.Random;

public class AvoidWhiteBlock extends Game {

    private int useTimes;
    private int max, min;
    private int axis, minY, maxY;

    public AvoidWhiteBlock(Room room) {
        super(room);
        this.count = (int) this.room.data.get("times");
        this.useTimes = count;
        getMaxAndMin();
    }

    public void getMaxAndMin() {
        switch (room.direction) {
            case "x+", "x-" -> {
                this.min = room.xMin;
                this.max = room.xMax;
                this.axis = room.zMin;
            }
            case "z+", "z-" -> {
                this.min = room.zMin;
                this.max = room.zMax;
                this.axis = room.xMin;
            }
        }
        this.minY = room.yMax;
        this.maxY = room.yMin;
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.isFinished) return;
        if (this.gameTypeName.equals("AvoidWhiteBlock")) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                if (this.room.isInArena(block)) {
                    if (block.getDamage() == 15) {
                        event.setCancelled(true);
                        if ((int) Math.round(Math.floor(block.y)) != minY) return;
                        this.room.rank = room.rank + 1;
                        checkFinish();
                        updateBlock(block);
                    } else {
                        this.room.gamePlayer.sendMessage(">>  游戏失败");
                        this.room.gamePlayer = null;
                    }
                }
            }
        }
    }

    public void updateBlock(Block block) {
        Level level = block.level;
        this.useTimes = useTimes - 1;
        int newW = new Random().nextInt(width);
        switch (room.direction) {
            case "x+" -> {
                for (int x = min; x <= max; x++) {
                    level.setBlock(new Vector3(x, (int) Math.round(Math.floor(block.y)), axis), airBlock);

                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int x = min; x <= max; x++) {
                        Block b = level.getBlock(x, y, axis);
                        level.setBlock(new Vector3(x, y - 1, axis), b);
                    }
                }
                if (useTimes < length) {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, axis), greenWoolBlock);
                    }
                } else {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, axis), whiteWoolBlock);
                    }
                    level.setBlock(new Vector3(min + newW, maxY, axis), blackWoolBlock);
                }
            }
            case "x-" -> {
                for (int x = min; x <= max; x++) {
                    level.setBlock(new Vector3(x, (int) Math.round(Math.floor(block.y)), axis), airBlock);

                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int x = max; x >= min; x--) {
                        Block b = level.getBlock(x, y, axis);
                        level.setBlock(new Vector3(x, y - 1, axis), b);
                    }
                }
                if (useTimes < length) {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, axis), greenWoolBlock);
                    }
                } else {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, axis), whiteWoolBlock);
                    }
                    level.setBlock(new Vector3(min + newW, maxY, axis), blackWoolBlock);
                }
            }
            case "z+" -> {
                for (int z = min; z <= max; z++) {
                    level.setBlock(new Vector3(this.axis, (int) Math.round(Math.floor(block.y)), z), airBlock);
                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int z = min; z <= max; z++) {
                        Block b = level.getBlock(this.axis, y, z);
                        level.setBlock(new Vector3(this.axis, y - 1, z), b);
                    }
                }
                if (useTimes < length) {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(this.axis, maxY, z), greenWoolBlock);
                    }
                } else {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(this.axis, maxY, z), whiteWoolBlock);
                    }
                    level.setBlock(new Vector3(axis, maxY, min + newW), blackWoolBlock);

                }
            }
            case "z-" -> {
                for (int z = min; z <= max; z++) {
                    level.setBlock(new Vector3(this.axis, (int) Math.round(Math.floor(block.y)), z), airBlock);
                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int z = max; z >= min; z--) {
                        Block b = level.getBlock(this.axis, y, z);
                        level.setBlock(new Vector3(this.axis, y - 1, z), b);
                    }
                }
                if (useTimes < length) {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(this.axis, maxY, z), greenWoolBlock);
                    }
                } else {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(this.axis, maxY, z), whiteWoolBlock);
                    }
                    level.setBlock(new Vector3(axis, maxY, min + newW), blackWoolBlock);
                }
            }
        }
    }

    @Override
    public void checkFinish() {
        if (this.room.rank >= this.count) {
            this.room.isFinished = true;
        }
    }

    @Override
    public void buildArena() {
        switch (room.direction) {
            case "x+", "x-" -> {
                for (int y = room.yMin; y <= room.yMax; y++) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        room.level.setBlock(new Vector3(x, y, room.zMin), whiteWoolBlock);
                    }
                    int x = new Random().nextInt(width);
                    room.level.setBlock(new Vector3(room.xMin + x, y, room.zMin), blackWoolBlock);
                }
            }
            case "z+", "z-" -> {
                for (int y = room.yMin; y <= room.yMax; y++) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        room.level.setBlock(new Vector3(room.xMin, y, z), whiteWoolBlock);
                    }
                    int z = new Random().nextInt(width);
                    room.level.setBlock(new Vector3(room.xMin, y, room.zMin + z), blackWoolBlock);
                }
            }
        }
        buildOperation(true);
    }
}
