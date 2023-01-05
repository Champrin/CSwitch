package cn.createlight.cswitch.game;


import cn.createlight.cswitch.CSwitchGameType;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.createlight.cswitch.room.Room;
import cn.nukkit.math.Vector3;

public class CrazyClick extends Game {

    public CrazyClick(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTouch(PlayerInteractEvent event) {
        // 判断是否在房间进行游戏
        if (this.room.gameType != CSwitchGameType.CRAZY_CLICK) return;
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

    @Override
    public void setArenaFrame(){
        //TODO 区域不再限定为一个方块
        room.level.setBlock(new Vector3(room.xMin, room.yMin, room.zMin), Block.get(BlockID.DIAMOND_BLOCK, 0));
    }
}
