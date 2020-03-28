package xyz.caibin.cswitch.Game;

import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerInteractEvent;
import xyz.caibin.cswitch.Room;

public class HanoiTower extends Game {


    public HanoiTower(Room plugin) {
        this.plugin = plugin;
        this.game_type = "HanoiTower";
        this.area = (int) this.plugin.data.get("area");
    }

    @Override
    public void onTouch(PlayerInteractEvent event) {

    }

    public void updateBlock(Block block) {

    }

    @Override
    public void checkFinish() {

    }
}
