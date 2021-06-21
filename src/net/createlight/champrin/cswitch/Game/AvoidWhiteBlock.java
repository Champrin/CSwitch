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
    private int o, minY, maxY;
    private int width, length;

    public AvoidWhiteBlock(Room room) {
        super(room);
        this.count = (int) this.room.data.get("times");
        this.width = (int) this.room.data.get("width");
        this.length = (int) this.room.data.get("length");
        this.useTimes = count;
        getMaxAndMin();
    }

    public void getMaxAndMin() {
        switch (room.direction) {
            case "x+":
            case "x-":
                this.min = room.xi;
                this.max = room.xa;
                this.o = room.zi;
                break;
            case "z+":
            case "z-":
                this.min = room.zi;
                this.max = room.za;
                this.o = room.xi;
                break;
        }
        this.minY = room.ya;
        this.maxY = room.yi;
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.finish) return;
        if (this.game_type.equals("AvoidWhiteBlock")) {
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
            case "x+":
                for (int x = min; x <= max; x++) {
                    level.setBlock(new Vector3(x, (int) Math.round(Math.floor(block.y)), o), Block.get(0, 0));

                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int x = min; x <= max; x++) {
                        Block b = level.getBlock(x, y, o);
                        level.setBlock(new Vector3(x, y - 1, o), b);
                    }
                }
                if (useTimes < length) {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, o), Block.get(35, 5));
                    }
                } else {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, o), Block.get(35, 0));
                    }
                    level.setBlock(new Vector3(min + newW, maxY, o), Block.get(35, 15));
                }
                break;
            case "x-":
                for (int x = min; x <= max; x++) {
                    level.setBlock(new Vector3(x, (int) Math.round(Math.floor(block.y)), o), Block.get(0, 0));

                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int x = max; x >= min; x--) {
                        Block b = level.getBlock(x, y, o);
                        level.setBlock(new Vector3(x, y - 1, o), b);
                    }
                }
                if (useTimes < length) {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, o), Block.get(35, 5));
                    }
                } else {
                    for (int x = min; x <= max; x++) {
                        level.setBlock(new Vector3(x, maxY, o), Block.get(35, 0));
                    }
                    level.setBlock(new Vector3(min + newW, maxY, o), Block.get(35, 15));
                }
                break;
            case "z+":
                for (int z = min; z <= max; z++) {
                    level.setBlock(new Vector3(o, (int) Math.round(Math.floor(block.y)), z), Block.get(0, 0));
                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int z = min; z <= max; z++) {
                        Block b = level.getBlock(o, y, z);
                        level.setBlock(new Vector3(o, y - 1, z), b);
                    }
                }
                if (useTimes < length) {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(o, maxY, z), Block.get(35, 5));
                    }
                } else {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(o, maxY, z), Block.get(35, 0));
                    }
                    level.setBlock(new Vector3(o, maxY, min + newW), Block.get(35, 15));

                }
                break;
            case "z-":
                for (int z = min; z <= max; z++) {
                    level.setBlock(new Vector3(o, (int) Math.round(Math.floor(block.y)), z), Block.get(0, 0));
                }
                for (int y = minY + 1; y <= maxY; y++) {
                    for (int z = max; z >= min; z--) {
                        Block b = level.getBlock(o, y, z);
                        level.setBlock(new Vector3(o, y - 1, z), b);
                    }
                }
                if (useTimes < length) {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(o, maxY, z), Block.get(35, 5));
                    }
                } else {
                    for (int z = min; z <= max; z++) {
                        level.setBlock(new Vector3(o, maxY, z), Block.get(35, 0));
                    }
                    level.setBlock(new Vector3(o, maxY, min + newW), Block.get(35, 15));
                }
                break;
        }
    }

    @Override
    public void checkFinish() {
        if (this.room.rank >= this.count) {
            this.room.finish = true;
        }
    }

    @Override
    public void madeArena() {
        int width = (int) this.room.data.get("width");
        switch (room.direction) {
            case "x+":
            case "x-":
                for (int y = room.yi; y <= room.ya; y++) {
                    for (int x = room.xi; x <= room.xa; x++) {
                        room.level.setBlock(new Vector3(x, y, room.zi), Block.get(35, 0));
                    }
                }
                for (int y = room.yi; y <= room.ya; y++) {
                    int num = new Random().nextInt(width);
                    room.level.setBlock(new Vector3(room.xi + num, y, room.zi), Block.get(35, 15));
                }
                break;
            case "z+":
            case "z-":
                for (int y = room.yi; y <= room.ya; y++) {
                    for (int z = room.zi; z <= room.za; z++) {
                        room.level.setBlock(new Vector3(room.xi, y, z), Block.get(35, 0));
                    }
                }
                for (int y = room.yi; y <= room.ya; y++) {
                    int num = new Random().nextInt(width);
                    room.level.setBlock(new Vector3(room.xi, y, room.zi + num), Block.get(35, 15));
                }
                break;
        }
        finishBuild();
    }
}
