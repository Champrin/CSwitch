package cn.createlight.cswitch.game;


import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.createlight.cswitch.room.Room;

public class CrazyClick extends Game {

    public CrazyClick(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTouch(PlayerInteractEvent event) {
        // 判断是否在房间进行游戏
        if (!this.gameType.equals("CrazyClick")) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        if (!this.room.isInGame(event.getPlayer())) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        if (event.getBlock().getId() == 57) {
            ++this.room.point;
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
