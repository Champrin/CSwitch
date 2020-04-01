package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import xyz.caibin.cswitch.Room;

import java.util.LinkedHashMap;

public class Sudoku extends Game {

    public LinkedHashMap<String, Integer> value;
    public LinkedHashMap<String, Boolean> check = new LinkedHashMap<>();

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
            Player player = event.getPlayer();
            if (this.plugin.isInGame(player)) {
                Block block = event.getBlock();
                if (block.getDamage() == 15) {
                    event.setCancelled(true);
                    return;
                }
                String position = getPosition(block);
                if (this.isInArena(position)) {
                    if (!this.check.get(position)) return;
                    if (isTrue(position, block.getDamage())) {
                        this.plugin.rank = plugin.rank - 1;
                        this.check.put(position, false);
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
            Player player = event.getPlayer();
            if (this.plugin.isInGame(player)) {
                Block block = event.getBlock();
                String position = getPosition(block);
                if (this.isInArena(position)) {
                    if (check.get(position)) return;
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
            this.check.put(position, true);
        }
        checkFinish();
    }

    private boolean isTrue(String position, int value) {
        return this.value.get(position) == value;
    }

    private String getPosition(Block block) {
        int xi = plugin.xi + 1;
        int ya = plugin.ya - 1;
        int zi = plugin.zi + 1;
        int xa = plugin.xa - 1;
        int za = plugin.za - 1;
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));
        int h = 0, l = 0;
        if (y <= ya && y > ya - 3) {
            l = Math.abs(ya - y);
        } else if (y < ya - 3 && y > ya - 7) {
            l = Math.abs(ya - y) - 1;
        } else if (y < ya - 7 && y > ya - 11) {
            l = Math.abs(ya - y) - 2;
        }
        switch (plugin.direction) {
            case "x+":
                if (x < xi + 3) {
                    h = Math.abs(x - xi);
                } else if (x > xi + 3 && x < xi + 7) {
                    h = Math.abs(x - xi) - 1;
                } else if (x > xi + 7 && x < xi + 11) {
                    h = Math.abs(x - xi) - 2;
                }
                return Math.abs(l) + "-" + Math.abs(h);
            case "x-":
                if (x < xa - 3) {
                    h = Math.abs(xa - x);
                } else if (x > xa - 3 && x < xa - 7) {
                    h = Math.abs(xa - x) - 1;
                } else if (x > xa - 7 && x < xa - 11) {
                    h = Math.abs(xa - x) - 2;
                }
                return Math.abs(l) + "-" + Math.abs(h);
            case "z+":
                if (z < zi + 3) {
                    h = Math.abs(z - zi);
                } else if (z > zi + 3 && z < zi + 7) {
                    h = Math.abs(z - zi) - 1;
                } else if (z > zi + 7 && z < zi + 11) {
                    h = Math.abs(z - zi) - 2;
                }
                return Math.abs(l) + "-" + Math.abs(h);
            case "z-":
                if (z < za - 3) {
                    h = Math.abs(za - z);
                } else if (z > za - 3 && z < za - 7) {
                    h = Math.abs(za - z) - 1;
                } else if (z > za - 7 && z < za - 11) {
                    h = Math.abs(za - z) - 2;
                }
                return Math.abs(l) + "-" + Math.abs(h);
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
