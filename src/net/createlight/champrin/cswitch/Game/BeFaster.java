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
        if (this.room.finish) return;
        if (this.game_type.equals("BeFaster")) {
            if (this.room.game != 1) return;
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                Block block = event.getBlock();
                if (block.getId() != 35) return;
                if (block.getDamage() == 0) return;
                if (this.room.isInArena(block)) {
                    event.setCancelled(true);
                    this.room.rank = room.rank + 1;
                    block.level.setBlock(block, Block.get(35, 0));
                }
            }
        }
    }

    @Override
    public void checkFinish() {
    }

    @Override
    public void madeArena() {
        switch (room.direction) {
            case "x+":
            case "x-":
                for (int x = room.xi; x <= room.xa; x++) {
                    for (int y = room.yi; y <= room.ya; y++) {
                        room.level.setBlock(new Vector3(x, y, room.zi), Block.get(35, 0));
                    }
                }
                break;
            case "z+":
            case "z-":
                for (int z = room.zi; z <= room.za; z++) {
                    for (int y = room.yi; y <= room.ya; y++) {
                        room.level.setBlock(new Vector3(room.xi, y, z), Block.get(35, 0));
                    }
                }
                break;
        }
        finishBuild();
    }
}
