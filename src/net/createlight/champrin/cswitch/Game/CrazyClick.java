package net.createlight.champrin.cswitch.Game;


import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerInteractEvent;
import net.createlight.champrin.cswitch.Room;

public class CrazyClick extends Game {

    public CrazyClick(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.isFinished) return;
        if (this.gameTypeName.equals("CrazyClick")) {
            if (!this.room.isStarted) return;
            if (this.room.isInGame(event.getPlayer())) {
                if (event.getBlock().getId() == 57) {
                    this.room.rank = room.rank + 1;
                }
            }
        }
    }

    @Override
    public void checkFinish() {

    }

    @Override
    public void buildArena() {
        buildOperation(true);
    }
}
