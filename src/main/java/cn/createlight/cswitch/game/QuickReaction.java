package cn.createlight.cswitch.game;

import cn.createlight.cswitch.CSwitchGameType;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.math.Vector3;
import cn.createlight.cswitch.room.Room;

public class QuickReaction extends Game {

    public QuickReaction(Room room) {
        super(room);
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 房间游戏条件限制
        if (this.room.gameType != CSwitchGameType.QUICK_REACTION) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        Block block = event.getBlock();
        if (block.getId() != 35) return;
        if (block.getDamage() == 0) return;
        if (!this.room.isInArena(block)) return;

        this.room.point = room.point + 1;
        block.level.setBlock(block, Block.get(35, 0));
    }

    @Override
    public void checkFinish() {
    }

    @Override
    public void buildArena() {
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                for (int x = room.xMin; x <= room.xMax; x++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        room.level.setBlock(new Vector3(x, y, room.zMin), Block.get(35, 0));
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int z = room.zMin; z <= room.zMax; z++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        room.level.setBlock(new Vector3(room.xMin, y, z), Block.get(35, 0));
                    }
                }
                break;
        }
        buildOperation(true);
    }
}
