package net.createlight.champrin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

////红，橙，黄，绿，青，蓝，紫，灰，粉，黑，白，棕。
/// 14-1-4-5-13-9-3-11-10-8-7-6-15-0-12
public class BlockPlay_4 extends Game implements Listener {

    public ArrayList<Integer> checkLayout = new ArrayList<>(Arrays.asList(14, 1, 4, 5, 13, 9, 3, 11, 10, 8, 7, 6, 15, 0, 12, 0));

    public BlockPlay_4(Room room) {
        super(room);
        buildArena();
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.isFinished) return;
        if (this.gameTypeName.equals("BlockPlay_4")) {
            if (!this.room.isStarted) return;
            Block block = event.getBlock();
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                if (this.room.isInArena(block)) {
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
                if (room.direction.equals("x+") || room.direction.equals("x-")) {
                    if (checkBlock(block, new Vector3(x + 1, y, z))) {
                        checkBlock(block, new Vector3(x - 1, y, z));
                    }
                } else if (room.direction.equals("z+") || room.direction.equals("z-")) {
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
        switch (room.direction) {
            case "x+": {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        Vector3 v3 = new Vector3(x, y, room.zMin);
                        int id = block.getLevel().getBlock(v3).getDamage();
                        check.add(id);
                    }
                }
                break;
            }
            case "x-": {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMax; x >= room.xMin; x--) {
                        Vector3 v3 = new Vector3(x, y, room.zMin);
                        int id = block.getLevel().getBlock(v3).getDamage();
                        check.add(id);
                    }
                }
                break;
            }
            case "z+": {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        Vector3 v3 = new Vector3(room.xMin, y, z);
                        int id = block.getLevel().getBlock(v3).getDamage();
                        check.add(id);
                    }
                }
                break;
            }
            case "z-": {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMax; z >= room.zMin; z--) {
                        Vector3 v3 = new Vector3(room.xMin, y, z);
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
            this.room.isFinished = true;
        }
    }

    @Override
    public void buildArena() {
        ArrayList<Integer> layout = new ArrayList<>(Arrays.asList(14, 1, 4, 5, 13, 9, 3, 11, 10, 8, 7, 6, 15, 0, 12));
        Collections.shuffle(layout);

        int a = 0;
        switch (room.direction) {
            case "x+": {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMin; x <= room.xMax; x++) {
                        if (a == 15) break;
                        Vector3 v3 = new Vector3(x, y, room.zMin);
                        int mate = layout.get(a);
                        room.level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;
                    }
                }
                break;
            }
            case "x-": {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int x = room.xMax; x >= room.xMin; x--) {
                        if (a == 15) break;
                        Vector3 v3 = new Vector3(x, y, room.zMin);
                        int mate = layout.get(a);
                        room.level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;
                    }
                }
                break;
            }
            case "z+": {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMin; z <= room.zMax; z++) {
                        if (a == 15) break;
                        Vector3 v3 = new Vector3(room.xMin, y, z);
                        int mate = layout.get(a);
                        room.level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;

                    }
                }
                break;
            }
            case "z-": {
                for (int y = room.yMax; y >= room.yMin; y--) {
                    for (int z = room.zMax; z >= room.zMin; z--) {
                        if (a == 15) break;
                        Vector3 v3 = new Vector3(room.xMin, y, z);
                        int mate = layout.get(a);
                        room.level.setBlock(v3, Block.get(35, mate));
                        a = a + 1;
                    }
                }
                break;
            }
        }
        buildOperation(true);
    }
}