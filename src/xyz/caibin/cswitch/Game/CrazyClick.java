package xyz.caibin.cswitch.Game;


import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerInteractEvent;
import xyz.caibin.cswitch.Room;

public class CrazyClick extends Game {

    public CrazyClick(Room plugin) {
        this.plugin = plugin;
        this.game_type = plugin.game_type;
    }

   @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("CrazyClick")) {
            if (this.plugin.game != 1) return;
            if (this.plugin.isInGame(event.getPlayer())) {
                if (event.getBlock().getId() == 57){
                    this.plugin.rank=plugin.rank+1;
                }
            }
        }
    }

    @Override
    public void checkFinish() {

    }
}
