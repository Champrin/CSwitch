package cn.createlight.cswitch.game;

import cn.createlight.cswitch.CSwitchGameType;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.math.Vector3;
import cn.createlight.cswitch.room.Room;

import java.util.Random;

public class LightsOut extends Game implements Listener {

    public LightsOut(Room room) {
        super(room);
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 判断是否在房间进行游戏
        if (this.room.gameType != CSwitchGameType.LIGHTS_OUT) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        Block block = event.getBlock();
        if (block.getId() != BlockID.WOOL) return;
        if (!this.room.isInArena(block)) return;

        updateBlock(block);
        checkFinish();
    }

    /**
     * 抽象数组中原本为1的变为0，原本为0的变为1
     */
    public void abstractProc() {
        int tmpRow, tmpCol;
        for (int[] moveDirection : abstractArrayMoveDirections) {
            tmpRow = row + moveDirection[0];
            tmpCol = col + moveDirection[1];

            if (abstractArray[tmpRow][tmpCol] == 1) {
                abstractArray[tmpRow][tmpCol] = 0;
                --this.room.point;
            } else {
                abstractArray[tmpRow][tmpCol] = 1;
                ++this.room.point;
            }
        }
    }

    public void updateBlock(Vector3 block) {
        // 0.获取方块的真实世界坐标x,y,z
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));

        // 1.根据在真实世界的坐标点获取在抽象数组中的行号和列号
        getRolCowByBlockPosition(x, y, z);

        // 2.根据行号和列号更新抽象数组
        abstractProc();

        // 3.根据抽象数组更新真实世界的方块
        int tmpX, tmpY, tmpZ;
        int tmpRow, tmpCol;

        int directionNum = blockMoveDirections.length;
        for (int i = 0; i < directionNum; ++i) {
            tmpX = x + blockMoveDirections[i][0];
            tmpY = y + blockMoveDirections[i][1];
            tmpZ = z + blockMoveDirections[i][2];

            tmpRow = row + abstractArrayMoveDirections[i][0];
            tmpCol = col + abstractArrayMoveDirections[i][1];

            room.level.setBlock(new Vector3(tmpX, tmpY, tmpZ),
                    abstractArray[tmpRow][tmpCol] == 1 ? greenWoolBlock : blackWoolBlock);
        }
    }

    @Override
    public void checkFinish() {
        if (this.room.point >= room.area) {
            this.room.isFinished = true;
        }
    }

    //TODO 写一个数据生成器 使得必有解
    @Override
    public void buildArena() {
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                for (int x = room.xMin; x <= room.xMax; x++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        int num = new Random().nextInt(2);
                        int mate = (num == 1 ? 5 : 15);
                        if (mate == 5) {
                            ++this.room.point;
                        }
                        Block block = Block.get(35, mate);
                        room.level.setBlock(new Vector3(x, y, room.zMin), block);
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int z = room.zMin; z <= room.zMax; z++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        int num = new Random().nextInt(2);
                        int mate = (num == 1 ? 5 : 15);
                        if (mate == 5) {
                            ++this.room.point;
                        }
                        Block block = Block.get(35, mate);
                        room.level.setBlock(new Vector3(room.xMin, y, z), block);
                    }
                }
                break;
        }
        buildOperation(true);
    }
}