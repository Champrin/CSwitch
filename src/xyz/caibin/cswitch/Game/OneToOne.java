package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import xyz.caibin.cswitch.Room;

public class OneToOne extends Game implements Listener {

    public OneToOne( Room plugin) {
        this.plugin = plugin;
        this.game_type = plugin.game_type;
        this.area = (int) this.plugin.data.get("area");
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.game_type.equals("OneToOne")) {
            if (this.plugin.game != 1) return;
            Player player = event.getPlayer();
            if (this.plugin.isInGame(player)) {
                Block block = event.getBlock();
                int x = (int) Math.round(Math.floor(block.x));
                int y = (int) Math.round(Math.floor(block.y));
                int z = (int) Math.round(Math.floor(block.z));
                int[] pos = {x, y, z};
                event.setCancelled(true);
                if (this.plugin.isInArena(pos)) {
                    Item item = player.getInventory().getItemInHand();
                    if (item.getId() == 0) return;
                    if (block.getId() == 35) {
                        this.count = count + 1;
                        block.level.setBlock(block, Block.get(20, 0));
                        if (block.getDamage() + item.getDamage() == 15) {
                            this.plugin.rank = this.plugin.rank + 1;
                        } else {
                            this.plugin.rank = this.plugin.rank - 1;
                            player.sendTitle("§l§c哎呀！配对错误","接下来要小心哦！" );
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
            this.plugin.finish=true;
            this.count = 0;
        }
    }

}
