package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import xyz.caibin.cswitch.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Jigsaw extends Game implements Listener {


    public ArrayList<String> layout = new ArrayList<>(Arrays.asList("35-0", "42-0", "80-0", "155-0", "159-0", "24-0", "35-4", "159-4", "179-0"));
    public ArrayList<Vector3> Pos = new ArrayList<>();

    public Jigsaw(Room plugin) {
        this.plugin = plugin;
        this.game_type = plugin.game_type;
        this.area = (int) this.plugin.data.get("area");
    }

    public void shuffleLayout() {
        Collections.shuffle(layout);
    }

    public void setRightPlace() {
        switch (plugin.direction) {
            case "x+": {
                for (int y = plugin.ya; y >= plugin.yi; y--) {
                    for (int x = plugin.xi; x <= plugin.xa; x++) {
                        Vector3 v3 = new Vector3(x, y, plugin.zi);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case "x-": {
                for (int y = plugin.ya; y >= plugin.yi; y--) {
                    for (int x = plugin.xa; x >= plugin.xi; x--) {
                        Vector3 v3 = new Vector3(x, y, plugin.zi);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case "z+": {
                for (int y = plugin.ya; y >= plugin.yi; y--) {
                    for (int z = plugin.zi; z <= plugin.za; z++) {
                        Vector3 v3 = new Vector3(plugin.xi, y, z);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case "z-": {
                for (int y = plugin.ya; y >= plugin.yi; y--) {
                    for (int z = plugin.za; z >= plugin.zi; z--) {
                        Vector3 v3 = new Vector3(plugin.xi, y, z);
                        Pos.add(v3);
                    }
                }
                break;
            }
        }
    }

    public void setBlock() {
        int a = 0;
        switch (plugin.direction) {
            case "x+": {
                for (int y = plugin.ya + 6; y >= plugin.yi + 6; y--) {
                    for (int x = plugin.xi; x <= plugin.xa; x++) {
                        String[] I = layout.get(a).split("-");
                        plugin.level.setBlock(new Vector3(x, y, plugin.zi), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case "x-": {
                for (int y = plugin.ya + 6; y >= plugin.yi + 6; y--) {
                    for (int x = plugin.xa; x >= plugin.xi; x--) {
                        String[] I = layout.get(a).split("-");
                        plugin.level.setBlock(new Vector3(x, y, plugin.zi), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case "z+": {
                for (int y = plugin.ya + 6; y >= plugin.yi + 6; y--) {
                    for (int z = plugin.zi; z <= plugin.za; z++) {
                        String[] I = layout.get(a).split("-");
                        plugin.level.setBlock(new Vector3(plugin.xi, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case "z-": {
                for (int y = plugin.ya + 6; y >= plugin.yi + 6; y--) {
                    for (int z = plugin.za; z >= plugin.zi; z--) {
                        String[] I = layout.get(a).split("-");
                        plugin.level.setBlock(new Vector3(plugin.xi, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
        }
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("Jigsaw")) {
            if (this.plugin.game != 1) return;
            Player player = event.getPlayer();
            if (this.plugin.isInGame(player)) {
                Block block = event.getBlock();
                int x = (int) Math.round(Math.floor(block.x));
                int y = (int) Math.round(Math.floor(block.y));
                int z = (int) Math.round(Math.floor(block.z));
                int[] pos = {x, y, z};
                if (this.plugin.isInArena(pos)) {
                    event.setCancelled(true);
                    Vector3 v3 = new Vector3(x, y, z);
                    Item item = player.getInventory().getItemInHand();
                    if (item.getId() == 0) return;
                    String Hand = item.getId() + "-" + item.getDamage();
                    if (layout.contains(Hand)) {
                        int a = layout.indexOf(Hand);
                        if (Pos.get(a).equals(v3)) {
                            this.plugin.rank = this.plugin.rank + 1;
                            updateBlock(block, Hand);
                        }
                    }
                }
            }
        }
    }

    public void updateBlock(Block block, String Hand) {
        String[] Item = Hand.split("-");
        plugin.level.setBlock(block, Block.get(Integer.parseInt(Item[0]), Integer.parseInt(Item[1])));
        checkFinish();
    }

    public void checkFinish() {
        if (this.plugin.rank >= 9) {
            this.plugin.finish = true;
        }
    }
}
