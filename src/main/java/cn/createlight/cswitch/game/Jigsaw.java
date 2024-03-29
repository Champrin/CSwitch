package cn.createlight.cswitch.game;

import cn.createlight.cswitch.CSwitchGameType;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.createlight.cswitch.room.Room;

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
        buildOperation(true);
    }

    public void shuffleLayout() {
        Collections.shuffle(layout);
    }

    public void setRightPlace() {
        switch (room.direction) {
            case X_PLUS: {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        Vector3 v3 = new Vector3(x, y, room.zMin);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case X_MINUS: {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMax; x >= room.xMin; x--) {
                        Vector3 v3 = new Vector3(x, y, room.zMin);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case Z_PLUS: {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        Vector3 v3 = new Vector3(room.xMin, y, z);
                        Pos.add(v3);
                    }
                }
                break;
            }
            case Z_MINUS: {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMax; z >= room.zMin; z--) {
                        Vector3 v3 = new Vector3(room.xMin, y, z);
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
            case X_PLUS: {
                for (int y = room.yMax + 6; y >= room.yMin + 6; y--) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        String[] I = layout.get(a).split("-");
                        room.level.setBlock(new Vector3(x, y, room.zMin), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case X_MINUS: {
                for (int y = room.yMax + 6; y >= room.yMin + 6; y--) {
                    for (int x = room.xMax; x >= room.xMin; x--) {
                        String[] I = layout.get(a).split("-");
                        room.level.setBlock(new Vector3(x, y, room.zMin), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case Z_PLUS: {
                for (int y = room.yMax + 6; y >= room.yMin + 6; y--) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        String[] I = layout.get(a).split("-");
                        room.level.setBlock(new Vector3(room.xMin, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
            case Z_MINUS: {
                for (int y = room.yMax + 6; y >= room.yMin + 6; y--) {
                    for (int z = room.zMax; z >= room.zMin; z--) {
                        String[] I = layout.get(a).split("-");
                        room.level.setBlock(new Vector3(room.xMin, y, z), Block.get(Integer.parseInt(I[0]), Integer.parseInt(I[1])));
                        a = a + 1;
                    }
                }
                break;
            }
        }
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        // 判断是否在房间进行游戏
        if (this.room.gameType != CSwitchGameType.JIGSAW) return;
        if (this.room.isFinished) return;
        if (!this.room.isStarted) return;
        Player player = event.getPlayer();
        if (!this.room.isInGame(player)) return;

        // 满足判断条件，终止事件带来的其他影响
        event.setCancelled(true);

        // 该类型游戏机制
        Block block = event.getBlock();
        if (!this.room.isInArena(block)) return;

        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));
        event.setCancelled(true);
        Vector3 v3 = new Vector3(x, y, z);
        Item item = player.getInventory().getItemInHand();
        if (item.getId() == 0) return;
        String Hand = item.getId() + "-" + item.getDamage();
        if (layout.contains(Hand)) {
            int a = layout.indexOf(Hand);
            if (Pos.get(a).equals(v3)) {
                this.room.point = this.room.point + 1;
                updateBlock(block, Hand);
            }
        }
    }

    public void updateBlock(Block block, String Hand) {
        String[] Item = Hand.split("-");
        room.level.setBlock(block, Block.get(Integer.parseInt(Item[0]), Integer.parseInt(Item[1])));
        checkFinish();
    }

    public void checkFinish() {
        if (this.room.point >= 9) {
            this.room.isFinished = true;
        }
    }

    @Override
    public void buildArena() {
        shuffleLayout();
        setRightPlace();
        setBlock();
        buildOperation(true);
    }
}
