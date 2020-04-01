package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import xyz.caibin.cswitch.Room;

import java.util.Random;

public class AvoidWhiteBlock extends Game {

    private int useTimes;
    private int max, min;
    private int o, minY, maxY;
    private int width, length;

    public AvoidWhiteBlock(Room plugin) {
        this.plugin = plugin;
        this.game_type = plugin.game_type;
        this.count = (int) this.plugin.data.get("times");
        this.width = (int) this.plugin.data.get("width");
        this.length = (int) this.plugin.data.get("length");
        this.useTimes = count;
        this.area = (int) this.plugin.data.get("area");
        getMaxAndMin();
    }

    public void getMaxAndMin() {
        switch (plugin.direction) {
            case "x+":
            case "x-":
                this.min = plugin.xi;
                this.max = plugin.xa;
                this.o = plugin.zi;
                break;
            case "z+":
            case "z-":
                this.min = plugin.zi;
                this.max = plugin.za;
                this.o = plugin.xi;
                break;
        }
        this.minY = plugin.ya;
        this.maxY = plugin.yi;
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("AvoidWhiteBlock")) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            int x = (int) Math.round(Math.floor(block.x));
            int y = (int) Math.round(Math.floor(block.y));
            int z = (int) Math.round(Math.floor(block.z));
            int[] pos = {x, y, z};
            if (this.plugin.isInGame(player)) {
                if (this.plugin.isInArena(pos)) {
                    if (block.getDamage() == 15) {
                        event.setCancelled(true);
                        if ((int) Math.round(Math.floor(block.y)) != minY) return;
                        this.plugin.rank = plugin.rank + 1;
                        checkFinish();
                        updateBlock(block);
                    } else {
                        this.plugin.gamePlayer.sendMessage(">>  游戏失败");
                        this.plugin.gamePlayer = null;
                    }
                }
            }
        }
    }

    public void updateBlock(Block block) {
        Level level = block.level;
        this.useTimes = useTimes - 1;
        int newW = new Random().nextInt(width);
        switch (plugin.direction) {
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
        if (this.plugin.rank >= this.count) {
            this.plugin.finish = true;
        }
    }
}
