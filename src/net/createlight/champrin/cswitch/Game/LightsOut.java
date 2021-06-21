package net.createlight.champrin.cswitch.Game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.cswitch.Room;

import java.util.Random;

public class LightsOut extends Game implements Listener {

    public LightsOut(Room room) {
        super(room);
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event) {
        if (this.room.finish) return;
        if (this.game_type.equals("LightsOut")) {
            if (this.room.game != 1) return;
            Block block = event.getBlock();
            Player player = event.getPlayer();
            if (this.room.isInGame(player)) {
                if (this.room.isInArena(block)) {
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
        int x = (int) Math.round(Math.floor(block.x));
        int y = (int) Math.round(Math.floor(block.y));
        int z = (int) Math.round(Math.floor(block.z));
        Level level = block.level;
        Block b;

        if (block.getDamage() == 5) {
            if (this.room.rank - 1 > 0) {
                this.room.rank = this.room.rank - 1;
            }
            level.setBlock(block, Block.get(35, 15));

        } else if (block.getDamage() == 15) {
            this.room.rank = this.room.rank + 1;
            level.setBlock(block, Block.get(35, 5));
        }

        b = level.getBlock(new Vector3(x, y + 1, z));
        if (b.getId() == 35) {

            if (b.getDamage() == 15) {
                this.room.rank = this.room.rank + 1;
                level.setBlock(b, Block.get(35, 5));
            } else if (b.getDamage() == 5) {
                if (this.room.rank - 1 > 0) {
                    this.room.rank = this.room.rank - 1;
                }
                level.setBlock(b, Block.get(35, 15));
            }
        }

        b = level.getBlock(new Vector3(x, y - 1, z));
        if (b.getId() == 35) {
            if (b.getDamage() == 15) {
                this.room.rank = this.room.rank + 1;
                level.setBlock(b, Block.get(35, 5));
            } else if (b.getDamage() == 5) {
                if (this.room.rank - 1 > 0) {
                    this.room.rank = this.room.rank - 1;
                }
                level.setBlock(b, Block.get(35, 15));
            }
        }

        if (room.direction.equals("x+") || room.direction.equals("x-")) {

            b = level.getBlock(new Vector3(x + 1, y, z));
            if (b.getId() == 35) {
                if (b.getDamage() == 15) {
                    this.room.rank = this.room.rank + 1;
                    level.setBlock(b, Block.get(35, 5));
                } else if (b.getDamage() == 5) {
                    if (this.room.rank - 1 >= 0) {
                        this.room.rank = this.room.rank - 1;
                    }
                    level.setBlock(b, Block.get(35, 15));
                }
            }

            b = level.getBlock(new Vector3(x - 1, y, z));
            if (b.getId() == 35) {
                if (b.getDamage() == 15) {
                    this.room.rank = this.room.rank + 1;
                    level.setBlock(b, Block.get(35, 5));
                } else if (b.getDamage() == 5) {
                    if (this.room.rank - 1 > 0) {
                        this.room.rank = this.room.rank - 1;
                    }
                    level.setBlock(b, Block.get(35, 15));
                }
            }

        } else {

            b = level.getBlock(new Vector3(x, y, z + 1));
            if (b.getId() == 35) {
                if (b.getDamage() == 15) {
                    this.room.rank = this.room.rank + 1;
                    level.setBlock(b, Block.get(35, 5));
                } else if (b.getDamage() == 5) {
                    if (this.room.rank - 1 > 0) {
                        this.room.rank = this.room.rank - 1;
                    }
                    level.setBlock(b, Block.get(35, 15));
                }
            }

            b = level.getBlock(new Vector3(x, y, z - 1));
            if (b.getId() == 35) {
                if (b.getDamage() == 15) {
                    this.room.rank = this.room.rank + 1;
                    level.setBlock(b, Block.get(35, 5));
                } else if (b.getDamage() == 5) {
                    if (this.room.rank - 1 > 0) {
                        this.room.rank = this.room.rank - 1;
                    }
                    level.setBlock(b, Block.get(35, 15));
                }
            }
        }
    }
    @Override
    public void checkFinish() {
        if (this.room.rank >= area) {
            this.room.finish=true;
        }
    }@Override
    public void madeArena() {
        switch (room.direction) {
            case "x+":
            case "x-":
                for (int x = room.xi; x <= room.xa; x++) {
                    for (int y = room.yi; y <= room.ya; y++) {
                        int num = new Random().nextInt(2);
                        int mate = (num == 1 ? 5 : 15);
                        if (mate == 5) {
                            this.room.rank = room.rank + 1;
                        }
                        Block block = Block.get(35, mate);
                        room.level.setBlock(new Vector3(x, y, room.zi), block);
                    }
                }
                break;
            case "z+":
            case "z-":
                for (int z = room.zi; z <= room.za; z++) {
                    for (int y = room.yi; y <= room.ya; y++) {
                        int num = new Random().nextInt(2);
                        int mate = (num == 1 ? 5 : 15);
                        if (mate == 5) {
                            this.room.rank = room.rank + 1;
                        }
                        Block block = Block.get(35, mate);
                        room.level.setBlock(new Vector3(room.xi, y, z), block);
                    }
                }
                break;
        }
        finishBuild();
    }
}