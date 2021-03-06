package net.createlight.champrin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Jigsaw extends Game implements Listener {


    public ArrayList<String> layout = new ArrayList<>(Arrays.asList("35-0", "42-0", "80-0", "155-0", "159-0", "24-0", "35-4", "159-4", "179-0"));
    public ArrayList<Vector3> Pos = new ArrayList<>();

    public Jigsaw(Room room) {
        super(room);
        shuffleLayout();
        setRightPlace();
        setBlock();
        finishBuild();
    }

    public void shuffleLayout() {
        Collections.shuffle(layout);
    }

    public void setRightPlace() {
        switch (room.direction) {
            case "x+": {
                for (int y = room.ya; y >= room.yi; y--) {
                    for (int x = room.xi; x <= room.xa; x++) {
                        Vector3 v3 = new Vector3(x, y, room.zi);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case "x-": {
                for (int y = room.ya; y >= room.yi; y--) {
                    for (int x = room.xa; x >= room.xi; x--) {
                        Vector3 v3 = new Vector3(x, y, room.zi);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case "z+": {
                for (int y = room.ya; y >= room.yi; y--) {
                    for (int z = room.zi; z <= room.za; z++) {
                        Vector3 v3 = new Vector3(room.xi, y, z);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case "z-": {
                for (int y = room.ya; y >= room.yi; y--) {
                    for (int z = room.za; z >= room.zi; z--) {
                        Vector3 v3 = new Vector3(room.xi, y, z);
                        Pos.add(v3);
                    }
                }
                break;
            }
        }
    }

    public void setBlock() {
        int a = 0;
        switch (room.direction) {
            case "x+": {
                for (int y = room.ya + 6; y >= room.yi + 6; y--) {
                    for (int x = room.xi; x <= room.xa; x++) {
                        String[] I = layout.get(a).split("-");
                        room.level.setBlock(new Vector3(x, y, room.zi), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case "x-": {
                for (int y = room.ya + 6; y >= room.yi + 6; y--) {
                    for (int x = room.xa; x >= room.xi; x--) {
                        String[] I = layout.get(a).split("-");
                        room.level.setBlock(new Vector3(x, y, room.zi), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case "z+": {
                for (int y = room.ya + 6; y >= room.yi + 6; y--) {
                    for (int z = room.zi; z <= room.za; z++) {
                        String[] I = layout.get(a).split("-");
                        room.level.setBlock(new Vector3(room.xi, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case "z-": {
                for (int y = room.ya + 6; y >= room.yi + 6; y--) {
                    for (int z = room.za; z >= room.zi; z--) {
                        String[] I = layout.get(a).split("-");
                        room.level.setBlock(new Vector3(room.xi, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
        }
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.finish) return;
        if (this.game_type.equals("Jigsaw")) {
            if (this.room.game != 1) return;
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                Block block = event.getBlock();
                int x = (int) Math.round(Math.floor(block.x));
                int y = (int) Math.round(Math.floor(block.y));
                int z = (int) Math.round(Math.floor(block.z));
                if (this.room.isInArena(block)) {
                    event.setCancelled(true);
                    Vector3 v3 = new Vector3(x, y, z);
                    Item item = player.getInventory().getItemInHand();
                    if (item.getId() == 0) return;
                    String Hand = item.getId() + "-" + item.getDamage();
                    if (layout.contains(Hand)) {
                        int a = layout.indexOf(Hand);
                        if (Pos.get(a).equals(v3)) {
                            this.room.rank = this.room.rank + 1;
                            updateBlock(block, Hand);
                        }
                    }
                }
            }
        }
    }

    public void updateBlock(Block block, String Hand) {
        String[] Item = Hand.split("-");
        room.level.setBlock(block, Block.get(Integer.parseInt(Item[0]), Integer.parseInt(Item[1])));
        checkFinish();
    }

    public void checkFinish() {
        if (this.room.rank >= 9) {
            this.room.finish = true;
        }
    }

    @Override
    public void madeArena() {
        shuffleLayout();
        setRightPlace();
        setBlock();
        finishBuild();
    }
}
