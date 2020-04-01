package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import xyz.caibin.cswitch.Room;

import java.util.ArrayList;
import java.util.Arrays;

////红，橙，黄，绿，青，蓝，紫，灰，粉，黑，白，棕。
/// 14-1-4-5-13-9-3-11-10-8-7-6-15-0-12
public class BlockPlay_4 extends Game implements Listener {

    public ArrayList<Integer> checkLayout = new ArrayList<>(Arrays.asList(14, 1, 4, 5, 13, 9, 3, 11, 10, 8, 7, 6, 15, 0, 12, 0));

    public BlockPlay_4(Room plugin) {
        this.plugin = plugin;
        this.game_type = plugin.game_type;
        this.area = (int) this.plugin.data.get("area");
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("BlockPlay_4")) {
            if (this.plugin.game != 1) return;
            Block block = event.getBlock();
            Player player = event.getPlayer();
            int x = (int) Math.round(Math.floor(block.x));
            int y = (int) Math.round(Math.floor(block.y));
            int z = (int) Math.round(Math.floor(block.z));
            int[] pos = {x, y, z};
            if (this.plugin.isInGame(player)) {
                if (this.plugin.isInArena(pos)) {
                    event.setCancelled(true);
                    this.updateBlock(block);
                }
            }
        }
    }

    public void updateBlock(Block block) {
        if (block.getId() == 20) return;
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));

        if (checkBlock(block, new Vector3(x, y + 1, z))) {
            if (checkBlock(block, new Vector3(x, y - 1, z))) {
                if (plugin.direction.equals("x+") || plugin.direction.equals("x-")) {
                    if (checkBlock(block, new Vector3(x + 1, y, z))) {
                        checkBlock(block, new Vector3(x - 1, y, z));
                    }
                } else if (plugin.direction.equals("z+") || plugin.direction.equals("z-")) {
                    if (checkBlock(block, new Vector3(x, y, z + 1))) {
                        checkBlock(block, new Vector3(x, y, z - 1));
                    }
                }
            }
        }
        checkArea(block);
        this.checkFinish();
    }

    public boolean checkBlock(Block blocK, Vector3 v3) {
        Level level = blocK.level;
        Block block = level.getBlock(v3);
        if (block.getId() == 20) {
            level.setBlock(blocK, Block.get(20, 0));
            level.setBlock(block, blocK);
            return false;
        }
        return true;
    }
    public ArrayList<Integer> check = new ArrayList<>();
    public void checkArea(Block block) {
        switch (plugin.direction) {
            case "x+": {
                for (int y = plugin.ya; y >= plugin.yi; y--) {
                    for (int x = plugin.xi; x <= plugin.xa; x++) {
                        Vector3 v3 = new Vector3(x, y, plugin.zi);
                        int id = block.getLevel().getBlock(v3).getDamage();
                        check.add(id);
                    }
                }
                break;
            }
            case "x-": {
                for (int y = plugin.ya; y >= plugin.yi; y--) {
                    for (int x = plugin.xa; x >= plugin.xi; x--) {
                        Vector3 v3 = new Vector3(x, y, plugin.zi);
                        int id = block.getLevel().getBlock(v3).getDamage();
                        check.add(id);
                    }
                }
                break;
            }
            case "z+": {
               for (int y = plugin.ya; y >= plugin.yi; y--) {
                    for (int z = plugin.zi; z <= plugin.za; z++) {
                        Vector3 v3 = new Vector3(plugin.xi, y, z);
                        int id = block.getLevel().getBlock(v3).getDamage();
                        check.add(id);
                    }
                }
                break;
            }
            case "z-": {
                for (int y = plugin.ya; y >= plugin.yi; y--) {
                    for (int z = plugin.za; z >= plugin.zi; z--) {
                        Vector3 v3 = new Vector3(plugin.xi, y, z);
                        int id = block.getLevel().getBlock(v3).getDamage();
                        check.add(id);
                    }
                }
                break;
            }
        }
    }
    @Override
    public void checkFinish() {
        if (this.checkLayout.equals(this.check)) {
            this.plugin.finish = true;
        }
    }
}