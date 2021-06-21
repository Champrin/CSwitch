package net.createlight.champrin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.Room;
import net.createlight.champrin.cswitch.untils.ShuDuBuilder;

import java.util.LinkedHashMap;

public class Sudoku extends Game {

    public LinkedHashMap<String, Integer> value;
    public LinkedHashMap<String, Boolean> check = new LinkedHashMap<>();

    public Sudoku(Room room) {
        super(room);
        madeArena();
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (this.room.finish) return;
        if (this.game_type.equals("Sudoku")) {
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                Block block = event.getBlock();
                if (block.getDamage() == 15) {
                    event.setCancelled(true);
                    return;
                }
                String position = getPosition(block);
                if (this.isInArena(position)) {
                    if (!this.check.get(position)) return;
                    if (isTrue(position, block.getDamage())) {
                        this.room.rank = room.rank - 1;
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
        if (this.room.finish) return;
        if (this.game_type.equals("Sudoku")) {
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
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
            this.room.rank = room.rank + 1;
            this.check.put(position, true);
        }
        checkFinish();
    }

    private boolean isTrue(String position, int value) {
        return this.value.get(position) == value;
    }

    private String getPosition(Block block) {
        int xi = room.xi + 1;
        int ya = room.ya - 1;
        int zi = room.zi + 1;
        int xa = room.xa - 1;
        int za = room.za - 1;
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
        switch (room.direction) {
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
        if (this.room.rank >= value.size()) {
            this.room.finish = true;
        }
    }

    @Override
    public void madeArena() {
        this.check.clear();
        ShuDuBuilder builder = new ShuDuBuilder();
        int[][] key = builder.getKey();
        value = builder.getValue();
        for (String k : value.keySet()) {
            check.put(k, false);
        }
        int a = 0, b = 0, h = 0;
        switch (room.direction) {
            case "x+":
                for (int y = room.ya; y >= room.yi; y--) {
                    b = b + 1;
                    if (b % 3 == 0) {
                        y = y - 1;
                    }
                    h = h + 1;
                    int l = 0;
                    for (int x = room.xi; x <= room.xa; x++) {
                        if (key[h][l] == 0) {
                            room.level.setBlock(new Vector3(x, y, room.zi - 1), Block.get(0, 0));
                        } else {
                            room.level.setBlock(new Vector3(x, y, room.zi - 1), Block.get(35, key[h][l]));
                        }
                        a = a + 1;
                        l = l + 1;
                        if (a == 3) {
                            x = x + 1;
                            a = 1;
                        }
                    }
                }
                break;
            case "x-":
                for (int y = room.ya; y >= room.yi; y--) {
                    b = b + 1;
                    if (b % 3 == 0) {
                        y = y - 1;
                    }
                    h = h + 1;
                    int l = 0;
                    for (int x = room.xa; x >= room.xi; x--) {
                        room.level.setBlock(new Vector3(x, y, room.zi - 1), Block.get(35, key[h][l]));
                        a = a + 1;
                        l = l + 1;
                        if (a == 3) {
                            x = x + 1;
                            a = 1;
                        }
                    }
                }
                break;
            case "z+":
                for (int y = room.ya; y >= room.yi; y--) {
                    ++b;
                    int l = 0;
                    for (int z = room.zi; z <= room.za; z++) {
                        if (key[h][l] == 0) {
                            room.level.setBlock(new Vector3(room.xi - 1, y, z), Block.get(0, 0));
                        } else {
                            room.level.setBlock(new Vector3(room.xi - 1, y, z), Block.get(35, key[h][l]));
                        }
                        ++a;
                        if (a == 3) {
                            ++z;
                            a = 0;
                        }
                        l++;
                    }
                    if (b == 3) {
                        --y;
                        b = 0;
                    }
                    h++;
                }
                break;
            case "z-":
                for (int y = room.ya; y >= room.yi; y--) {
                    b = b + 1;
                    if (b % 3 == 0) {
                        y = y - 1;
                    }
                    h = h + 1;
                    int l = 0;
                    for (int z = room.za; z >= room.zi; z--) {
                        room.level.setBlock(new Vector3(room.xi - 1, y, z), Block.get(35, key[h][l]));
                        a = a + 1;
                        l = l + 1;
                        if (a % 3 == 0) {
                            z = z + 1;
                        }
                    }
                }
                break;
        }
        finishBuild();
    }
}
