package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
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
        String direction = (String) this.plugin.data.get("direction");
        String[] p1 = ((String) this.plugin.data.get("pos1")).split("\\+");
        String[] p2 = ((String) this.plugin.data.get("pos2")).split("\\+");
        switch (direction) {
            case "x+": {
                int z = Integer.parseInt(p1[2]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int x = Integer.parseInt(p1[0]); x <= Integer.parseInt(p2[0]); x++) {
                        Vector3 v3 = new Vector3(x, y, z);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case "x-": {
                int z = Integer.parseInt(p1[2]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int x = Integer.parseInt(p1[0]); x >= Integer.parseInt(p2[0]); x--) {
                        Vector3 v3 = new Vector3(x, y, z);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case "z+": {
                int x = Integer.parseInt(p1[0]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int z = Integer.parseInt(p1[2]); z <= Integer.parseInt(p2[2]); z++) {
                        Vector3 v3 = new Vector3(x, y, z);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case "z-": {
                int x = Integer.parseInt(p1[0]);
                for (int y = Integer.parseInt(p1[1]); y >= Integer.parseInt(p2[1]); y--) {
                    for (int z = Integer.parseInt(p1[2]); z >= Integer.parseInt(p2[2]); z--) {
                        Vector3 v3 = new Vector3(x, y, z);
                        Pos.add(v3);
                    }
                }
                break;
            }
        }
    }

    public void setBlock() {
        String direction = (String) this.plugin.data.get("direction");
        String[] p1 = ((String) this.plugin.data.get("pos1")).split("\\+");
        String[] p2 = ((String) this.plugin.data.get("pos2")).split("\\+");
        Level level = this.plugin.plugin.getServer().getLevelByName((String) this.plugin.data.get("room_world"));
        int a = 0;
        switch (direction) {
            case "x+": {
                int z = Integer.parseInt(p1[2]);
                for (int y = Integer.parseInt(p1[1]) + 6; y >= Integer.parseInt(p2[1]) + 6; y--) {
                    for (int x = Integer.parseInt(p1[0]); x <= Integer.parseInt(p2[0]); x++) {
                        String[] I = layout.get(a).split("-");
                        level.setBlock(new Vector3(x, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case "x-": {
                int z = Integer.parseInt(p1[2]);
                for (int y = Integer.parseInt(p1[1]) + 6; y >= Integer.parseInt(p2[1]) + 6; y--) {
                    for (int x = Integer.parseInt(p1[0]); x >= Integer.parseInt(p2[0]); x--) {
                        String[] I = layout.get(a).split("-");
                        level.setBlock(new Vector3(x, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case "z+": {
                int x = Integer.parseInt(p1[0]);
                for (int y = Integer.parseInt(p1[1]) + 6; y >= Integer.parseInt(p2[1]) + 6; y--) {
                    for (int z = Integer.parseInt(p1[2]); z <= Integer.parseInt(p2[2]); z++) {
                        String[] I = layout.get(a).split("-");
                        level.setBlock(new Vector3(x, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case "z-": {
                int x = Integer.parseInt(p1[0]);
                for (int y = Integer.parseInt(p1[1]) + 6; y >= Integer.parseInt(p2[1]) + 6; y--) {
                    for (int z = Integer.parseInt(p1[2]); z >= Integer.parseInt(p2[2]); z--) {
                        String[] I = layout.get(a).split("-");
                        level.setBlock(new Vector3(x, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
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
        Level level = block.getLevel();
        level.setBlock(block, Block.get(Integer.parseInt(Item[0]), Integer.parseInt(Item[1])));

        checkFinish();
    }

    public void checkFinish() {
        if (this.plugin.rank >= 9) {
            this.plugin.finish = true;
        }
    }
}
