package net.createlight.champrin.cswitch.game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import net.createlight.champrin.cswitch.room.Room;

import java.util.Random;

public class OneToOne extends Game implements Listener {

    public OneToOne(Room room) {
        super(room);
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 判断是否在房间进行游戏
        if (!this.gameType.equals("OneToOne")) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        Block block = event.getBlock();
        if (!this.room.isInArena(block)) return;
        Item item = player.getInventory().getItemInHand();
        if (item.getId() == 0) return;
        if (block.getId() != 35) return;

        ++this.count;
        block.level.setBlock(block, glassBlock);
        if (block.getDamage() + item.getDamage() == 15) {
            ++this.room.point;
        } else {
            --this.room.point;
            //TODO 音效或粒子效果代替
            //player.sendTitle("§l§c哎呀！配对错误", "接下来要小心哦！");
        }
        checkFinish();
    }

    @Override
    public void checkFinish() {
        if (count >= area) {
            this.room.isFinished = true;
            this.count = 0;
        }
    }

    @Override
    public void buildArena() {
        switch (room.direction) {
            case X_PLUS:
            case X_MINUS:
                for (int x = room.xMin; x <= room.xMax; x++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        Block block = Block.get(35, new Random().nextInt(16));
                        room.level.setBlock(x, y, room.zMin, block, false, true);
                    }
                }
                break;
            case Z_PLUS:
            case Z_MINUS:
                for (int z = room.zMin; z <= room.zMax; z++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        Block block = Block.get(35, new Random().nextInt(16));
                        room.level.setBlock(room.xMin, y, z, block, false, true);
                    }
                }
                break;
        }
        buildOperation(true);
    }
}
