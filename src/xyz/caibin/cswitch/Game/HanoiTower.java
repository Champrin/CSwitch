package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import javafx.geometry.Pos;
import xyz.caibin.cswitch.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HanoiTower extends Game implements Listener {

    public HanoiTower(Room plugin) {
        this.plugin = plugin;
        this.game_type = plugin.game_type;
    }

    private Block click;

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("HanoiTower")) {
            if (this.plugin.game != 1) return;
            Player player = event.getPlayer();
            if (this.plugin.isInGame(player)) {
                Block block = event.getBlock();
                if (block.getDamage() == 15) return;
                int x = (int) Math.round(Math.floor(block.x));
                int y = (int) Math.round(Math.floor(block.y));
                int z = (int) Math.round(Math.floor(block.z));
                int[] pos = {x, y, z};
                if (this.plugin.isInArena(pos)) {
                    event.setCancelled(true);
                    if (click == null) {
                        this.click = block;
                    } else {
                        updateBlock(block);
                        this.click = null;
                    }
                }
            }
        }
    }

    public void updateBlock(Block block) {
        Level level = block.getLevel();
        Block b = level.getBlock(new Vector3(block.x, plugin.yi, block.z));
        if (level.getBlock(new Vector3(click.x, click.y + 1, click.z)).getDamage() != 0) {
            plugin.gamePlayer.sendMessage(">  操作错误");
            return;
        }
        if (b.getDamage() == 0) {
            level.setBlock(click, Block.get(35, 0));
            level.setBlock(b, Block.get(35, click.getDamage()));
        } else if (b.getDamage() != 0) {
            Block b1 = level.getBlock(new Vector3(block.x, plugin.yi + 1, block.z));
            if (b1.getDamage() == 0) {
                if (b.getDamage() > click.getDamage()) {
                    level.setBlock(click, Block.get(35, 0));
                    level.setBlock(b1, Block.get(35, click.getDamage()));
                }else {
                    plugin.gamePlayer.sendMessage(">  操作错误");
                    return;
                }
            } else if (b1.getDamage() != 0) {
                Block b2 = level.getBlock(new Vector3(block.x, plugin.yi + 2, block.z));
                if (b2.getDamage() == 0) {
                    if (b1.getDamage() > click.getDamage()) {
                        level.setBlock(click, Block.get(35, 0));
                        level.setBlock(b2, Block.get(35, click.getDamage()));
                    }
                }else {
                    plugin.gamePlayer.sendMessage(">  操作错误");
                    return;
                }
            } else {
                plugin.gamePlayer.sendMessage(">  操作错误");
                return;
            }
        } else {
            plugin.gamePlayer.sendMessage(">  操作错误");
            return;
        }
        switch (plugin.direction) {
            case "x+":
            case "z-":
                if (level.getBlock(new Vector3(plugin.xa, plugin.yi, plugin.zi)).getDamage() == 3) {
                    if (level.getBlock(new Vector3(plugin.xa, plugin.yi + 1, plugin.zi)).getDamage() == 2) {
                        if (level.getBlock(new Vector3(plugin.xa, plugin.ya, plugin.zi)).getDamage() == 1) {
                            this.plugin.rank = 3;
                        }
                    }
                }
                break;
            case "z+":
            case "x-":
                if (level.getBlock(new Vector3(plugin.xi, plugin.yi, plugin.za)).getDamage() == 3) {
                    if (level.getBlock(new Vector3(plugin.xi, plugin.yi + 1, plugin.za)).getDamage() == 2) {
                        if (level.getBlock(new Vector3(plugin.xi, plugin.ya, plugin.za)).getDamage() == 1) {
                            this.plugin.rank = 3;
                        }
                    }
                }
                break;
        }
        checkFinish();
    }

    public void checkFinish() {
        if (this.plugin.rank >= 3) {
            this.plugin.finish = true;
        }
    }
}
