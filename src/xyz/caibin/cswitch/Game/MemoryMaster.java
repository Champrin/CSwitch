package xyz.caibin.cswitch.Game;

import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerInteractEvent;
import xyz.caibin.cswitch.Room;

public class MemoryMaster extends Game {


    public MemoryMaster(Room plugin) {
        this.plugin = plugin;
        this.game_type = "MemoryMaster";
        this.area = (int) this.plugin.data.get("area");
    }

    @Override
    public void onTouch(PlayerInteractEvent event) {

    }

    @Override
    public void updateBlock(Block block) {

    }

    @Override
    public void checkFinish() {

    }
}
