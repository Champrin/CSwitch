package xyz.caibin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import xyz.caibin.cswitch.Room;

public class LightsOut extends Game  implements Listener {

    public LightsOut(Room plugin) {
        this.game_type = plugin.game_type;
        this.plugin = plugin;
        this.area = (int) this.plugin.data.get("area");
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.plugin.finish) return;
        if (this.game_type.equals("LightsOut")) {
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
                    updateBlock(block);
                    checkFinish();
                }
            }
        }
    }

    /**
     * mate:5绿色羊毛 15黑色羊毛
     **/
    public void updateBlock(Block block) {
        if (block.getId() != 35) return;
        String direction = (String) this.plugin.data.get("direction");
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));
        Level level = block.level;
        Block b;

        if (block.getDamage() == 5) {
            if (this.plugin.rank - 1 > 0) {
                this.plugin.rank = this.plugin.rank - 1;
            }
            level.setBlock(block, Block.get(35, 15));

        } else if (block.getDamage() == 15) {
            this.plugin.rank = this.plugin.rank + 1;
            level.setBlock(block, Block.get(35, 5));
        }

        b = level.getBlock(new Vector3(x, y + 1, z));
        if (b.getId() == 35) {

            if (b.getDamage() == 15) {
                this.plugin.rank = this.plugin.rank + 1;
                level.setBlock(b, Block.get(35, 5));
            } else if (b.getDamage() == 5) {
                if (this.plugin.rank - 1 > 0) {
                    this.plugin.rank = this.plugin.rank - 1;
                }
                level.setBlock(b, Block.get(35, 15));
            }
        }

        b = level.getBlock(new Vector3(x, y - 1, z));
        if (b.getId() == 35) {
            if (b.getDamage() == 15) {
                this.plugin.rank = this.plugin.rank + 1;
                level.setBlock(b, Block.get(35, 5));
            } else if (b.getDamage() == 5) {
                if (this.plugin.rank - 1 > 0) {
                    this.plugin.rank = this.plugin.rank - 1;
                }
                level.setBlock(b, Block.get(35, 15));
            }
        }

        if (direction.equals("x+") || direction.equals("x-")) {

            b = level.getBlock(new Vector3(x + 1, y, z));
            if (b.getId() == 35) {
                if (b.getDamage() == 15) {
                    this.plugin.rank = this.plugin.rank + 1;
                    level.setBlock(b, Block.get(35, 5));
                } else if (b.getDamage() == 5) {
                    if (this.plugin.rank - 1 >= 0) {
                        this.plugin.rank = this.plugin.rank - 1;
                    }
                    level.setBlock(b, Block.get(35, 15));
                }
            }

            b = level.getBlock(new Vector3(x - 1, y, z));
            if (b.getId() == 35) {
                if (b.getDamage() == 15) {
                    this.plugin.rank = this.plugin.rank + 1;
                    level.setBlock(b, Block.get(35, 5));
                } else if (b.getDamage() == 5) {
                    if (this.plugin.rank - 1 > 0) {
                        this.plugin.rank = this.plugin.rank - 1;
                    }
                    level.setBlock(b, Block.get(35, 15));
                }
            }

        } else {

            b = level.getBlock(new Vector3(x, y, z + 1));
            if (b.getId() == 35) {
                if (b.getDamage() == 15) {
                    this.plugin.rank = this.plugin.rank + 1;
                    level.setBlock(b, Block.get(35, 5));
                } else if (b.getDamage() == 5) {
                    if (this.plugin.rank - 1 > 0) {
                        this.plugin.rank = this.plugin.rank - 1;
                    }
                    level.setBlock(b, Block.get(35, 15));
                }
            }

            b = level.getBlock(new Vector3(x, y, z - 1));
            if (b.getId() == 35) {
                if (b.getDamage() == 15) {
                    this.plugin.rank = this.plugin.rank + 1;
                    level.setBlock(b, Block.get(35, 5));
                } else if (b.getDamage() == 5) {
                    if (this.plugin.rank - 1 > 0) {
                        this.plugin.rank = this.plugin.rank - 1;
                    }
                    level.setBlock(b, Block.get(35, 15));
                }
            }
        }
    }

    public void checkFinish() {
        if (this.plugin.rank >= area) {
            this.plugin.finish=true;
        }
    }
}