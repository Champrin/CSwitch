package cn.createlight.cswitch.game;

import cn.createlight.cswitch.CSwitchGameType;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.createlight.cswitch.room.Room;

public class HanoiTower extends Game implements Listener {

    public HanoiTower(Room room) {
        super(room);
    }

    private Block click;

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 判断是否在房间进行游戏
        if (this.room.gameType != CSwitchGameType.HANOI_TOWER) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        Block block = event.getBlock();
        if (block.getDamage() == 15) return;
        if (!this.room.isInArena(block)) return;

        if (click == null) {
            this.click = block;
        } else {
            updateBlock(block);
            this.click = null;
        }
    }

    public void updateBlock(Block block) {
        Level level = block.getLevel();
        Block b = level.getBlock(new Vector3(block.x, room.yMin, block.z));
        if (level.getBlock(new Vector3(click.x, click.y + 1, click.z)).getDamage() != 0) {
            room.gamePlayer.sendMessage(">  操作错误");
            return;
        }
        if (b.getDamage() == 0) {
            level.setBlock(click, Block.get(35, 0));
            level.setBlock(b, Block.get(35, click.getDamage()));
        } else if (b.getDamage() != 0) {
            Block b1 = level.getBlock(new Vector3(block.x, room.yMin + 1, block.z));
            if (b1.getDamage() == 0) {
                if (b.getDamage() > click.getDamage()) {
                    level.setBlock(click, Block.get(35, 0));
                    level.setBlock(b1, Block.get(35, click.getDamage()));
                } else {
                    room.gamePlayer.sendMessage(">  操作错误");
                    return;
                }
            } else if (b1.getDamage() != 0) {
                Block b2 = level.getBlock(new Vector3(block.x, room.yMin + 2, block.z));
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
            case X_PLUS:
            case Z_MINUS:
                if (level.getBlock(new Vector3(room.xMax, room.yMin, room.zMin)).getDamage() == 3) {
                    if (level.getBlock(new Vector3(room.xMax, room.yMin + 1, room.zMin)).getDamage() == 2) {
                        if (level.getBlock(new Vector3(room.xMax, room.yMax, room.zMin)).getDamage() == 1) {
                            this.room.point = 3;
                        }
                    }
                }
                break;
            case Z_PLUS:
            case X_MINUS:
                if (level.getBlock(new Vector3(room.xMin, room.yMin, room.zMax)).getDamage() == 3) {
                    if (level.getBlock(new Vector3(room.xMin, room.yMin + 1, room.zMax)).getDamage() == 2) {
                        if (level.getBlock(new Vector3(room.xMin, room.yMax, room.zMax)).getDamage() == 1) {
                            this.room.point = 3;
                        }
                    }
                }
                break;
        }
        checkFinish();
    }

    @Override
    public void checkFinish() {
        if (this.room.point >= 3) {
            this.room.isFinished = true;
        }
    }

    @Override
    public void setArenaFrame(){
        int ma = 0;
        int aa = 1;
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                for (int x = room.xMin; x <= room.xMax; x++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        room.level.setBlock(new Vector3(x, y, room.zMin), Block.get(35, ma));
                    }
                    if (aa == 1) {
                        ma = 15;
                        aa = 2;
                    } else {
                        ma = 0;
                        aa = 1;
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int z = room.zMin; z <= room.zMax; z++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        room.level.setBlock(new Vector3(room.xMin, y, z), Block.get(35, ma));
                    }
                    if (aa == 1) {
                        ma = 15;
                        aa = 2;
                    } else {
                        ma = 0;
                        aa = 1;
                    }
                }
                break;
        }
    }

    @Override
    public void buildArena() {
        this.click = null;
        switch (room.direction) {
            case X_PLUS:
            case Z_MINUS:
                room.level.setBlock(room.xMin, room.yMin, room.zMax, Block.get(35, 3), false, true);
                room.level.setBlock(room.xMin, room.yMin + 1, room.zMax, Block.get(35, 2), false, true);
                room.level.setBlock(room.xMin, room.yMin + 2, room.zMax, Block.get(35, 1), false, true);
                break;
            case X_MINUS:
            case Z_PLUS:
                room.level.setBlock(room.xMax, room.yMin, room.zMin, Block.get(35, 3), false, true);
                room.level.setBlock(room.xMax, room.yMin + 1, room.zMin, Block.get(35, 2), false, true);
                room.level.setBlock(room.xMax, room.yMin + 2, room.zMin, Block.get(35, 1), false, true);
                break;
        }
        buildOperation(true);
    }
}
