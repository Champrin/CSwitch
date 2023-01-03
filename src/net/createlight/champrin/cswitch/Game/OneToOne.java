package net.createlight.champrin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.Room;

import java.util.Random;

public class OneToOne extends Game implements Listener {

    public OneToOne(Room room) {
        super(room);
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.isFinished) return;
        if (this.gameTypeName.equals("OneToOne")) {
            if (!this.room.isStarted) return;
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                Block block = event.getBlock();
                event.setCancelled(true);
                if (this.room.isInArena(block)) {
                    Item item = player.getInventory().getItemInHand();
                    if (item.getId() == 0) return;
                    if (block.getId() == 35) {
                        this.count = count + 1;
                        block.level.setBlock(block, Block.get(20, 0));
                        if (block.getDamage() + item.getDamage() == 15) {
                            this.room.rank = this.room.rank + 1;
                        } else {
                            this.room.rank = this.room.rank - 1;
                            player.sendTitle("§l§c哎呀！配对错误", "接下来要小心哦！");
                        }
                    }
                    checkFinish();
                }
            }
        }
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
            case "x+":
            case "x-":
                for (int x = room.xMin; x <= room.xMax; x++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        int num = new Random().nextInt(16);
                        Block block = Block.get(35, num);
                        room.level.setBlock(new Vector3(x, y, room.zMin), block);
                    }
                }
                break;
            case "z+":
            case "z-":
                for (int z = room.zMin; z <= room.zMax; z++) {
                    for (int y = room.yMin; y <= room.yMax; y++) {
                        int num = new Random().nextInt(16);
                        Block block = Block.get(35, num);
                        room.level.setBlock(new Vector3(room.xMin, y, z), block);
                    }
                }
                break;
        }
        buildOperation(true);
    }
}
