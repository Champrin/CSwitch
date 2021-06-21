package net.createlight.champrin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.Room;

public class HanoiTower extends Game implements Listener {

    public HanoiTower(Room room) {
        super(room);
    }

    private Block click;

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.finish) return;
        if (this.game_type.equals("HanoiTower")) {
            if (this.room.game != 1) return;
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                Block block = event.getBlock();
                if (block.getDamage() == 15) return;
                if (this.room.isInArena(block)) {
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
        Block b = level.getBlock(new Vector3(block.x, room.yi, block.z));
        if (level.getBlock(new Vector3(click.x, click.y + 1, click.z)).getDamage() != 0) {
            room.gamePlayer.sendMessage(">  操作错误");
            return;
        }
        if (b.getDamage() == 0) {
            level.setBlock(click, Block.get(35, 0));
            level.setBlock(b, Block.get(35, click.getDamage()));
        } else if (b.getDamage() != 0) {
            Block b1 = level.getBlock(new Vector3(block.x, room.yi + 1, block.z));
            if (b1.getDamage() == 0) {
                if (b.getDamage() > click.getDamage()) {
                    level.setBlock(click, Block.get(35, 0));
                    level.setBlock(b1, Block.get(35, click.getDamage()));
                } else {
                    room.gamePlayer.sendMessage(">  操作错误");
                    return;
                }
            } else if (b1.getDamage() != 0) {
                Block b2 = level.getBlock(new Vector3(block.x, room.yi + 2, block.z));
                if (b2.getDamage() == 0) {
                    if (b1.getDamage() > click.getDamage()) {
                        level.setBlock(click, Block.get(35, 0));
                        level.setBlock(b2, Block.get(35, click.getDamage()));
                    }
                } else {
                    room.gamePlayer.sendMessage(">  操作错误");
                    return;
                }
            } else {
                room.gamePlayer.sendMessage(">  操作错误");
                return;
            }
        } else {
            room.gamePlayer.sendMessage(">  操作错误");
            return;
        }
        switch (room.direction) {
            case "x+":
            case "z-":
                if (level.getBlock(new Vector3(room.xa, room.yi, room.zi)).getDamage() == 3) {
                    if (level.getBlock(new Vector3(room.xa, room.yi + 1, room.zi)).getDamage() == 2) {
                        if (level.getBlock(new Vector3(room.xa, room.ya, room.zi)).getDamage() == 1) {
                            this.room.rank = 3;
                        }
                    }
                }
                break;
            case "z+":
            case "x-":
                if (level.getBlock(new Vector3(room.xi, room.yi, room.za)).getDamage() == 3) {
                    if (level.getBlock(new Vector3(room.xi, room.yi + 1, room.za)).getDamage() == 2) {
                        if (level.getBlock(new Vector3(room.xi, room.ya, room.za)).getDamage() == 1) {
                            this.room.rank = 3;
                        }
                    }
                }
                break;
        }
        checkFinish();
    }

    @Override
    public void checkFinish() {
        if (this.room.rank >= 3) {
            this.room.finish = true;
        }
    }

    @Override
    public void madeArena() {
        this.click=null;
        switch (room.direction) {
            case "x+":
            case "z-":
                room.level.setBlock(new Vector3(room.xi, room.yi, room.za), Block.get(35, 3));
                room.level.setBlock(new Vector3(room.xi, room.yi + 1, room.za), Block.get(35, 2));
                room.level.setBlock(new Vector3(room.xi, room.yi + 2, room.za), Block.get(35, 1));
                break;
            case "x-":
            case "z+":
                room.level.setBlock(new Vector3(room.xa, room.yi, room.zi), Block.get(35, 3));
                room.level.setBlock(new Vector3(room.xa, room.yi + 1, room.zi), Block.get(35, 2));
                room.level.setBlock(new Vector3(room.xa, room.yi + 2, room.zi), Block.get(35, 1));
                break;
        }
        finishBuild();
    }
}
