package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import xyz.caibin.cswitch.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Sudoku extends Game {

    public LinkedHashMap<String, Integer> value;
    private ArrayList<Integer> num = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    public Sudoku(Room plugin) {
        this.plugin = plugin;
        this.game_type = plugin.game_type;
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("Sudoku")) {
            if (this.plugin.game != 1) return;
            Player player = event.getPlayer();
            if (this.plugin.isInGame(player)) {
                Block block = event.getBlock();
                if (block.getId() != Block.WOOL && !num.contains(block.getDamage())) {
                    event.setCancelled(true);
                    return;
                }
                String position = getPosition(block);
                if (this.isInArena(position)) {
                    block.level.setBlock(block, Block.get(0, 0));
                    if (isTrue(position, block.getDamage())) {
                        this.plugin.rank = plugin.rank - 1;
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("Sudoku")) {
            if (this.plugin.game != 1) return;
            Player player = event.getPlayer();
            if (this.plugin.isInGame(player)) {
                Block block = event.getBlock();
                if (block.getId() != Block.WOOL && !num.contains(block.getDamage())) {
                    event.setCancelled(true);
                    return;
                }
                String position = getPosition(block);
                if (this.isInArena(position)) {
                    updateBlock(block, position);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isInArena(String position) {
        return this.value.containsKey(position);
    }

    public void updateBlock(Block block, String position) {
        int value = block.getDamage();
        if (isTrue(position, value)) {
            this.plugin.rank = plugin.rank + 1;
        }
        checkFinish();
    }

    private boolean isTrue(String position, int value) {
        System.out.println("a-"+this.value.get(position)+"b-"+value);
        return this.value.get(position) == value;
    }

    private String getPosition(Block block) {
        String direction = (String) this.plugin.data.get("direction");
        String[] p1 = ((String) this.plugin.data.get("pos1")).split("\\+");
        String[] p2 = ((String) this.plugin.data.get("pos2")).split("\\+");
        int xi = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        int yi = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        int zi = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));
        switch (direction) {
            case "x+":
            case "x-":
                return (x - xi) + "-" + (y - yi);
            case "z+":
            case "z-":
                return (z - zi) + "-" + (y - yi);
            default:
                return null;
        }
    }

    @Override
    public void checkFinish() {
        if (this.plugin.rank >= value.size()) {
            this.plugin.finish = true;
        }
    }
}
