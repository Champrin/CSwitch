package net.createlight.champrin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.Room;

public class BeFaster extends Game {

    public BeFaster(Room room) {
        super(room);
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 房间游戏条件限制
        if (!this.gameTypeName.equals("BeFaster")) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (this.room.isInGame(player)) return;
        // 该类型游戏机制
        Block block = event.getBlock();
        if (block.getId() != 35) return;
        if (block.getDamage() == 0) return;
        if (!this.room.isInArena(block)) return;

        event.setCancelled(true);
        this.room.rank = room.rank + 1;
        block.level.setBlock(block, Block.get(35, 0));
    }

    @Override
    public void checkFinish() {
    }

    @Override
    public void buildArena() {
        switch (room.direction) {
            case "x+":
            case "x-":
                for (int x = room.xMin; x <= room.xMax; x++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        room.level.setBlock(new Vector3(x, y, room.zMin), Block.get(35, 0));
                    }
                }
                break;
            case "z+":
            case "z-":
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
