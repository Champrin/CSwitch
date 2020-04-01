package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import xyz.caibin.cswitch.Room;

public class BeFaster extends Game {

    public BeFaster(Room plugin) {
        this.plugin = plugin;
        this.game_type = plugin.game_type;
        this.area = (int) this.plugin.data.get("area");
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("BeFaster")) {
            if (this.plugin.game != 1) return;
            Player player = event.getPlayer();
            if (this.plugin.isInGame(player)) {
                Block block = event.getBlock();
                if (block.getId() != 35) return;
                if (block.getDamage() == 0) return;
                int x = (int) Math.round(Math.floor(block.x));
                int y = (int) Math.round(Math.floor(block.y));
                int z = (int) Math.round(Math.floor(block.z));
                int[] pos = {x, y, z};
                if (this.plugin.isInArena(pos)) {
                    event.setCancelled(true);
                    this.plugin.rank = plugin.rank +1;
                    block.level.setBlock(block, Block.get(35, 0));
                }
            }
        }
    }

    @Override
    public void checkFinish() {
    }

}
