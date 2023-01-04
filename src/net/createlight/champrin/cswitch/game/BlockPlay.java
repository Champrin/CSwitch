package net.createlight.champrin.cswitch.game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import net.createlight.champrin.cswitch.room.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BlockPlay extends Game implements Listener {

    private int size; //游戏区域为size*size

    // 正确的模板
    public ArrayList<Block> correctLayout = new ArrayList<>(Arrays.asList(
            Block.get(35, 14),
            Block.get(35, 1),
            Block.get(35, 4),
            Block.get(35, 5),
            Block.get(35, 13),
            Block.get(35, 9),
            Block.get(35, 3),
            Block.get(35, 11)
    ));

    public BlockPlay(Room room) {
        super(room);
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 房间游戏条件限制
        if (!this.gameType.equals("BlockPlay")) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        Block block = event.getBlock();
        if (block.getId() == 20) return;
        if (!this.room.isInArena(block)) return;

        this.updateBlock(block);
    }

    public void updateBlock(Block block) {
        // 0.获取方块的真实世界坐标x,y,z
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));
        int tmpX, tmpY, tmpZ;

        Level level = block.getLevel();

        // 1.从block的上下左右四个方向寻找玻璃方块，找到跟block交换
        for (int[] blockMoveDirection : blockMoveDirectionsWithoutSelf) {
            tmpX = x + blockMoveDirection[0];
            tmpY = y + blockMoveDirection[1];
            tmpZ = z + blockMoveDirection[2];

            if (level.getBlock(tmpX, tmpY, tmpZ).getId() == BlockID.GLASS) {
                level.setBlock(block, glassBlock);
                level.setBlock(tmpX, tmpY, tmpZ, block, false, true);
                break;
            }
        }

        this.checkFinish();
    }

    @Override
    public void checkFinish() {
        boolean flag = true; // 判断标志位，flag = false表示游戏未完成

        int index = 0;

        switch (room.direction) {
            case X_PLUS -> {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        if (correctLayout.get(index) != level.getBlock(x, y, room.zMin)) {
                            flag = false;
                            break;
                        }
                        ++index;
                    }
                }
            }
            case X_MINUS -> {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMax; x >= room.xMin; x--) {
                        if (correctLayout.get(index) != level.getBlock(x, y, room.zMin)) {
                            flag = false;
                            break;
                        }
                        ++index;
                    }
                }
            }
            case Z_PLUS -> {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        if (correctLayout.get(index) != level.getBlock(room.xMin, y, z)) {
                            flag = false;
                            break;
                        }
                        ++index;
                    }
                }
            }
            case Z_MINUS -> {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMax; z >= room.zMin; z--) {
                        if (correctLayout.get(index) != level.getBlock(room.xMin, y, z)) {
                            flag = false;
                            break;
                        }
                        ++index;
                    }
                }
            }
        }

        this.room.isFinished = flag;
    }

    @Override
    public void buildArena() {
        ArrayList<Block> layout = (ArrayList<Block>) correctLayout.clone();
        Collections.shuffle(layout);
        layout.add(Block.get(20, 0));

        int index = 0;
        switch (room.direction) {
            case X_PLUS -> {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        room.level.setBlock(x, y, room.zMin, layout.get(index), false, true);
                        ++index;
                    }
                }
            }
            case X_MINUS -> {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMax; x >= room.xMin; x--) {
                        room.level.setBlock(x, y, room.zMin, layout.get(index), false, true);
                        ++index;
                    }
                }
            }
            case Z_PLUS -> {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        room.level.setBlock(room.xMin, y, z, layout.get(index), false, true);
                        ++index;
                    }
                }
            }
            case Z_MINUS -> {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMax; z >= room.zMin; z--) {
                        room.level.setBlock(room.xMin, y, z, layout.get(index), false, true);
                        ++index;
                    }
                }
            }
        }
        buildOperation(true);
    }
}
